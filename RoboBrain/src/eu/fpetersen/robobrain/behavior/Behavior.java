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

import java.util.UUID;

import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.ui.Starter;
import eu.fpetersen.robobrain.util.RoboLog;

//TODO: Requirement-Management for behaviors and robots.

/**
 * The Behavior class should be extended for implementing a specific new Robot
 * Behavior. For now the Robot is able to run one behavior at a time. N
 * behaviors can be created for each robot. It's important to make sure, that
 * the specific robot is compatible with the requirements of the behavior.
 * 
 * When implementing a Behavior class, please make sure to supply a default
 * constructor (i.e. don't specify any constructor). For now there is no way to
 * supply more parameters on initialization. This will change soon.
 * 
 * @author Frederik Petersen
 * 
 */
public abstract class Behavior {

	private boolean turnedOn = false;
	private Robot robot;
	private String name;
	private UUID id;

	/**
	 * Is to be called right after creating a new instance of this class. It
	 * basically has constructor functionality to allow dynamic contruction of
	 * the object (which needs the standard contructor).
	 * 
	 * @param robot
	 *            The robot this behavior is created for.
	 * @param name
	 *            The name of the behavior to be displayed in the UI.
	 */
	public void initialize(Robot robot, String name) {
		this.robot = robot;
		this.name = name;
	}

	/**
	 * Standard constructor to allow dynamically creating different subclasses.
	 * Creates a UUID to identify the behavior, making it easily addressable by
	 * the UI.
	 */
	protected Behavior() {
		id = UUID.randomUUID();
	}

	/**
	 * Starts the behavior, starting it's main loop.
	 */
	public void startBehavior() {
		if (turnedOn == false) {

			turnedOn = true;

			updateBehaviorStatusInUI();

			while (turnedOn) {
				behaviorLoop();
			}

			onStop();
		}
	}

	private void updateBehaviorStatusInUI() {
		// Try updating UI here
		Starter starter = Starter.getInstance();
		if (starter != null) {
			starter.updateUIDueToBehaviorStateSwitch(getRobot());
		}
	}

	/**
	 * Turns the behavior off, stopping it's main loop.
	 */
	public void stopBehavior() {
		this.turnedOn = false;
		updateBehaviorStatusInUI();
	}

	/**
	 * 
	 * @return Name to be displayed by UI, as given in xml config
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return The identifying id of this behavior
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * 
	 * @return True if beahvior is turned on, else false
	 */
	public boolean isTurnedOn() {
		return turnedOn;
	}

	/**
	 * Sends an intent to the Console UI, which is then able to display the
	 * message to the user
	 * 
	 * @param message
	 *            Message that is to be displayed in the Console UI
	 */
	public void toConsole(String message) {
		RoboLog.log(getRobot().getRobotService(), message);
	}

	/**
	 * 
	 * @return The robot that owns this behavior
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * This is the main loop of the behavior. All the logic happens here. Try to
	 * avoid making the thread sleep that's running this loop. Look for examples
	 * in existing behaviors
	 */
	protected abstract void behaviorLoop();

	/**
	 * Called when behavior is stopped. Try undoing everything that changed the
	 * robot during the behavior, except maybe its new location
	 */
	protected abstract void onStop();

}
