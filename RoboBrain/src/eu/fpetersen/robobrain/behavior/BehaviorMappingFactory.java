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
package eu.fpetersen.robobrain.behavior;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.RoboBrainFactory;
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.XmlParserHelper;

/**
 * Singleton factory that allows creating a mapping of robots to behaviors from
 * xml file on sd card.
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorMappingFactory extends RoboBrainFactory {

	private static BehaviorMappingFactory sInstance;
	private static final String NS = null;

	private BehaviorMappingFactory(RobotService service) {
		super(service);
	}

	/**
	 * Uses behavior.xml in [sdcard]/robobrain and parses robot and it's
	 * behaviors from the file.
	 * 
	 * @param in
	 *            The Input Stream to read the Mapping from, enter null for
	 *            standard sd card file
	 * 
	 * @return Mapping of robotname to behaviornames.
	 */
	public Map<String, List<BehaviorInitializer>> createMappings(InputStream in) {
		try {
			if (in == null) {
				in = new FileInputStream(
						ExternalStorageManager.getBehaviorMappingFile(getService()));
			}
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			return readMappings(parser);
		} catch (XmlPullParserException e) {
			if (e.getMessage().contains("Premature end of document")) {
				RoboLog.alertWarning(getService(),
						"Empty behaviormapping.xml on sd card. Please configure...");
			} else {
				RoboLog.alertError(getService(),
						"Error parsing your behaviormapping.xml. Is it valid xml?");
			}
		} catch (Exception e) {
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
	 * Does the actual xml parsing
	 * 
	 * @param parser
	 *            The parser for the behaviormapping.xml file
	 * @return The mapping of robots and behaviors
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private Map<String, List<BehaviorInitializer>> readMappings(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Map<String, List<BehaviorInitializer>> behaviorMapping = new HashMap<String, List<BehaviorInitializer>>();
		parser.require(XmlPullParser.START_TAG, NS, "behaviormapping");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("robot")) {
				addRobotToMapping(parser, behaviorMapping);
			} else {
				XmlParserHelper.skip(parser);
			}
		}

		return behaviorMapping;

	}

	/**
	 * Adds one Robot to the mapping
	 * 
	 * @param parser
	 *            The parser for the behaviormapping.xml file
	 * @param behaviorMapping
	 *            The mapping of robots and behaviors
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void addRobotToMapping(XmlPullParser parser,
			Map<String, List<BehaviorInitializer>> behaviorMapping) throws XmlPullParserException,
			IOException {

		List<BehaviorInitializer> behaviorInitializersForRobot = new ArrayList<BehaviorInitializer>();
		parser.require(XmlPullParser.START_TAG, NS, "robot");
		String robotName = parser.getAttributeValue(NS, "name");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("behavior")) {
				addBehaviorInitializer(behaviorInitializersForRobot, parser);
			} else {
				XmlParserHelper.skip(parser);
			}
		}

		behaviorMapping.put(robotName, behaviorInitializersForRobot);

	}

	/**
	 * Adds one behavior name to the mapping
	 * 
	 * @param behaviorInitializersForRobot
	 *            List of names that the new name is added to
	 * @param parser
	 *            The parser for the behaviormapping.xml file
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void addBehaviorInitializer(List<BehaviorInitializer> behaviorInitializersForRobot,
			XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NS, "behavior");

		behaviorInitializersForRobot.add(new BehaviorInitializer(parser.getAttributeValue(NS,
				"name"), parser.getAttributeValue(NS, "speechName")));
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, NS, "behavior");

	}

	public static BehaviorMappingFactory getInstance(RobotService service) {
		if (sInstance == null) {
			sInstance = new BehaviorMappingFactory(service);
		} else if (sInstance.getService() != service) {
			sInstance.setService(service);
		}
		return sInstance;
	}

}
