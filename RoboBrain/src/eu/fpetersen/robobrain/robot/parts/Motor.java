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
package eu.fpetersen.robobrain.robot.parts;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents Arduino controlled motor unit of the robot.
 * 
 * @author Frederik Petersen
 * 
 */
public class Motor extends RobotPart {

	/**
	 * Represents in which state the Motor is.
	 */
	private MotorState mState = MotorState.STOPPED;

	/**
	 * Back off at given speed
	 * 
	 * @param speed
	 *            The speed the robot is supposed to back off with.
	 */
	public void backOff(int speed) {
		getRobot().sendToArduino(getFlag("backoff"), speed);
		setState(MotorState.BACKWARD);

	}

	/**
	 * Advance at given speed
	 * 
	 * @param speed
	 *            The speed the robot is supposed to advance with.
	 */
	public void advance(int speed) {
		getRobot().sendToArduino(getFlag("advance"), speed);
		setState(MotorState.FORWARD);
	}

	/**
	 * Stops the robot with the given delay
	 * 
	 * @param delay
	 *            Delay the robot is supposed to stop with.
	 */
	public void stop(int delay) {
		getRobot().sendToArduino(getFlag("stop"), delay);
		if (delay <= 0 && mState != MotorState.STOPPED) {
			setState(MotorState.STOPPED);
		} else {
			setState(MotorState.STOPPINGWITHDELAY);
		}
	}

	/**
	 * Turn right with given angle in deg
	 * 
	 * @param angle
	 *            Angle to turn with in degrees
	 */
	public void turnRight(int angle) {
		getRobot().sendToArduino(getFlag("right"), angle);
		setState(MotorState.TURNING_RIGHT);
	}

	/**
	 * Turn leftwith given angle in deg
	 * 
	 * @param angle
	 *            Angle to turn with in degrees
	 */
	public void turnLeft(int angle) {
		getRobot().sendToArduino(getFlag("left"), angle);
		setState(MotorState.TURNING_LEFT);
	}

	public MotorState getState() {
		return mState;
	}

	private void setState(MotorState state) {
		this.mState = state;
	}

	/**
	 * When delay action is done, the Motor state needs to be set to
	 * {@link MotorState#STOPPED}. This happens after turning or stopping with
	 * delay
	 */
	public void delayActionDone() {
		setState(MotorState.STOPPED);
	}

	/**
	 * The different states the motor can be in
	 * 
	 * @author Frederik Petersen
	 * 
	 */
	public enum MotorState {
		STOPPED, FORWARD, BACKWARD, TURNING_RIGHT, TURNING_LEFT, STOPPINGWITHDELAY
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.fpetersen.robobrain.robot.RobotPart#getRequiredFlagIds()
	 */
	@Override
	protected List<String> getRequiredFlagIds() {
		List<String> requiredFlagIds = new ArrayList<String>();
		requiredFlagIds.add("advance");
		requiredFlagIds.add("backoff");
		requiredFlagIds.add("stop");
		requiredFlagIds.add("left");
		requiredFlagIds.add("right");
		return requiredFlagIds;
	}

}
