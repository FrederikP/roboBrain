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
package eu.fpetersen.robobrain.robot.test;

import java.util.HashMap;
import java.util.Map;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.robot.parts.RobotPart;
import eu.fpetersen.robobrain.robot.parts.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.robot.RobotPartInitializer;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link Robot}
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotTest extends AndroidTestCase {

	private MockRobotService mService;

	/**
	 * Test adding the standard Motor, setting its speed and stopping the robot.
	 */
	public void testPartAddingWithMainMotor() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot robot = factory.createSimpleRobot("TestBot");

		assertNotNull(robot);

		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("advance", 'A');
		flags.put("backoff", 'B');
		flags.put("stop", 'S');
		flags.put("left", 'L');
		flags.put("right", 'R');

		RobotPartInitializer initializer = new RobotPartInitializer("Motor", robot, flags);

		RobotPart motorPart = RobotPartFactory.getInstance(mService).createRobotPart("Motor",
				initializer);

		assertNotNull(motorPart);

		robot.addPart("main_motor", motorPart);

		Motor motor = robot.getMainMotor();

		assertNotNull(motor);

		motor.advance(200);

		assertEquals(MotorState.FORWARD, motor.getState());

		robot.stop();

		assertEquals(MotorState.STOPPED, motor.getState());

	}

	/**
	 * Just make sure no exception is thrown. Possible improvement would be a
	 * registered listener
	 */
	public void testAmarinoSending() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot robot = factory.createSimpleRobot("TestBot");

		assertNotNull(robot);

		// int
		robot.sendToArduino('X', 1);

		// int[]
		int[] intArray = { 1, 2, 3 };
		robot.sendToArduino('X', intArray);

		// String
		robot.sendToArduino('X', "boo");

		// String Array
		String[] stringArray = { "Blub", "Blab" };
		robot.sendToArduino('X', stringArray);
	}

	@Override
	protected void tearDown() throws Exception {
		if (mService != null) {
			mService.destroy();
		}
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
