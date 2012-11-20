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

import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.util.RoboLog;

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

	private boolean mTurnedOn = false;
	private Robot mRobot;
	private String mName;
	private UUID mId;
	private String mSpeechName;
	private Requirements mRequirements = new Requirements();

	protected RoboLog mLog;

	/**
	 * Is to be called right after creating a new instance of this class. It
	 * basically has constructor functionality to allow dynamic contruction of
	 * the object (which needs the standard contructor).
	 * 
	 * @param robot
	 *            The robot this behavior is created for.
	 * @param name
	 *            The name of the behavior to be displayed in the UI.
	 * @param speechName
	 *            The name that the behavior should react to when working with
	 *            voice commands
	 */
	public void initialize(Robot robot, String name, String speechName) {
		this.mRobot = robot;
		this.mName = name;
		this.mSpeechName = speechName;
		mLog = new RoboLog("Behavior", robot.getRobotService());
	}

	/**
	 * Standard constructor to allow dynamically creating different subclasses.
	 * Creates a UUID to identify the behavior, making it easily addressable by
	 * the UI. If you overwrite this constructor, make sure calling super();
	 */
	protected Behavior() {
		mId = UUID.randomUUID();
		fillRequirements(mRequirements);
	}

	/**
	 * Starts the behavior, starting it's main loop.
	 */
	public void startBehavior() {
		Runnable startTask = new Runnable() {

			public void run() {
				mLog.log("Started behavior: " + mName + " for robot: " + getRobot().getName(), true);
				if (mTurnedOn == false) {

					mTurnedOn = true;

					onStart();

					updateBehaviorStatusInUI();

					while (mTurnedOn) {
						behaviorLoop();
					}

					onStop();
				}
			}
		};
		Thread thread = new Thread(startTask);
		thread.start();

	}

	/**
	 * Call this everytime you want to update the Behavior state in the Starter
	 * activity
	 */
	private void updateBehaviorStatusInUI() {
		// Try updating UI here
		Intent intent = new Intent(RoboBrainIntent.ACTION_BEHAVIORUPDATE);
		intent.putExtra(RoboBrainIntent.EXTRA_ROBOTADDRESS, getRobot().getAddress());
		getRobot().getRobotService().sendBroadcast(intent);
	}

	/**
	 * Turns the behavior off, stopping it's main loop.
	 */
	public void stopBehavior() {
		this.mTurnedOn = false;
		mLog.log("Stopped behavior: " + mName + " for robot: " + getRobot().getName(), true);
		updateBehaviorStatusInUI();
	}

	/**
	 * 
	 * @return Name to be displayed by UI, as given in xml config
	 */
	public String getName() {
		return mName;
	}

	/**
	 * 
	 * @return The identifying id of this behavior
	 */
	public UUID getId() {
		return mId;
	}

	/**
	 * 
	 * @return True if beahvior is turned on, else false
	 */
	public boolean isTurnedOn() {
		return mTurnedOn;
	}

	/**
	 * Sends an intent to the Console UI, which is then able to display the
	 * message to the user
	 * 
	 * @param message
	 *            Message that is to be displayed in the Console UI
	 */
	public void toConsole(String message) {
		mLog.log(message, true);
	}

	/**
	 * 
	 * @return The robot that owns this behavior
	 */
	public Robot getRobot() {
		return mRobot;
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

	/**
	 * Called when behavior is started, before behavior Loop is started. Do any
	 * setup stuff here.
	 */
	protected abstract void onStart();

	/**
	 * Use this to fill the requirements
	 * 
	 * See other Behaviors for examples on how to do that. If it's not filled
	 * Requirements will not be checked and if a robot is not capable of running
	 * the behavior it's likely to crash with a NullPointer
	 */
	protected abstract void fillRequirements(Requirements requirements);

	public Requirements getRequirements() {
		return mRequirements;
	}

	public String getSpeechName() {
		return mSpeechName;
	}

}
