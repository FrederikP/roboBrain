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
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.RoboBrainFactory;
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.XMLParserHelper;

/**
 * Singleton factory to create {@link Robot}s from xml configurations
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotFactory extends RoboBrainFactory {

	private static RobotFactory instance;

	/**
	 * Namespace for xml parser. Is null if no namespace is used.
	 */
	private static final String ns = null;

	private RobotFactory(RobotService service) {
		super(service);
	}

	public static RobotFactory getInstance(RobotService service) {
		if (instance == null) {
			instance = new RobotFactory(service);
		} else if (instance.getService() != service) {
			instance.setService(service);
		}
		return instance;
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
			return createRobotFromXML(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// TODO Exception handling
		return null;
	}

	public Robot createRobotFromXML(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			return readRobot(parser);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// TODO Exception handling
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
	private Robot readRobot(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "robot");
		String address = parser.getAttributeValue(ns, "address");
		String robotName = parser.getAttributeValue(ns, "name");
		Robot robot = new Robot(getService(), address, robotName);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("parts")) {
				addParts(parser, robot);
			} else {
				XMLParserHelper.skip(parser);
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
	private void addParts(XmlPullParser parser, Robot robot)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "parts");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("part")) {
				readInPart(parser, robot);

			} else {
				XMLParserHelper.skip(parser);
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
	private void readInPart(XmlPullParser parser, Robot robot)
			throws XmlPullParserException, IOException {
		RobotPartFactory rbFac = RobotPartFactory.getInstance(getService());
		parser.require(XmlPullParser.START_TAG, ns, "part");
		String type = parser.getAttributeValue(ns, "type");
		String id = parser.getAttributeValue(ns, "id");
		robot.addPart(id, rbFac.createRobotPart(type, robot));
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "part");
	}

	/**
	 * Creates robots from all xml files in the /robobrain/robots folder
	 * 
	 * @return Map of Robots with names
	 */
	public Map<String, Robot> createRobots() {
		Map<String, Robot> robots = new HashMap<String, Robot>();
		File robotsXmlDir = ExternalStorageManager
				.getRobotsXmlDir(getService());
		try {
			if (robotsXmlDir != null) {
				for (File robotXml : robotsXmlDir.listFiles()) {
					if (robotXml.getAbsolutePath().endsWith(".xml")) {
						Robot newRobot = createRobotFromXML(robotXml);
						robots.put(newRobot.getName(), newRobot);
					}
				}
			} else {
				RoboLog.log(getService(),
						"Robots XML Dir could not be accessed on sd card!");
			}
		} catch (NullPointerException e) {
			RoboLog.log(getService(),
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
