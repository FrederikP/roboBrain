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
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.XMLParserHelper;

/**
 * Singleton factory that allows creating a mapping of robots to behaviors from
 * xml file on sd card.
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorMappingFactory {

	private static BehaviorMappingFactory instance;
	private static final String ns = null;

	private BehaviorMappingFactory() {

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
	public Map<String, List<String>> createMappings(InputStream in) {
		try {
			if (in == null) {
				in = new FileInputStream(
						ExternalStorageManager
								.getBehaviorMappingFile(RobotService
										.getInstance()));
			}
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			return readMappings(parser);
		} catch (XmlPullParserException e) {
			if (e.getMessage().contains("Premature end of document")) {
				RoboLog.log(RobotService.getInstance(),
						"Empty behaviormapping.xml on sd card. Please configure...");
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
		// TODO Exception handling
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
	private Map<String, List<String>> readMappings(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Map<String, List<String>> behaviorMapping = new HashMap<String, List<String>>();
		parser.require(XmlPullParser.START_TAG, ns, "behaviormapping");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("robot")) {
				addRobotToMapping(parser, behaviorMapping);
			} else {
				XMLParserHelper.skip(parser);
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
			Map<String, List<String>> behaviorMapping)
			throws XmlPullParserException, IOException {

		List<String> behaviorNamesForRobot = new ArrayList<String>();
		parser.require(XmlPullParser.START_TAG, ns, "robot");
		String robotName = parser.getAttributeValue(ns, "name");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("behavior")) {
				addBehaviorName(behaviorNamesForRobot, parser);
			} else {
				XMLParserHelper.skip(parser);
			}
		}

		behaviorMapping.put(robotName, behaviorNamesForRobot);

	}

	/**
	 * Adds one behavior name to the mapping
	 * 
	 * @param behaviorNamesForRobot
	 *            List of names that the new name is added to
	 * @param parser
	 *            The parser for the behaviormapping.xml file
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void addBehaviorName(List<String> behaviorNamesForRobot,
			XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "behavior");

		behaviorNamesForRobot.add(parser.getAttributeValue(ns, "name"));
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "behavior");

	}

	public static BehaviorMappingFactory getInstance() {
		if (instance == null) {
			instance = new BehaviorMappingFactory();
		}
		return instance;
	}

}
