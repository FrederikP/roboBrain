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
package eu.fpetersen.robobrain.test.mock;

import java.util.HashMap;
import java.util.Map;

import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.ProximitySensor;
import eu.fpetersen.robobrain.robot.RgbLed;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.robot.RobotPartInitializer;
import eu.fpetersen.robobrain.robot.Servo;
import eu.fpetersen.robobrain.services.RobotService;

/**
 * Creates simple robots for testing purposes
 * 
 * @author Frederik Petersen
 * 
 */
public class MockRobotFactory {

	private RobotService mService;

	public MockRobotFactory(RobotService robotService) {
		mService = robotService;
		;
	}

	/**
	 * Creates Robot without any parts. For simple unit testing
	 */
	public Robot createSimpleRobot(String name) {
		String id;
		Robot robot = new Robot(mService, "----------", name);

		id = "main_motor";
		Motor motor = createStandardMotor(robot, id);
		robot.addPart(id, motor);

		id = "headcolor_rgbled";
		RgbLed led = createStandardLed(robot, id);
		robot.addPart(id, led);

		id = "head_servo";
		Servo servo = createStandardServo(robot, id);
		robot.addPart(id, servo);

		id = "front_proxsensor";
		ProximitySensor frontProx = createFrontProxSensor(robot, id);
		robot.addPart(id, frontProx);

		id = "back_proxsensor";
		ProximitySensor backProx = createFrontProxSensor(robot, id);
		robot.addPart(id, backProx);

		return robot;
	}

	private ProximitySensor createFrontProxSensor(Robot robot, String id) {
		RobotPartInitializer initializer = new RobotPartInitializer(id, robot, null);
		ProximitySensor proxSensor = (ProximitySensor) RobotPartFactory.getInstance(mService)
				.createRobotPart("ProximitySensor", initializer);
		return proxSensor;
	}

	private Servo createStandardServo(Robot robot, String id) {
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("toAngle", 'C');

		RobotPartInitializer initializer = new RobotPartInitializer(id, robot, flags);
		Servo servo = (Servo) RobotPartFactory.getInstance(mService).createRobotPart("Servo",
				initializer);
		return servo;
	}

	private RgbLed createStandardLed(Robot robot, String id) {
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("toColor", 'D');

		RobotPartInitializer initializer = new RobotPartInitializer(id, robot, flags);
		RgbLed rgbLed = (RgbLed) RobotPartFactory.getInstance(mService).createRobotPart("RgbLed",
				initializer);
		return rgbLed;
	}

	private Motor createStandardMotor(Robot robot, String id) {
		Map<String, Character> flags = new HashMap<String, Character>();
		flags.put("advance", 'A');
		flags.put("backoff", 'B');
		flags.put("stop", 'S');
		flags.put("left", 'L');
		flags.put("right", 'R');

		RobotPartInitializer initializer = new RobotPartInitializer(id, robot, flags);
		Motor motor = (Motor) RobotPartFactory.getInstance(mService).createRobotPart("Motor",
				initializer);
		return motor;
	}

}
