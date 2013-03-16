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
package eu.fpetersen.robobrain.robot;

import java.util.Map;

import eu.fpetersen.robobrain.robot.parts.RobotPart;

/**
 * Takes care of managing the parameters that are needed for initializing the
 * RobotPart
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotPartInitializer {

	private Robot mRobot;

	private Map<String, Character> mFlags;

	private String mId;

	private boolean requirementsMet = false;

	public RobotPartInitializer(String id, Robot robot, Map<String, Character> flags) {
		this.mRobot = robot;
		this.mFlags = flags;
		this.mId = id;
	}

	/**
	 * Initialize the {@link RobotPart} with the parameters stored in this
	 * {@link RobotPartInitializer}
	 * 
	 * @param part
	 *            {@link RobotPart} to initialize
	 */
	public void initialize(RobotPart part) {
		requirementsMet = part.initialize(mId, mRobot, mFlags);
	}

	public boolean areRequirementsMet() {
		return requirementsMet;
	}

}
