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
package eu.fpetersen.robobrain.test.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.speech.SpeechResultManager;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.ui.About;
import eu.fpetersen.robobrain.ui.Console;
import eu.fpetersen.robobrain.ui.Starter;

/**
 * @author Frederik Petersen
 * 
 */
public class BasicUsageTest extends ActivityInstrumentationTestCase2<Starter> {

	private Starter starterActivity;
	private Activity consoleActivity;

	/**
	 * Sets test up to use {@link Starter} activity
	 */
	public BasicUsageTest() {
		super(Starter.class);
	}

	@Override
	protected void setUp() throws Exception {
		starterActivity = getActivity();

		System.setProperty(starterActivity.getString(R.string.envvar_testing), "true");

		getInstrumentation().waitForIdleSync();

		super.setUp();
	}

	public void testTurningOnAndOff() {

		starterActivity.removeAllOpenDialogs();
		getInstrumentation().waitForIdleSync();

		// ----Open Console activity-----
		ActivityMonitor amConsole = getInstrumentation().addMonitor(Console.class.getName(), null,
				false);

		getInstrumentation().invokeMenuActionSync(starterActivity, R.id.console_menu_item, 0);

		consoleActivity = getInstrumentation().waitForMonitorWithTimeout(amConsole, 10000);
		assertEquals(true, getInstrumentation().checkMonitorHit(amConsole, 1));

		// ----Go back to Starter activity-----
		ActivityMonitor amStarter = getInstrumentation().addMonitor(Starter.class.getName(), null,
				false);

		getInstrumentation().invokeMenuActionSync(consoleActivity, R.id.starter_menu_item, 0);

		Activity starterActivity2 = getInstrumentation()
				.waitForMonitorWithTimeout(amStarter, 10000);
		assertEquals(true, getInstrumentation().checkMonitorHit(amStarter, 1));

		assertEquals(starterActivity, starterActivity2);

		final TextView statusTV = (TextView) starterActivity.findViewById(R.id.status_textview);
		final ToggleButton toggleButton = (ToggleButton) starterActivity
				.findViewById(R.id.togglestatus_button);
		assertFalse(toggleButton.isChecked());
		assertEquals("Stopped!", statusTV.getText());

		// ----Turn on service----
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(toggleButton.performClick());
			}
		});

		// Wait a while to make sure the real service can be started
		double waitedSecs = 0;
		while (!statusTV.getText().toString().matches("Started!") && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		getInstrumentation().waitForIdleSync();

		starterActivity.removeAllOpenDialogs();
		getInstrumentation().waitForIdleSync();

		assertEquals("Started!", statusTV.getText());

		TableLayout table = starterActivity.getRobotBehaviorTable();

		assertTrue(table.isShown());

		assertEquals(2, table.getChildCount());

		TableRow row = (TableRow) table.getChildAt(1);

		assertNotNull(row);
		TextView nameView = (TextView) row.getChildAt(0);
		assertNotNull(nameView);
		assertEquals("TESTBOT", nameView.getText());

		LinearLayout behaviorList = (LinearLayout) row.getChildAt(1);
		assertNotNull(behaviorList);

		assertEquals(3, behaviorList.getChildCount());

		final Button behaviorButton = (Button) behaviorList.getChildAt(2);

		// ----Turn on one behavior
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(behaviorButton.performClick());
			}
		});

		// Wait a while to make sure the behavior can be started
		waitedSecs = 0;
		while (behaviorList.getChildCount() != 1 && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		getInstrumentation().waitForIdleSync();

		assertEquals(1, behaviorList.getChildCount());

		// Some more waiting just to let the UI finish building
		getInstrumentation().waitForIdleSync();

		final Button stopButton = (Button) behaviorList.getChildAt(0);

		assertEquals("Stop Behavior", stopButton.getText());

		// ----Turn off behavior
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(stopButton.performClick());
			}
		});

		// Wait a while to make sure the behavior can be stopped
		waitedSecs = 0;
		while (behaviorList.getChildCount() != 3 && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		getInstrumentation().waitForIdleSync();

		assertEquals(3, behaviorList.getChildCount());

		// Some more waiting just to let the UI finish building
		getInstrumentation().waitForIdleSync();

		// ----Turn on one behavior per mocked voice
		SpeechResultManager speechResultManager = SpeechResultManager.getInstance();
		List<String> mockedSpeechResults = new ArrayList<String>();
		mockedSpeechResults.add("bart robtacle behavior");
		mockedSpeechResults.add("start obstacle behavior");
		speechResultManager.allocateNewResults(consoleActivity, mockedSpeechResults);

		// Wait a while to make sure the behavior can be started
		RobotService service;
		waitedSecs = 0;
		do {
			service = starterActivity.getRobotService();
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		} while (service == null && waitedSecs < 10);
		CommandCenter cc = starterActivity.getRobotService().getCCForAddress("TESTADDRESS");
		assertNotNull(cc);
		waitedSecs = 0;
		while ((cc.getRunningBehavior() == null || !cc.getRunningBehavior().getSpeechName()
				.toLowerCase(Locale.US).contains("obstacle"))
				&& waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(100);
		Behavior runningBehavior = cc.getRunningBehavior();
		assertNotNull(runningBehavior);
		assertTrue(runningBehavior.getSpeechName().toLowerCase(Locale.US).contains("obstacle"));

		waitedSecs = 0;
		while (behaviorList.getChildCount() != 1 && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertEquals(1, behaviorList.getChildCount());

		// Some more waiting just to let the UI finish building
		getInstrumentation().waitForIdleSync();

		final Button stopButton2 = (Button) behaviorList.getChildAt(0);

		assertEquals("Stop Behavior", stopButton2.getText());

		// ----Turn off behavior with mocked up voice results----
		mockedSpeechResults.clear();
		mockedSpeechResults.add("stop obstacle behavior");
		speechResultManager.allocateNewResults(consoleActivity, mockedSpeechResults);

		// Wait a while to make sure the behavior can be started
		assertNotNull(cc);
		// Wait a while to make sure the behavior can be stopped
		waitedSecs = 0;
		while (behaviorList.getChildCount() != 3 && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		getInstrumentation().waitForIdleSync();

		assertEquals(3, behaviorList.getChildCount());

		// Some more waiting just to let the UI finish building
		getInstrumentation().waitForIdleSync();

		// ----Turn off service----
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(toggleButton.performClick());
			}
		});

		// Wait a while to make sure the real service can be stopped
		waitedSecs = 0;
		while (!statusTV.getText().toString().matches("Stopped!") && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		getInstrumentation().waitForIdleSync();

		assertEquals("Stopped!", statusTV.getText());

		// Some more waiting just to let the UI finish building
		getInstrumentation().waitForIdleSync();

		assertFalse(table.isShown());

		assertEquals(1, table.getChildCount());

		// Go to Console and check for entries

		getInstrumentation().invokeMenuActionSync(starterActivity, R.id.console_menu_item, 0);

		assertEquals(true, getInstrumentation().checkMonitorHit(amConsole, 1));

		assertTrue(consoleActivity instanceof Console);

		Console console = (Console) consoleActivity;

		TextView consoleTextView = (TextView) console.findViewById(R.id.consoleTextView);

		// Check if stuff was written to console
		assertTrue(consoleTextView.getText().toString().contains("Started behavior: "));

	}

	public void testSwitchingToAboutActivityAndBack() {

		starterActivity.removeAllOpenDialogs();

		// ----Open Console activity-----
		ActivityMonitor amAbout = getInstrumentation().addMonitor(About.class.getName(), null,
				false);

		getInstrumentation().invokeMenuActionSync(starterActivity, R.id.about_menu_item, 0);

		Activity aboutActivity = getInstrumentation().waitForMonitorWithTimeout(amAbout, 10000);
		assertEquals(true, getInstrumentation().checkMonitorHit(amAbout, 1));

		getInstrumentation().waitForIdleSync();

		// ----Go back to Starter activity-----
		ActivityMonitor amStarter = getInstrumentation().addMonitor(Starter.class.getName(), null,
				false);

		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(2000);

		getInstrumentation().invokeMenuActionSync(aboutActivity, R.id.starter_menu_item, 0);

		Activity starterActivity2 = getInstrumentation()
				.waitForMonitorWithTimeout(amStarter, 10000);
		assertEquals(true, getInstrumentation().checkMonitorHit(amStarter, 1));

		assertEquals(starterActivity, starterActivity2);
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(500);

		aboutActivity.finish();
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(500);
	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		getInstrumentation().waitForIdleSync();
		RobotService service = starterActivity.getRobotService();
		if (service != null) {
			service.stopSelf();
		}
		starterActivity.finish();
		if (consoleActivity != null && !consoleActivity.isFinishing())
			consoleActivity.finish();
		System.setProperty(starterActivity.getString(R.string.envvar_testing), "");

		super.tearDown();
	}

}
