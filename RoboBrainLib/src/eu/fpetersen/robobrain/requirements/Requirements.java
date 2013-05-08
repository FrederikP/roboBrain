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
package eu.fpetersen.robobrain.requirements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.parts.RobotPart;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Saves the Requirements of a behavior, so that only robots, which satisfy
 * these requirements are able to run the behavior
 * 
 * @author Frederik Petersen
 * 
 */
public class Requirements {

	public static final String ANDROID_SENSOR = "ANDROID_SENSOR";
	public Map<String, String> mPartsRequired = new HashMap<String, String>();
	private SensorManager mSensorManager;

	/**
	 * Add a part that is required to run the behavior.
	 * 
	 * @param name
	 *            Identifying id of part
	 * @param part
	 *            Actual Part class
	 */
	public void addPart(String id, String partType) {
		mPartsRequired.put(id, partType);
	}

	public boolean fulfillsRequirements(Robot robot, Context context) {
		RoboLog log = new RoboLog("Requirements", robot.getRobotService());
		for (Entry<String, String> requiredPart : mPartsRequired.entrySet()) {
			String id = requiredPart.getKey();
			String type = requiredPart.getValue();
			if (type.matches(ANDROID_SENSOR)) {
				if (mSensorManager == null) {
					mSensorManager = (SensorManager) context
							.getSystemService(Context.SENSOR_SERVICE);
				}
				List<Sensor> availSensors = mSensorManager.getSensorList(Integer.parseInt(id));
				if (availSensors.size() < 1) {
					log.alertError("Robot " + robot.getName()
							+ " does not fulfill Requirements: Androidsensor with id " + id
							+ " is not available");
					return false;
				}
			} else {
				RobotPart part = robot.getPart(requiredPart.getKey());
				if (part == null) {
					log.alertError("Robot " + robot.getName()
							+ " does not fulfill Requirements: Part with id " + id
							+ " could not be found");
					return false;
				}
				String typeName = part.getClass().getName();
				if (!typeName.matches(type)) {
					log.alertError("Robot " + robot.getName()
							+ " does not fulfill Requirements: Part with id " + id
							+ " is not of type: " + mPartsRequired.get(id));
					return false;
				}
			}
		}

		return true;
	}
}
