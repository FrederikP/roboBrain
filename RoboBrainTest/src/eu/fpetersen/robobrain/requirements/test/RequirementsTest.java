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
package eu.fpetersen.robobrain.requirements.test;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.robot.parts.ProximitySensor;
import eu.fpetersen.robobrain.robot.parts.RgbLed;
import eu.fpetersen.robobrain.robot.parts.Servo;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests the {@link Requirements} class
 * 
 * @author Frederik Petersen
 * 
 */
public class RequirementsTest extends AndroidTestCase {

	private Robot mockRobot;

	private MockRobotService mRobotService;

	@Override
	protected void setUp() throws Exception {
		mRobotService = new MockRobotService(getContext());
		MockRobotFactory fact = new MockRobotFactory(mRobotService);
		mockRobot = fact.createSimpleRobot("TestBot");
		super.setUp();
	}

	/**
	 * Test if requirements check is positive for the robot.
	 */
	public void testRequirementsMet() {
		Requirements requirements = new Requirements();
		requirements.addPart("main_motor", Motor.class.getName());
		requirements.addPart("head_servo", Servo.class.getName());
		requirements.addPart("front_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("back_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("headcolor_rgbled", RgbLed.class.getName());

		// Test positive:
		assertTrue(requirements.fulfillsRequirements(mockRobot));

	}

	/**
	 * Test if Requirements check fails for required part with an ID that the
	 * robot has but a different type
	 */
	public void testWrongType() {
		Requirements requirements = new Requirements();
		requirements.addPart("main_motor", RgbLed.class.getName());

		// Test negative
		assertFalse(requirements.fulfillsRequirements(mockRobot));
	}

	/**
	 * Test if Requirements check fails for required part with an ID that the
	 * robot does not have a part for
	 */
	public void testMissingId() {
		Requirements requirements = new Requirements();
		requirements.addPart("main_rocket", "eu.fpetersen.robobrain.robot.Rocket");

		// Test negative
		assertFalse(requirements.fulfillsRequirements(mockRobot));
	}

	@Override
	protected void tearDown() throws Exception {
		if (mRobotService != null) {
			mRobotService.destroy();
		}
		super.tearDown();
	}

}
