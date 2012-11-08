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
import java.util.Map;

import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotPart;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Saves the Requirements of a behavior, so that only robots, which satisfy
 * these requirements are able to run the behavior
 * 
 * @author Frederik Petersen
 * 
 */
public class Requirements {

	public Map<String, String> partsRequired = new HashMap<String, String>();

	/**
	 * Add a part that is required to run the behavior.
	 * 
	 * @param name
	 *            Identifying id of part
	 * @param part
	 *            Actual Part class
	 */
	public void addPart(String id, String partType) {
		partsRequired.put(id, partType);
	}

	public boolean fulfillsRequirements(Robot robot) {
		for (String id : partsRequired.keySet()) {
			RobotPart part = robot.getPart(id);
			if (part == null) {
				RoboLog.alertError(
						robot.getRobotService(),
						"Robot "
								+ robot.getName()
								+ " does not fulfill Requirements: Part with id "
								+ id + " could not be found");
				return false;
			}
			String typeName = part.getClass().getName();
			if (!typeName.matches(partsRequired.get(id))) {
				RoboLog.alertError(
						robot.getRobotService(),
						"Robot "
								+ robot.getName()
								+ " does not fulfill Requirements: Part with id "
								+ id + " is not of type: "
								+ partsRequired.get(id));
				return false;
			}
		}

		return true;
	}

}
