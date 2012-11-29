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
import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.behavior.ReactToSpeechBehavior;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.parts.Motor.MotorState;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests {@link ReactToSpeechBehaviorTest}.
 * 
 * @author Frederik Petersen
 * 
 */
public class ReactToSpeechBehaviorTest extends AndroidTestCase {

	private MockRobotService mService;

	@Override
	protected void setUp() throws Exception {
		System.setProperty(getContext().getString(R.string.envvar_testing), "true");
		mService = new MockRobotService(getContext());
		super.setUp();
	}

	/**
	 * Tries to test and cover as much of the {@link ReactToSpeechBehavior} as
	 * possible
	 * 
	 * @throws IOException
	 */
	public void testRunningReactToSpeechBehavior() {
		MockRobotFactory fact = new MockRobotFactory(mService);
		Robot robot = fact.createSimpleRobot("TestBot");

		robot.getFrontSensor().setValue(90);
		robot.getBackSensor().setValue(1);

		final ReactToSpeechBehavior reactBehavior = new ReactToSpeechBehavior();
		BehaviorInitializer initializer = new BehaviorInitializer("ReactToSpeech", "react");
		initializer.initialize(reactBehavior, robot);
		assertNotNull(reactBehavior.getId());

		// Turn on
		Runnable behaviorTask = new Runnable() {

			public void run() {
				reactBehavior.startBehavior();
			}
		};
		Thread behaviorThread = new Thread(behaviorTask);
		behaviorThread.start();

		double waitedSecs = 0;
		while (!reactBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		assertTrue(reactBehavior.isTurnedOn());

		assertEquals(MotorState.STOPPED, robot.getMainMotor().getState());

		List<String> mockedResults = new ArrayList<String>();

		mockedResults.add("forward");
		reactBehavior.onReceive(mockedResults);
		assertEquals(MotorState.FORWARD, robot.getMainMotor().getState());
		mockedResults.clear();

		robot.getFrontSensor().setValue(5);
		robot.getBackSensor().setValue(1);

		waitedSecs = 0;
		while (MotorState.STOPPED != robot.getMainMotor().getState() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertEquals(MotorState.STOPPED, robot.getMainMotor().getState());

		mockedResults.add("backward");
		reactBehavior.onReceive(mockedResults);
		assertEquals(MotorState.BACKWARD, robot.getMainMotor().getState());
		mockedResults.clear();

		mockedResults.add("bla bla");
		mockedResults.add("bla bla stop");
		reactBehavior.onReceive(mockedResults);
		assertEquals(MotorState.STOPPED, robot.getMainMotor().getState());
		mockedResults.clear();

		mockedResults.add("backward");
		reactBehavior.onReceive(mockedResults);
		assertEquals(MotorState.BACKWARD, robot.getMainMotor().getState());
		mockedResults.clear();

		robot.getFrontSensor().setValue(50);
		robot.getBackSensor().setValue(0);
		waitedSecs = 0;
		while (MotorState.STOPPED != robot.getMainMotor().getState() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		assertEquals(MotorState.STOPPED, robot.getMainMotor().getState());

		mockedResults.add("right");
		reactBehavior.onReceive(mockedResults);
		assertEquals(MotorState.TURNING_RIGHT, robot.getMainMotor().getState());
		mockedResults.clear();

		mockedResults.add("left");
		reactBehavior.onReceive(mockedResults);
		assertEquals(MotorState.TURNING_LEFT, robot.getMainMotor().getState());
		mockedResults.clear();

		robot.getMainMotor().delayActionDone();
		assertEquals(MotorState.STOPPED, robot.getMainMotor().getState());

		mockedResults.add("red");
		reactBehavior.onReceive(mockedResults);
		assertEquals(255, robot.getHeadColorLed().getColor().getRed());
		mockedResults.clear();

		mockedResults.add("red");
		reactBehavior.onReceive(mockedResults);
		assertEquals(255, robot.getHeadColorLed().getColor().getRed());
		mockedResults.clear();

		mockedResults.add("octopus");
		reactBehavior.onReceive(mockedResults);
		assertEquals(255, robot.getHeadColorLed().getColor().getRed());
		mockedResults.clear();

		mockedResults.add("green");
		reactBehavior.onReceive(mockedResults);
		assertEquals(0, robot.getHeadColorLed().getColor().getRed());
		assertEquals(255, robot.getHeadColorLed().getColor().getGreen());
		mockedResults.clear();

		mockedResults.add("something");
		mockedResults.add("navy blue");
		reactBehavior.onReceive(mockedResults);
		assertEquals(0, robot.getHeadColorLed().getColor().getRed());
		assertEquals(0, robot.getHeadColorLed().getColor().getGreen());
		assertEquals(128, robot.getHeadColorLed().getColor().getBlue());
		mockedResults.clear();

		// Turn off
		reactBehavior.stopBehavior();
		waitedSecs = 0;
		while (reactBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		assertFalse(reactBehavior.isTurnedOn());
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
