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

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.robot.RobotPart;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link Robot}
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotTest extends AndroidTestCase {

	/**
	 * Test adding the standard Motor, setting its speed and stopping the robot.
	 */
	public void testPartAddingWithMainMotor() {
		RobotService service = new MockRobotService(getContext());
		Robot robot = RobotFactory.getInstance(service).createSimpleRobot(
				"TestBot");

		assertNotNull(robot);

		RobotPart motorPart = RobotPartFactory.getInstance(service)
				.createRobotPart("Motor", robot);

		assertNotNull(motorPart);

		robot.addPart("main_motor", motorPart);

		Motor motor = robot.getMainMotor();

		assertNotNull(motor);

		motor.advance(200);

		assertEquals(MotorState.FORWARD, motor.getState());

		robot.stop();

		assertEquals(MotorState.STOPPED, motor.getState());

	}

}