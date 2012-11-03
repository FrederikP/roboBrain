package eu.fpetersen.robobrain.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Helper Class for XML Parsing. Central place to access methods that are needed
 * in different Factories.
 * 
 * @author Frederik Petersen
 * 
 */
public class XMLParserHelper {

	/**
	 * Skip the current tag
	 * 
	 * @param parser
	 *            Parser of which the next tag is to be skipped
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static void skip(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
