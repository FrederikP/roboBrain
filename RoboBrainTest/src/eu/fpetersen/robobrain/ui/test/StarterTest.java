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

import android.app.Dialog;
import android.test.ActivityInstrumentationTestCase2;
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

	private MockRobotService robotService;

	public StarterTest() {
		super(Starter.class);
	}

	@Override
	protected void setUp() throws Exception {
		starterActivity = getActivity();
		robotService = new MockRobotService(starterActivity);
		MockRobotFactory factory = new MockRobotFactory(starterActivity);
		Robot robot = factory.createSimpleRobot("TestBot");
		Behavior b1 = new BackAndForthBehavior();
		b1.initialize(robot, "BackAndForth", "TestSpeechName");
		ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
		behaviors.add(b1);
		robotService.addCC(robot, behaviors);
		System.setProperty(starterActivity.getString(R.string.envvar_testing), "true");
		super.setUp();
	}

	/**
	 * Test toggling the RoboBrain Service from the UI
	 */
	public void testStatusToggle() {
		assertFalse(robotService.isRunning());
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
		int waitedSecs = 0;
		while (!statusTV.getText().toString().matches("Started!") && waitedSecs < 20) {
			Helper.sleepMillis(1000);
			waitedSecs++;
		}

		// set mock service to running
		robotService.setRunning(true);
		assertTrue(robotService.isRunning());

		Helper.sleepMillis(3000);

		// Overwrite real Service with Mock, because we only want to check this
		// Activity
		robotService.broadcastUIUpdateIntent(false);

		TextView robotNameView = getRobotNameTextView();
		int seconds = 0;
		while ((robotNameView == null || !robotNameView.getText().toString().matches("TestBot"))
				&& seconds < 20) {
			Helper.sleepMillis(1000);
			seconds++;
			robotNameView = getRobotNameTextView();
		}

		Helper.sleepMillis(1000);

		assertTrue(robotService.isRunning());
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
			Helper.sleepMillis(1000);
			waitedSecs++;
		}

		// set mock service to not running
		robotService.setRunning(false);

		// Overwrite real Service with Mock, because we only want to check this
		// Activity
		robotService.broadcastUIUpdateIntent(true);

		Helper.sleepMillis(3000);

		assertEquals("Stopped!", statusTV.getText());
		assertFalse(robotService.isRunning());

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
	 * Test creation of Alert Dialog
	 */
	public void testDialogCreation() {
		Dialog dialog = starterActivity.showAlertDialog("TESTDIALOG", "Test this, baby");
		double secondsWaited = 0;
		while (!dialog.isShowing() && secondsWaited < 10) {
			Helper.sleepMillis(100);
			secondsWaited = secondsWaited + 0.1;
		}
		assertNotNull(dialog);
		assertTrue(dialog.isShowing());

		starterActivity.removeAllOpenDialogs();
		secondsWaited = 0;
		while (dialog.isShowing() && secondsWaited < 10) {
			Helper.sleepMillis(100);
			secondsWaited = secondsWaited + 0.1;
		}
		assertFalse(dialog.isShowing());

	}

	/**
	 * Test showing dialog, when activity finished This should lead to a warning
	 * being logged, about that the alert can't be shown
	 */
	public void testDialogCreationWhenActivityFinished() {
		starterActivity.finish();
		double secondsWaited = 0;
		while (!starterActivity.isFinishing() && secondsWaited < 10) {
			Helper.sleepMillis(100);
			secondsWaited = secondsWaited + 0.1;
		}
		assertTrue(starterActivity.isFinishing());

		Helper.sleepMillis(2000);

		Dialog dialog = starterActivity.showAlertDialog("TESTDIALOG", "Test this, baby");

		Helper.sleepMillis(1000);
		assertNotNull(dialog);

		assertFalse(dialog.isShowing());

	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		starterActivity.finish();
		super.tearDown();
	}
}
