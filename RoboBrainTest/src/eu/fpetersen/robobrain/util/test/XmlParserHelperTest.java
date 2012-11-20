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
package eu.fpetersen.robobrain.util.test;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources.NotFoundException;
import android.test.AndroidTestCase;
import android.util.Xml;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.util.XmlParserHelper;

/**
 * 
 * Tests {@link XmlParserHelper}
 * 
 * @author Frederik Petersen
 * 
 */
public class XmlParserHelperTest extends AndroidTestCase {

	private XmlParserHelper mXmlParserHelper;

	@Override
	protected void setUp() throws Exception {
		mXmlParserHelper = new XmlParserHelper();
		super.setUp();
	}

	public void testSkipping() throws NotFoundException, XmlPullParserException, IOException {

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(getContext().getResources().openRawResource(R.raw.test), null);
		parser.nextTag();
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "eins");
		parser.nextTag();
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "zwei");
		mXmlParserHelper.skip(parser);
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "drei");
		parser.nextTag();
		boolean error = false;
		try {
			mXmlParserHelper.skip(parser);
		} catch (IllegalStateException e) {
			error = true;
		}

		assertTrue(error);

	}

}
