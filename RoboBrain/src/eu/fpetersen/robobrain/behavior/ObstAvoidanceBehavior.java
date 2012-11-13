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

import android.util.Log;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.ProximitySensor;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.Servo;

/**
 * Makes robot avoid obstacles by backing off and looking in both directions for
 * where to go next.
 * 
 * @author Frederik Petersen
 * 
 */
public class ObstAvoidanceBehavior extends Behavior {

	private static final String TAG = "ObstAvoidanceBehavior";

	private static final int SPEED = 240;

	private long mBackWardTime = 0;

	@Override
	public void startBehavior() {
		mBackWardTime = 0;
		super.startBehavior();
	}

	@Override
	protected void behaviorLoop() {
		Robot robot = getRobot();
		if (!robot.getMainMotor().getState().equals(MotorState.STOPPINGWITHDELAY)
				&& !robot.getMainMotor().getState().equals(MotorState.TURNING_LEFT)
				&& !robot.getMainMotor().getState().equals(MotorState.TURNING_RIGHT)) {
			if (robot.getMainMotor().getState().equals(MotorState.FORWARD)) {
				goingForward();
			} else if (robot.getMainMotor().getState().equals(MotorState.BACKWARD)) {
				goingBackward();
			} else if (robot.getMainMotor().getState().equals(MotorState.STOPPED)) {
				startGoingForward();
			}
		}
	}

	/**
	 * Makes robot go forward.
	 */
	private void startGoingForward() {
		getRobot().getMainMotor().advance(SPEED);
	}

	/**
	 * Called while going backward to check if time has expired or obstacle in
	 * back
	 */
	private void goingBackward() {
		Robot robot = getRobot();
		if (mBackWardTime < System.currentTimeMillis() || robot.getBackSensor().getValue() == 0) {
			mBackWardTime = 0;
			checkForBestRouteAndTurn();
		}
	}

	/**
	 * Called after backing off from obstacle in front. Looks right, then left
	 * to find out which direction the robot will best turn and continue.
	 */
	private void checkForBestRouteAndTurn() {
		Robot robot = getRobot();
		try {
			robot.getHeadServo().setToAngle(50);
			Thread.sleep(300);
			int rightMeasurement = robot.getFrontSensor().getValue();
			robot.getHeadServo().setToAngle(140);
			Thread.sleep(300);
			int leftMeasurement = robot.getFrontSensor().getValue();
			robot.getHeadServo().setToAngle(95);
			if (rightMeasurement >= leftMeasurement) {
				robot.getMainMotor().turnRight(45);
			} else {
				robot.getMainMotor().turnLeft(45);
			}
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted while waiting for servo to turn", e);
		}
	}

	/**
	 * checks for obstacles in front while going forward. If it finds obstacle,
	 * goes backward.
	 */
	private void goingForward() {
		Robot robot = getRobot();
		if (robot.getFrontSensor().getValue() < 30) {
			robot.getMainMotor().backOff(SPEED);
			mBackWardTime = System.currentTimeMillis() + 1000;
		}

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

	@Override
	protected void onStart() {
		// Nothing to setup for now
	}

}
