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
package eu.fpetersen.robobrain.ui.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.MoreAsserts;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.ui.Console;

/**
 * Unit Testing of the {@link Console} activity.
 * 
 * @author Frederik Petersen
 * 
 */
public class ConsoleTest extends ActivityInstrumentationTestCase2<Console> {

	private Console consoleActivity;

	public ConsoleTest() {
		super(Console.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		consoleActivity = getActivity();
	}

	/**
	 * Test if the timestamp is correctly created and formatted
	 */
	public void testFormattedTimeStamp() {
		String date = consoleActivity.getFormattedCurrentTimestamp();
		assertNotNull(date);
		String pattern = consoleActivity
				.getString(R.string.console_timestamp_format);
		pattern = pattern.replaceAll("(\\w)", "\\\\\\d");
		pattern = pattern.replaceAll("(\\s)", "\\\\\\s");
		MoreAsserts.assertMatchesRegex(pattern, date);
	}

	/**
	 * Test if text is correctly appended to the console, and if scrolling down
	 * works.
	 */
	public void testTextAppendWithoutIntent() {
		TextView consoleTextView = (TextView) consoleActivity
				.findViewById(R.id.consoleTextView);
		String findThis = "INTHEMIDDLE";
		for (int i = 0; i < 30; i++) {
			Helper.sleepMillis(200);
			consoleActivity.appendText("Test " + i);
			if (i == 15) {
				consoleActivity.appendText(findThis);
			}
		}
		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(findThis));

		assertTrue(consoleActivity.isScrolledDown());
	}

	/**
	 * Test if text is correctly appended to the console when sending the
	 * intent, and if scrolling down works.
	 */
	public void testTextAppendWithIntent() {
		TextView consoleTextView = (TextView) consoleActivity
				.findViewById(R.id.consoleTextView);
		String appendMe = "AppendMe";
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, appendMe);
		consoleActivity.sendBroadcast(cIntent);

		Helper.sleepMillis(500);
		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(appendMe));

	}

}
