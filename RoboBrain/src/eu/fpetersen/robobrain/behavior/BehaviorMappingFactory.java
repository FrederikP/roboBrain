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
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.XMLParserHelper;

public class BehaviorMappingFactory {

	private static BehaviorMappingFactory instance;
	private static final String ns = null;

	private BehaviorMappingFactory() {

	}

	public Map<String, List<String>> createMappings() {
		InputStream in = null;
		try {
			in = new FileInputStream(
					ExternalStorageManager.getBehaviorMappingFile());
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			return readMappings(parser);
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
				behaviorNamesForRobot.add(parser.getAttributeValue(ns, "name"));
			} else {
				XMLParserHelper.skip(parser);
			}
		}

		behaviorMapping.put(robotName, behaviorNamesForRobot);

	}

	public static BehaviorMappingFactory getInstance() {
		if (instance == null) {
			instance = new BehaviorMappingFactory();
		}
		return instance;
	}

}
