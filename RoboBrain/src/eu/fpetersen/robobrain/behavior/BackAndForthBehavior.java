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
package eu.fpetersen.robobrain.behavior;

import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.ProximitySensor;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.Servo;

/**
 * Makes the robot go back and forth. Very simple behavior. Checks front and
 * back proximity sensors for distance to next obstacle so it knows when to
 * switch direction. If there are obstacles in back and front, the robot stops
 * moving, until one of the obstacles is gone.
 * 
 * @author Frederik Petersen
 */
public class BackAndForthBehavior extends Behavior {

	@Override
	protected void behaviorLoop() {
		Robot robot = getRobot();
		if (robot.getMainMotor().getState().equals(MotorState.STOPPED)) {
			if (robot.getFrontSensor().getValue() < 20 && robot.getBackSensor().getValue() == 1) {
				robot.getMainMotor().backOff(240);
				toConsole("STOPPED -> BACKWARD");
			} else if (robot.getFrontSensor().getValue() >= 20) {
				robot.getMainMotor().advance(240);
				toConsole("STOPPED -> FORWARD");
			}
		} else if (robot.getMainMotor().getState().equals(MotorState.FORWARD)) {
			if (robot.getFrontSensor().getValue() < 20 && robot.getBackSensor().getValue() == 1) {
				robot.getMainMotor().backOff(240);
				toConsole("FORWARD -> BACKWARD");
				toConsole("Frontsensor=" + robot.getFrontSensor().getValue());
			} else if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMainMotor().stop(0);
				toConsole("FORWARD -> STOPPED");
			}
		} else if (robot.getMainMotor().getState().equals(MotorState.BACKWARD)) {
			if (robot.getFrontSensor().getValue() >= 20 && robot.getBackSensor().getValue() == 0) {
				robot.getMainMotor().advance(240);
				toConsole("BACKWARD-> FORWARD");
			} else if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMainMotor().stop(0);
				toConsole("BACKWARD-> STOPPED");
			}
		}
	}

	@Override
	protected void onStart() {
		// Nothing to setup for now
	}

	@Override
	protected void onStop() {
		getRobot().stop();
	}

	@Override
	protected void fillRequirements(Requirements requirements) {
		requirements.addPart("main_motor", Motor.class.getName());
		requirements.addPart("head_servo", Servo.class.getName());
		requirements.addPart("front_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("back_proxsensor", ProximitySensor.class.getName());

	}

}
