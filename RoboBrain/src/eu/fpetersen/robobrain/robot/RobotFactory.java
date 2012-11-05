package eu.fpetersen.robobrain.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.util.Xml;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.XMLParserHelper;

/**
 * Singleton factory to create {@link Robot}s from xml configurations
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotFactory {

	private static RobotFactory instance;

	private RobotService service;

	/**
	 * Namespace for xml parser. Is null if no namespace is used.
	 */
	private static final String ns = null;

	private RobotFactory(RobotService service) {
		this.service = service;
	}

	public static RobotFactory getInstance(RobotService service) {
		if (instance == null) {
			instance = new RobotFactory(service);
		} else if (instance.getService() != service) {
			instance.setService(service);
		}
		return instance;
	}

	private Service getService() {
		return service;
	}

	private void setService(RobotService service) {
		this.service = service;
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
		Robot robot = new Robot(service, address, robotName);
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
		RobotPartFactory rbFac = RobotPartFactory.getInstance();
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
		File robotsXmlDir = ExternalStorageManager.getRobotsXmlDir(RobotService
				.getInstance());
		try {
			if (robotsXmlDir != null) {
				for (File robotXml : robotsXmlDir.listFiles()) {
					if (robotXml.getAbsolutePath().endsWith(".xml")) {
						Robot newRobot = createRobotFromXML(robotXml);
						robots.put(newRobot.getName(), newRobot);
					}
				}
			} else {
				RoboLog.log(RobotService.getInstance(),
						"Robots XML Dir could not be accessed on sd card!");
			}
		} catch (NullPointerException e) {
			RoboLog.log(RobotService.getInstance(),
					"SDCard could not be accessed. Please check if SDCard is mounted.");
		}
		return robots;
	}

	/**
	 * Creates Robot without any parts. For simple unit testing
	 */
	public Robot createSimpleRobot(String name) {
		return new Robot(service, "----------", name);
	}

}
