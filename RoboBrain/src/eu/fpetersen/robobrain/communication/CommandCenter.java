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
package eu.fpetersen.robobrain.communication;

import java.util.ArrayList;
import java.util.List;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.robot.Robot;

/**
 * Each command center holds a reference to one robot and it's behaviors. The
 * static context of the class holds a reference to a Map of all Behaviors and
 * their identifying UUIDs.
 * 
 * @author Frederik Petersen
 * 
 */
public class CommandCenter {

	private Robot robot;
	private List<Behavior> behaviors = new ArrayList<Behavior>();

	/**
	 * Creates a new CommandCenter for the given robot and it's behaviors.
	 * 
	 * @param robot
	 *            The robot this CommandCenter is created for
	 * @param behaviors
	 *            The behaviors of the robot above.
	 */
	public CommandCenter(Robot robot, List<Behavior> behaviors) {
		this.robot = robot;

		addBehaviors(behaviors);
	}

	/**
	 * Add behaviors to robot
	 * 
	 * @param behaviors
	 *            List of behaviors to add
	 */
	private void addBehaviors(List<Behavior> behaviors) {
		for (Behavior b : behaviors) {
			this.behaviors.add(b);
		}
	}

	/**
	 * Connect to the Arduino device of this CommandCenters Robot.
	 */
	public void connect() {
		RobotService rService = robot.getRobotService();

		Amarino.connect(rService, robot.getAddress());
	}

	/**
	 * Disconnect from the Arduino device of this CommandCenters Robot.
	 */
	public void disconnect() {

		RobotService rService = robot.getRobotService();

		Amarino.disconnect(rService, robot.getAddress());
	}

	/**
	 * 
	 * @return The robot of this CommandCenter
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * 
	 * @return The behaviors of this CommandCenters robot.
	 */
	public List<Behavior> getBehaviors() {
		return behaviors;
	}

}
