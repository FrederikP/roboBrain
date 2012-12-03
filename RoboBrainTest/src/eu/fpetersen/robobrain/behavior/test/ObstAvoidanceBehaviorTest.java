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
package eu.fpetersen.robobrain.behavior.test;

import java.io.IOException;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.behavior.ObstAvoidanceBehavior;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.parts.Motor.MotorState;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests {@link ObstAvoidanceBehavior}.
 * 
 * @author Frederik Petersen
 * 
 */
public class ObstAvoidanceBehaviorTest extends AndroidTestCase {

	private MockRobotService mService;

	@Override
	protected void setUp() throws Exception {
		System.setProperty(getContext().getString(R.string.envvar_testing), "true");
		mService = new MockRobotService(getContext());
		super.setUp();
	}

	/**
	 * Tries to test and cover as much of the {@link ObstAvoidanceBehavior} as
	 * possible
	 * 
	 * @throws IOException
	 */
	public void testRunningObstAvoidanceBehavior() {
		MockRobotFactory fact = new MockRobotFactory(mService);
		Robot robot = fact.createSimpleRobot("TestBot");

		robot.getFrontSensor().setValue(90);
		robot.getBackSensor().setValue(1);

		final Behavior obstBehavior = new ObstAvoidanceBehavior();
		BehaviorInitializer initializer = new BehaviorInitializer("ObstAvoidance", "obstacle");
		initializer.initialize(obstBehavior, robot);
		assertNotNull(obstBehavior.getId());

		// Turn on
		Runnable behaviorTask = new Runnable() {

			public void run() {
				obstBehavior.startBehavior();
			}
		};
		Thread behaviorThread = new Thread(behaviorTask);
		behaviorThread.start();

		double waitedSecs = 0;
		while (!obstBehavior.isTurnedOn() && waitedSecs < 10) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		assertTrue(obstBehavior.isTurnedOn());

		waitedSecs = 0;
		while (MotorState.FORWARD != robot.getMainMotor().getState() && waitedSecs < 10) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertEquals(MotorState.FORWARD, robot.getMainMotor().getState());

		robot.getFrontSensor().setValue(5);
		robot.getBackSensor().setValue(1);
		Helper.sleepMillis(200);
		assertEquals(MotorState.BACKWARD, robot.getMainMotor().getState());

		robot.getFrontSensor().setValue(200);
		robot.getBackSensor().setValue(0);
		Helper.sleepMillis(300);
		robot.getFrontSensor().setValue(60);
		Helper.sleepMillis(300);
		robot.getFrontSensor().setValue(90);
		Helper.sleepMillis(1000);
		assertEquals(MotorState.TURNING_LEFT, robot.getMainMotor().getState());

		robot.getFrontSensor().setValue(90);
		robot.getBackSensor().setValue(1);
		robot.getMainMotor().delayActionDone();
		Helper.sleepMillis(200);
		assertEquals(MotorState.FORWARD, robot.getMainMotor().getState());

		robot.getFrontSensor().setValue(5);
		robot.getBackSensor().setValue(1);
		Helper.sleepMillis(200);
		assertEquals(MotorState.BACKWARD, robot.getMainMotor().getState());

		robot.getFrontSensor().setValue(200);
		robot.getBackSensor().setValue(0);
		Helper.sleepMillis(300);
		robot.getFrontSensor().setValue(90);
		Helper.sleepMillis(300);
		robot.getFrontSensor().setValue(60);
		Helper.sleepMillis(500);
		assertEquals(MotorState.TURNING_RIGHT, robot.getMainMotor().getState());

		robot.getFrontSensor().setValue(90);
		robot.getBackSensor().setValue(1);
		robot.getMainMotor().delayActionDone();
		Helper.sleepMillis(200);
		assertEquals(MotorState.FORWARD, robot.getMainMotor().getState());

		// Turn off
		obstBehavior.stopBehavior();
		Helper.sleepMillis(200);
		assertFalse(obstBehavior.isTurnedOn());
		assertEquals(MotorState.STOPPED, robot.getMainMotor().getState());

	}

	@Override
	protected void tearDown() throws Exception {
		if (mService != null) {
			mService.destroy();
		}
		System.setProperty(getContext().getString(R.string.envvar_testing), "");
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
