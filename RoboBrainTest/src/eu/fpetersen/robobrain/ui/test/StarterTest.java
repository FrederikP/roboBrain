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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.BackAndForthBehavior;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.ui.Starter;

/**
 * Unit testing if the {@link Starter} activity.
 * 
 * @author Frederik Petersen
 * 
 */
public class StarterTest extends ActivityInstrumentationTestCase2<Starter> {

	private Starter starterActivity;

	private MockRobotService mRobotService;

	public StarterTest() {
		super(Starter.class);
	}

	@Override
	protected void setUp() throws Exception {
		starterActivity = getActivity();
		System.setProperty(starterActivity.getString(R.string.envvar_testing), "true");
		super.setUp();
	}

	/**
	 * Test toggling the RoboBrain Service from the UI
	 */
	public void testStatusToggle() {
		mRobotService = new MockRobotService(starterActivity);
		MockRobotFactory factory = new MockRobotFactory(mRobotService);
		Robot robot = factory.createSimpleRobot("TestBot");
		Behavior b1 = new BackAndForthBehavior();
		b1.initialize(robot, "BackAndForth", "TestSpeechName");
		ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
		behaviors.add(b1);
		mRobotService.addCC(robot, behaviors);
		assertFalse(mRobotService.isRunning());
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
		// Wait a while to make sure any real service can be started
		double waitedSecs = 0;
		while (!statusTV.getText().toString().matches("Started!") && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		// set mock service to running
		mRobotService.setRunning(true);
		assertTrue(mRobotService.isRunning());

		Helper.sleepMillis(3000);

		// Overwrite real Service with Mock, because we only want to check this
		// Activity
		mRobotService.broadcastUIUpdateIntent(false);

		TextView robotNameView = getRobotNameTextView();
		double seconds = 0;
		while ((robotNameView == null || !robotNameView.getText().toString().matches("TestBot"))
				&& seconds < 20) {
			Helper.sleepMillis(100);
			seconds = seconds + 0.1;
			robotNameView = getRobotNameTextView();
		}

		Helper.sleepMillis(1000);

		assertTrue(mRobotService.isRunning());
		assertEquals("Started!", statusTV.getText());

		int tableChilds = starterActivity.getRobotBehaviorTable().getChildCount();

		assertSame(2, tableChilds);

		robotNameView = (TextView) ((TableRow) starterActivity.getRobotBehaviorTable()
				.getChildAt(1)).getChildAt(0);
		assertSame("TestBot", robotNameView.getText());

		// ----Turn off service----
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				assertTrue(toggleButton.performClick());
			}
		});
		// Wait a while to make sure any real service can be stopped
		waitedSecs = 0;
		while (!statusTV.getText().toString().matches("Stopped!") && waitedSecs < 5) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		// set mock service to not running
		mRobotService.setRunning(false);

		// Overwrite real Service with Mock, because we only want to check this
		// Activity
		mRobotService.broadcastUIUpdateIntent(true);

		Helper.sleepMillis(3000);

		waitedSecs = 0;
		while (statusTV.getText() != "Stopped!" && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
			getInstrumentation().waitForIdleSync();
		}

		assertEquals("Stopped!", statusTV.getText());
		assertFalse(mRobotService.isRunning());

		tableChilds = starterActivity.getRobotBehaviorTable().getChildCount();

		assertSame(1, tableChilds);

		assertFalse(starterActivity.getRobotBehaviorTable().isShown());
		starterActivity.removeAllOpenDialogs();

	}

	/**
	 * Returns null if not there
	 * 
	 * @return TextView for RobotName
	 */
	private TextView getRobotNameTextView() {
		TableLayout behaviorTable = starterActivity.getRobotBehaviorTable();
		TableRow firstRobotRow = (TableRow) behaviorTable.getChildAt(1);
		if (firstRobotRow != null) {
			TextView robotNameView = (TextView) firstRobotRow.getChildAt(0);
			return robotNameView;
		}
		return null;
	}

	/**
	 * Test creation of Alert Dialog and removal by removeAllOpenDialogs.
	 */
	public void testDialogCreationAndRemovalByMethod() {
		Dialog dialog = starterActivity.showAlertDialog("TESTDIALOG", "Test this, baby");
		getInstrumentation().waitForIdleSync();
		assertNotNull(dialog);
		assertTrue(dialog.isShowing());

		starterActivity.removeAllOpenDialogs();
		getInstrumentation().waitForIdleSync();
		assertFalse(dialog.isShowing());

	}

	/**
	 * Test creation of Alert Dialog and removal by click on button.
	 */
	public void testDialogCreationAndRemovalByClick() {

		AlertDialog dialog = starterActivity.showAlertDialog("TESTDIALOG", "Test this, baby");

		getInstrumentation().waitForIdleSync();
		assertNotNull(dialog);
		assertTrue(dialog.isShowing());

		final Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		Log.v("ButtonTex", "" + positive.getText());
		starterActivity.runOnUiThread(new Runnable() {

			public void run() {
				positive.performClick();
			}
		});

		getInstrumentation().waitForIdleSync();
		assertFalse(dialog.isShowing());

	}

	/**
	 * Test showing dialog, when activity finished This should lead to a warning
	 * being logged, about that the alert can't be shown
	 */
	public void testDialogCreationWhenActivityFinished() {
		starterActivity.finish();
		Helper.sleepMillis(2500);
		getInstrumentation().waitForIdleSync();
		assertTrue(starterActivity.isFinishing());

		getInstrumentation().waitForIdleSync();

		Dialog dialog = starterActivity.showAlertDialog("TESTDIALOG", "Test this, baby");

		assertNotNull(dialog);

		getInstrumentation().waitForIdleSync();
		double waitedSecs = 0;
		while (dialog.isShowing() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
			getInstrumentation().waitForIdleSync();
		}
		getInstrumentation().waitForIdleSync();
		Helper.sleepMillis(500);
		assertFalse(dialog.isShowing());

	}

	/**
	 * Test dialog waiting method
	 */
	public void testDialogWaiting() {

		// Test if method returns null for null list
		AlertDialog dialog = starterActivity.waitForDialogToBeCreated(null);
		assertNull(dialog);

		final List<AlertDialog> dialogs = new ArrayList<AlertDialog>();

		// Test if method returns null for empty list
		dialog = starterActivity.waitForDialogToBeCreated(dialogs);
		assertNull(dialog);

		final AlertDialog dialogToInsert = starterActivity.showAlertDialog("Test", "Tested");

		TimerTask addDialogTask = new TimerTask() {

			@Override
			public void run() {
				dialogs.add(dialogToInsert);
			}
		};
		Timer timer = new Timer();
		timer.schedule(addDialogTask, 1000);

		// Test if method returns dialog, after it's added by timer task
		dialog = starterActivity.waitForDialogToBeCreated(dialogs);
		assertNotNull(dialog);
		assertEquals(dialog, dialogToInsert);

		// Test if method returns dialog, which is already added
		dialog = starterActivity.waitForDialogToBeCreated(dialogs);
		assertNotNull(dialog);
		assertEquals(dialog, dialogToInsert);

	}

	@Override
	protected void tearDown() throws Exception {
		if (mRobotService != null) {
			mRobotService.destroy();
		}
		getInstrumentation().waitForIdleSync();
		// ///CLOVER:FLUSH
		starterActivity.finish();
		System.setProperty(starterActivity.getString(R.string.envvar_testing), "");
		super.tearDown();
	}
}
