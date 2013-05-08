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

import android.hardware.Sensor;
import eu.fpetersen.robobrain.androidsensors.Compass;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.util.SleepHelper;

/**
 * @author Frederik Petersen
 * 
 */
public class CompassBehavior extends Behavior {

	private SleepHelper mSleepHelper;
	private Compass mCompass;

	@Override
	protected void behaviorLoop() {
		float currentDegToNorth = mCompass.getDegreeToNorth();
		if (currentDegToNorth > 90) {
			getRobot().getMainMotor().turnLeft(90);
		} else if (currentDegToNorth > 45) {
			getRobot().getMainMotor().turnLeft(45);
		} else if (currentDegToNorth > 10) {
			getRobot().getMainMotor().turnLeft(10);
		} else if (currentDegToNorth > -10) {
			// Freuen
		} else if (currentDegToNorth > -45) {
			getRobot().getMainMotor().turnRight(10);
		} else if (currentDegToNorth > -90) {
			getRobot().getMainMotor().turnRight(45);
		} else {
			getRobot().getMainMotor().turnRight(90);
		}
		mSleepHelper.sleepMillis(3000);
	}

	@Override
	protected void onStop() {
		mCompass.tearDown();
	}

	@Override
	protected void onStart() {
		mCompass = new Compass();
		mCompass.initialize(getRobot().getRobotService());

		mSleepHelper = new SleepHelper(getRobot().getRobotService());
	}

	@Override
	protected void fillRequirements(Requirements requirements) {
		requirements.addPart("main_motor", Motor.class.getName());
		requirements.addPart(Integer.toString(Sensor.TYPE_MAGNETIC_FIELD),
				Requirements.ANDROID_SENSOR);
		requirements.addPart(Integer.toString(Sensor.TYPE_ACCELEROMETER),
				Requirements.ANDROID_SENSOR);
	}

}
