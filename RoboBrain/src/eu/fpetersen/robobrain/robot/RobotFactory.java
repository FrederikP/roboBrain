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
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.XMLParserHelper;

public class RobotFactory {

	private static RobotFactory instance;

	private Service service;

	private static final String ns = null;

	private RobotFactory(Service service) {
		this.service = service;
	}

	public static RobotFactory getInstance(Service service) {
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

	private void setService(Service service) {
		this.service = service;
	}

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

	private Robot readRobot(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "robot");
		String address = parser.getAttributeValue(ns, "address");
		String robotName = parser.getAttributeValue(ns, "name");
		Robot robot = new Robot(address, robotName);
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

	private void readInPart(XmlPullParser parser, Robot robot)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "part");
		String type = parser.getAttributeValue(ns, "type");
		String id = parser.getAttributeValue(ns, "id");
		if (type.matches("motor")) {
			robot.addPart(id, new Motor(robot));
		} else if (type.matches("proxsensor")) {
			robot.addPart(id, new ProximitySensor(robot));
		} else if (type.matches("servo")) {
			robot.addPart(id, new Servo(robot));
		} else if (type.matches("rgbled")) {
			robot.addPart(id, new RGBLED(robot));
		}
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, "part");
	}

	public Map<String, Robot> createRobots() {
		Map<String, Robot> robots = new HashMap<String, Robot>();
		File robotsXmlDir = ExternalStorageManager.getRobotsXmlDir();
		if (robotsXmlDir != null) {
			for (File robotXml : robotsXmlDir.listFiles()) {
				if (robotXml.getAbsolutePath().endsWith(".xml")) {
					Robot newRobot = createRobotFromXML(robotXml);
					robots.put(newRobot.getName(), newRobot);
				}
			}
		} else {
			RoboLog.log("Robots XML Dir could not be accessed on sd card!");
		}
		return robots;
	}

}
