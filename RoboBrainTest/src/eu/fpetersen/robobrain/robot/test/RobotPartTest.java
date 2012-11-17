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
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotPart;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.robot.RobotPartInitializer;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link RobotPart} class.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotPartTest extends AndroidTestCase {

	/**
	 * Test creating a motor, including flags
	 */
	public void testMotorCreation() {
		RobotService service = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(getContext());
		Robot mockRobot = factory.createSimpleRobot("TestBot");
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("advance", 'A');
		flags.put("backoff", 'B');
		flags.put("stop", 'S');
		flags.put("left", 'L');
		flags.put("right", 'R');

		RobotPartInitializer initializer = new RobotPartInitializer("Motor", mockRobot, flags);
		Motor motor = (Motor) RobotPartFactory.getInstance(service).createRobotPart("Motor",
				initializer);

		assertNotNull(motor);

		assertTrue(initializer.areRequirementsMet());

		assertEquals('A', motor.getFlag("advance"));

		assertEquals(' ', motor.getFlag("notexistant"));

	}

	/**
	 * Test creating a motor, including flags
	 */
	public void testMotorWithMissingFlags() {
		RobotService service = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(getContext());
		Robot mockRobot = factory.createSimpleRobot("TestBot");
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("advance", 'A');
		flags.put("stop", 'S');

		RobotPartInitializer initializer = new RobotPartInitializer("Motor", mockRobot, flags);
		Motor motor = (Motor) RobotPartFactory.getInstance(service).createRobotPart("Motor",
				initializer);

		assertNotNull(motor);

		assertFalse(initializer.areRequirementsMet());

		assertEquals('A', motor.getFlag("advance"));

		assertEquals(' ', motor.getFlag("backoff"));

	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
