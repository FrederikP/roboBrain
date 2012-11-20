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
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.robot.RobotPartInitializer;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link Motor}
 * 
 * @author Frederik Petersen
 * 
 */
public class MotorTest extends AndroidTestCase {

	private MockRobotService mService;

	/**
	 * Test if Motor is in the right state, after called methods
	 */
	public void testStateManagement() {
		mService = new MockRobotService(getContext());
		int speed = 200;
		int angle = 90;
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot mockRobot = factory.createSimpleRobot("TestBot");
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("advance", 'A');
		flags.put("backoff", 'B');
		flags.put("stop", 'S');
		flags.put("left", 'L');
		flags.put("right", 'R');

		RobotPartInitializer initializer = new RobotPartInitializer("Motor", mockRobot, flags);
		Motor motor = (Motor) RobotPartFactory.getInstance(mService).createRobotPart("Motor",
				initializer);

		motor.advance(speed);
		assertEquals(MotorState.FORWARD, motor.getState());

		motor.backOff(speed);
		assertEquals(MotorState.BACKWARD, motor.getState());

		motor.turnLeft(angle);
		assertEquals(MotorState.TURNING_LEFT, motor.getState());

		motor.delayActionDone();
		assertEquals(MotorState.STOPPED, motor.getState());

		motor.turnRight(angle);
		assertEquals(MotorState.TURNING_RIGHT, motor.getState());

		motor.delayActionDone();
		assertEquals(MotorState.STOPPED, motor.getState());

		motor.advance(speed);
		assertEquals(MotorState.FORWARD, motor.getState());

		motor.stop(0);
		assertEquals(MotorState.STOPPED, motor.getState());

		motor.advance(speed);
		assertEquals(MotorState.FORWARD, motor.getState());

		motor.stop(500);
		assertEquals(MotorState.STOPPINGWITHDELAY, motor.getState());

		motor.delayActionDone();
		assertEquals(MotorState.STOPPED, motor.getState());

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
