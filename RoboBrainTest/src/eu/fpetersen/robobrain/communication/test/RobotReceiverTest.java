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
package eu.fpetersen.robobrain.communication.test;

import java.util.ArrayList;

import android.content.Intent;
import android.test.AndroidTestCase;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.RobotReceiver;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link RobotReceiver}
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotReceiverTest extends AndroidTestCase {

	private MockRobotService mService;
	private RobotReceiver mRobotReceiver;
	private Robot mRobot;

	@Override
	protected void setUp() throws Exception {
		mService = new MockRobotService(getContext());
		mRobotReceiver = new RobotReceiver(mService);
		MockRobotFactory fact = new MockRobotFactory(mService);
		mRobot = fact.createSimpleRobot("TestBot");
		mService.addCC(mRobot, new ArrayList<Behavior>());
		super.setUp();
	}

	/**
	 * Test if all positive behavior works out, by checking on the robots sensor
	 * values and motor state
	 */
	public void testChangingSensorValues() {

		// Test FrontSensor
		int newValue = 12;
		Intent intent = new Intent(AmarinoIntent.ACTION_RECEIVED);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.STRING_EXTRA);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, "FRONTPROX:" + newValue);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, mRobot.getAddress());

		mRobotReceiver.onReceive(getContext(), intent);
		assertEquals(12, mRobot.getFrontSensor().getValue());

		// Test BackSensor
		newValue = 1;
		intent = new Intent(AmarinoIntent.ACTION_RECEIVED);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.STRING_EXTRA);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, "BACKPROX:" + newValue);
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, mRobot.getAddress());

		mRobotReceiver.onReceive(getContext(), intent);
		assertEquals(12, mRobot.getBackSensor());

		// Test motor stop after delayed action
		intent = new Intent(AmarinoIntent.ACTION_RECEIVED);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.STRING_EXTRA);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, "STOPPEDAFTERDELAY");
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, mRobot.getAddress());

		mRobot.getMainMotor().advance(200);
		assertEquals(MotorState.FORWARD, mRobot.getMainMotor().getState());
		mRobotReceiver.onReceive(getContext(), intent);
		assertEquals(MotorState.STOPPED, mRobot.getMainMotor().getState());

		// Test Console to make sure no errors are thrown:
		intent = new Intent(AmarinoIntent.ACTION_RECEIVED);
		intent.putExtra(AmarinoIntent.EXTRA_DATA_TYPE, AmarinoIntent.STRING_EXTRA);
		intent.putExtra(AmarinoIntent.EXTRA_DATA, "CONSOLE:log this");
		intent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, mRobot.getAddress());

		mRobotReceiver.onReceive(getContext(), intent);

	}

	@Override
	protected void tearDown() throws Exception {
		mService.destroy();
		super.tearDown();
	}

}
