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

import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ScrollView;
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

	private static final String TESTFILL = "Testgsjdslgsfgsdlfgsdiccfgsdio\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n"
			+ "Testgsjdslgsfgsdlfgsdiccfgsdi\n" + "Testgsjdslgsfgsdlfgsdiccfgsd\n";

	private Console consoleActivity;

	public ConsoleTest() {
		super(Console.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		consoleActivity = getActivity();
		System.setProperty(consoleActivity.getString(R.string.envvar_testing), "true");
	}

	/**
	 * Test if the timestamp is correctly created and formatted
	 */
	public void testFormattedTimeStamp() {
		String date = consoleActivity.getFormattedCurrentTimestamp();
		assertNotNull(date);
		assertTrue(date.contains("-->"));
	}

	/**
	 * Test if text is correctly appended to the console, and if scrolling down
	 * works.
	 */
	public void testTextAppendWithoutIntent() {
		TextView consoleTextView = (TextView) consoleActivity.findViewById(R.id.consoleTextView);
		String findThis = "INTHEMIDDLE";
		consoleActivity.appendText(TESTFILL);
		getInstrumentation().waitForIdleSync();
		consoleActivity.appendText(findThis);
		getInstrumentation().waitForIdleSync();
		consoleActivity.appendText(TESTFILL);
		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(findThis));

		assertTrue(consoleActivity.isScrolledDown());
	}

	/**
	 * Test if text is correctly appended to the console, and if scrolling down
	 * works.
	 */
	public void testTextScrolling() {
		consoleActivity.appendText(TESTFILL);
		double waitedSecs = 0;
		while (!consoleActivity.isScrolledDown() && waitedSecs < 10) {
			Helper.sleepMillis(100);
			getInstrumentation().waitForIdleSync();
			waitedSecs = waitedSecs + 0.1;
		}
		Helper.sleepMillis(5000);
		assertTrue(consoleActivity.isScrolledDown());

		consoleActivity.appendText(TESTFILL);
		waitedSecs = 0;
		while (!consoleActivity.isScrolledDown() && waitedSecs < 10) {
			Helper.sleepMillis(100);
			getInstrumentation().waitForIdleSync();
			waitedSecs = waitedSecs + 0.1;
		}
		Helper.sleepMillis(1000);
		assertTrue(consoleActivity.isScrolledDown());

		consoleActivity.appendText(TESTFILL);
		waitedSecs = 0;
		while (!consoleActivity.isScrolledDown() && waitedSecs < 10) {
			Helper.sleepMillis(1000);
			getInstrumentation().waitForIdleSync();
			waitedSecs = waitedSecs + 0.1;
		}

		Helper.sleepMillis(2000);

		assertTrue(consoleActivity.isScrolledDown());
		Helper.sleepMillis(1000);

		getInstrumentation().waitForIdleSync();

		final ScrollView textScroll = (ScrollView) consoleActivity
				.findViewById(R.id.consoleScroller);
		final Set<Integer> interThreadHelperSet = new HashSet<Integer>();
		textScroll.post(new Runnable() {

			public void run() {
				textScroll.scrollTo(0, textScroll.getTop());
				textScroll.fullScroll(ScrollView.FOCUS_UP);
				interThreadHelperSet.add(1);

			}
		});

		waitedSecs = 0;
		while (!interThreadHelperSet.contains(1) && waitedSecs < 20) {
			Helper.sleepMillis(100);
			getInstrumentation().waitForIdleSync();
			waitedSecs = waitedSecs + 0.1;
		}

		Helper.sleepMillis(1000);

		assertFalse(consoleActivity.isScrolledDown());

		consoleActivity.appendText(TESTFILL);
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(1000);
		consoleActivity.appendText(TESTFILL);
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(1000);
		consoleActivity.appendText(TESTFILL);
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(1000);

		assertFalse(consoleActivity.isScrolledDown());
	}

	/**
	 * Test if text is correctly appended to the console when sending the
	 * intent, and if scrolling down works.
	 */
	public void testTextAppendWithIntent() {
		TextView consoleTextView = (TextView) consoleActivity.findViewById(R.id.consoleTextView);
		String appendMe = "AppendMe";
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, appendMe);
		consoleActivity.sendBroadcast(cIntent);

		double waitedSecs = 0;
		String allText = consoleTextView.getText().toString();
		while (!allText.contains(appendMe) && waitedSecs < 10) {
			Helper.sleepMillis(100);
			allText = consoleTextView.getText().toString();
			getInstrumentation().waitForIdleSync();
			waitedSecs = waitedSecs + 0.1;
		}

		assertTrue(allText.contains(appendMe));

	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		consoleActivity.finish();
		System.setProperty(consoleActivity.getString(R.string.envvar_testing), "");
		super.tearDown();
	}

}
