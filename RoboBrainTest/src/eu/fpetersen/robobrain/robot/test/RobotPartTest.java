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
import eu.fpetersen.robobrain.color.RgbColor;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.robot.RobotPartInitializer;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.robot.parts.ProximitySensor;
import eu.fpetersen.robobrain.robot.parts.RgbLed;
import eu.fpetersen.robobrain.robot.parts.RobotPart;
import eu.fpetersen.robobrain.test.mock.AbstractPart;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.mock.PrivateConstructorPart;

/**
 * Tests {@link RobotPart} class.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotPartTest extends AndroidTestCase {

	private MockRobotService mService;

	/**
	 * Test {@link RobotPart}s onReceive method. It is usually called when the
	 * hardware sends data to the Android device
	 */
	public void testOnReceive() {

		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot mockRobot = factory.createSimpleRobot("TestBot");

		RobotPartInitializer initializer = new RobotPartInitializer("ProximitySensor", mockRobot,
				null);
		RobotPart sensorPart = RobotPartFactory.getInstance(mService).createRobotPart(
				"ProximitySensor", initializer);

		assertNotNull(sensorPart);

		assertTrue(initializer.areRequirementsMet());

		sensorPart.onReceive("23");

		assertEquals(23, ((ProximitySensor) sensorPart).getValue());

	}

	/**
	 * Test creating a motor, including flags
	 */
	public void testMotorCreation() {
		mService = new MockRobotService(getContext());
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

		assertNotNull(motor);

		assertTrue(initializer.areRequirementsMet());

		assertEquals('A', motor.getFlag("advance"));

		assertEquals(' ', motor.getFlag("notexistant"));

	}

	/**
	 * Test creating a motor, including flags
	 */
	public void testMotorWithMissingFlags() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot mockRobot = factory.createSimpleRobot("TestBot");
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("advance", 'A');
		flags.put("stop", 'S');

		RobotPartInitializer initializer = new RobotPartInitializer("Motor", mockRobot, flags);
		Motor motor = (Motor) RobotPartFactory.getInstance(mService).createRobotPart("Motor",
				initializer);

		assertNotNull(motor);

		assertFalse(initializer.areRequirementsMet());

		assertEquals('A', motor.getFlag("advance"));

		assertEquals(' ', motor.getFlag("backoff"));

	}

	public void testRgbLed() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot mockRobot = factory.createSimpleRobot("TestBot");
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("toColor", 'D');

		RobotPartInitializer initializer = new RobotPartInitializer("RgbLed", mockRobot, flags);

		RgbLed led = (RgbLed) RobotPartFactory.getInstance(mService).createRobotPart("RgbLed",
				initializer);

		led.set(12, 12, 12);

		assertEquals(12, led.getColor().getRed());

		led.set(new RgbColor("Something", 12, 12, 12));

		led.set(new RgbColor("SomethingDifferent", 15, 15, 16));

		assertEquals(16, led.getColor().getBlue());
	}

	public void testNegativeFactoryBehavior() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot mockRobot = factory.createSimpleRobot("TestBot");

		RobotPartFactory fac = RobotPartFactory.getInstance(mService);
		RobotPartInitializer initializer = new RobotPartInitializer("Rocket", mockRobot, null);

		RobotPart part = fac.createRobotPart("Rocket", initializer);
		assertNull(part);

		part = fac.createRobotPart(AbstractPart.class.getName(), initializer);
		assertNull(part);

		part = fac.createRobotPart(PrivateConstructorPart.class.getName(), initializer);
		assertNull(part);

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
