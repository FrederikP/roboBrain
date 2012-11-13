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

/**
 * @author Frederik Petersen
 * 
 * 
 *         When implementing a RobotPart class, please make sure to supply a
 *         default constructor (i.e. don't specify any constructor). For now
 *         there is no way to supply more parameters on initialization. This
 *         will change soon. Initialization values will then be entered through
 *         the .xml config file of the robot.
 * 
 */
public abstract class RobotPart {
	private Robot mRobot;

	/**
	 * This needs to be called after instantiated the RobotPart and it exists
	 * because the default constructor is needed to dynamically decide which
	 * subclass is to be created
	 * 
	 * @param robot
	 *            Robot the Part belongs to.
	 */
	protected void initialize(Robot robot) {
		this.mRobot = robot;
	}

	protected RobotPart() {
	}

	public Robot getRobot() {
		return mRobot;
	}

}
