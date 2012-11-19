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

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.ui.Console;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Unit Testing for the {@link RoboLog} Helper class. This is also using the
 * Console acitivity to check for the output
 * 
 * @author Frederik Petersen
 * 
 */
@SuppressWarnings("rawtypes")
public class RoboLogTest extends ActivityInstrumentationTestCase2 {

	private Console consoleActivity;

	@SuppressWarnings("unchecked")
	public RoboLogTest() {
		super(Console.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		consoleActivity = (Console) getActivity();
	}

	/**
	 * Test if RoboLog message is correctly logged to Console UI activity
	 */
	public void testRoboLogLogging() {
		getInstrumentation().waitForIdleSync();
		TextView consoleTextView = (TextView) consoleActivity.findViewById(R.id.consoleTextView);
		String findThis = "Logged!";

		getInstrumentation().waitForIdleSync();

		RoboLog.log(consoleActivity, findThis, true);

		getInstrumentation().waitForIdleSync();

		String allText = consoleTextView.getText().toString();
		assertTrue(allText.contains(findThis));
	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		consoleActivity.finish();
		super.tearDown();
	}

}
