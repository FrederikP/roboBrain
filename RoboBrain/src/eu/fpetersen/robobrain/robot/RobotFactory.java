/*******************************************************************************
 * RoboBrain - Control your Arduino Robots per Android Device
 * Copyright (c) 2012 Frederik Petersen.
 * All rights reserved.
 * 
 * This file is part of RoboBrain.
 * 
 *     RoboBrain is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     RoboBrain is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with RoboBrain.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Frederik Petersen - Project Owner, initial Implementation
 ******************************************************************************/
package eu.fpetersen.robobrain.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.RoboBrainFactory;
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.XmlParserHelper;

/**
 * Singleton factory to create {@link Robot}s from xml configurations
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotFactory extends RoboBrainFactory {

	private static RobotFactory sInstance;

	/**
	 * Namespace for xml parser. Is null if no namespace is used.
	 */
	private static final String NS = null;

	private RobotFactory(RobotService service) {
		super(service);
	}

	public static RobotFactory getInstance(RobotService service) {
		if (sInstance == null) {
			sInstance = new RobotFactory(service);
		} else if (sInstance.getService() != service) {
			sInstance.setService(service);
		}
		return sInstance;
	}

	/**
	 * Create robot from specified XML file
	 * 
	 * @param robotXml
	 *            Robot configuration xml file
	 * @return The freshly created robot
	 */
	private Robot createRobotFromXML(File robotXml) {
		InputStream in = null;
		try {
			in = new FileInputStream(robotXml);
			return createRobotFromXml(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		RoboLog.alertError(getService(),
				"Something went wrong while building Robot. Check for valid and accessable conf files.");
		return null;
	}

	public Robot createRobotFromXml(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			return readRobot(parser);
		} catch (Exception e) {
			RoboLog.alertError(getService(),
					"Something went wrong while building Robot. Check for valid and accessable conf files.");
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Creates the robot with the given xml parser
	 * 
	 * @param parser
	 *            Parser of the robot's configuration xml file
	 * @return Freshly created robot
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Robot readRobot(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, "robot");
		String address = parser.getAttributeValue(NS, "address");
		String robotName = parser.getAttributeValue(NS, "name");
		Robot robot = new Robot(getService(), address, robotName);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("parts")) {
				addParts(parser, robot);
			} else {
				XmlParserHelper.skip(parser);
			}
		}

		return robot;
	}

	/**
	 * Add parts to the robot, as configured in xml file being parsed
	 * 
	 * @param parser
	 *            Parser of the robot's configuration xml file
	 * @param robot
	 *            The robot the Parts are added to
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void addParts(XmlPullParser parser, Robot robot) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, NS, "parts");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("part")) {
				readInPart(parser, robot);

			} else {
				XmlParserHelper.skip(parser);
			}
		}
	}

	/**
	 * Adds one part to the robot
	 * 
	 * @param parser
	 *            Parser of the robot's configuration xml file
	 * @param robot
	 *            The robot the part is added to
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void readInPart(XmlPullParser parser, Robot robot) throws XmlPullParserException,
			IOException {
		RobotPartFactory rbFac = RobotPartFactory.getInstance(getService());
		parser.require(XmlPullParser.START_TAG, NS, "part");
		String type = parser.getAttributeValue(NS, "type");
		String id = parser.getAttributeValue(NS, "id");
		Map<String, Character> flags = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("flags")) {
				flags = readInFlags(parser);
			} else {
				XmlParserHelper.skip(parser);
			}
		}
		RobotPartInitializer initializer = new RobotPartInitializer(id, robot, flags);
		robot.addPart(id, rbFac.createRobotPart(type, initializer));
		try {
			parser.require(XmlPullParser.END_TAG, NS, "part");
		} catch (XmlPullParserException e) {
			// parser still on last Flag tag -> jump to next
			parser.nextTag();
			parser.require(XmlPullParser.END_TAG, NS, "part");
		}

	}

	/**
	 * Read in flags with their ids
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private Map<String, Character> readInFlags(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, NS, "flags");
		Map<String, Character> flags = new HashMap<String, Character>();
		try {
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				if (name.equals("flag")) {
					readInFlag(flags, parser);
				} else {
					XmlParserHelper.skip(parser);
				}
			}
			parser.require(XmlPullParser.END_TAG, NS, "flags");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return flags;

	}

	/**
	 * @param flags
	 * @param parser
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void readInFlag(Map<String, Character> flags, XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, "flag");
		String id = parser.getAttributeValue(NS, "id");
		char flag = parser.getAttributeValue(NS, "flag").charAt(0);
		flags.put(id, flag);
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, NS, "flag");
	}

	/**
	 * Creates robots from all xml files in the /robobrain/robots folder
	 * 
	 * @return Map of Robots with names
	 */
	public Map<String, Robot> createRobots() {
		Map<String, Robot> robots = new HashMap<String, Robot>();
		File robotsXmlDir = ExternalStorageManager.getRobotsXmlDir(getService());
		try {
			if (robotsXmlDir != null) {
				for (File robotXml : robotsXmlDir.listFiles()) {
					if (robotXml.getAbsolutePath().endsWith(".xml")) {
						Robot newRobot = createRobotFromXML(robotXml);
						robots.put(newRobot.getName(), newRobot);
					}
				}
			} else {
				RoboLog.alertError(getService(), "Robots XML Dir could not be accessed on sd card!");
			}
		} catch (NullPointerException e) {
			RoboLog.alertError(getService(),
					"SDCard could not be accessed. Please check if SDCard is mounted.");
		}
		return robots;
	}

	/**
	 * Creates Robot without any parts. For simple unit testing
	 */
	public Robot createSimpleRobot(String name) {
		return new Robot(getService(), "----------", name);
	}

}
