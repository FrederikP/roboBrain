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

import android.content.Intent;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.services.RobotServiceContainer;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;

/**
 * Holds all RoboBrain Intent String constants
 * 
 * @author Frederik Petersen
 * 
 */
public class RoboBrainIntent extends Intent {

	// -----ACTIONS-----//
	/**
	 * For outputting messages to the console activity
	 */
	public static final String ACTION_OUTPUT = "eu.fpetersen.robobrain.console.intent.action.OUTPUT";

	/**
	 * For turning behaviors on and off.
	 */
	public static final String ACTION_BEHAVIORTRIGGER = "eu.fpetersen.robobrain.console.intent.action.BEHAVIORTRIGGER";

	/**
	 * For turning off all Behaviors, for example when stopping RoboBrain
	 * Service.
	 */
	public static final String ACTION_STOPALLBEHAVIORS = "eu.fpetersen.robobrain.console.intent.actions.STOPALLBEHAVIORS";

	/**
	 * For sending speech results to a {@link DistributingSpeechReceiver}.
	 */
	public static final String ACTION_SPEECH = "eu.fpetersen.robobrain.console.intent.actions.SPEECH";

	/**
	 * For updating the Starter activity UI
	 */
	public static final String ACTION_STARTERUIUPDATE = "eu.fpetersen.robobrain.starter.intent.actions.STARTERUIUPDATE";

	/**
	 * Use when Behavior state changes, add RobotAddress extra
	 */
	public static final String ACTION_BEHAVIORUPDATE = "eu.fpetersen.robobrain.starter.intent.actions.BEHAVIORUPDATE";

	/**
	 * Show alert in Starter activity
	 */
	public static final String ACTION_SHOWALERT = "eu.fpetersen.robobrain.starter.intent.actions.SHOWALERT";

	// -----EXTRAS-----//
	/**
	 * Fill this with message to display in console activity, when sending
	 * intent with {@link RoboBrainIntent#ACTION_OUTPUT}.
	 */
	public static final String EXTRA_OUTPUT = "eu.fpetersen.robobrain.console.intent.extra.OUTPUT";

	/**
	 * Fill this with boolean to state if behavior should be started(true) or
	 * stopped(false) when sending intent with
	 * {@link RoboBrainIntent#ACTION_BEHAVIORTRIGGER}
	 */
	public static final String EXTRA_BEHAVIORSTATE = "eu.fpetersen.robobrain.console.intent.extra.BEHAVIORSTATE";

	/**
	 * Fill this with behaviors UUID to specify which behavior is to be started
	 * when sending intent with {@link RoboBrainIntent#ACTION_BEHAVIORTRIGGER}
	 */
	public static final String EXTRA_BEHAVIORUUID = "eu.fpetersen.robobrain.console.intent.extra.BEHAVIORUUID";

	/**
	 * Fill this with speech results String array when sending intent with
	 * {@link RoboBrainIntent#ACTION_SPEECH}
	 */
	public static final String EXTRA_SPEECH_RESULTS = "eu.fpetersen.robobrain.console.intent.extra.SPEECHRESULTS";

	/**
	 * For sharing the UUID of the {@link RobotService} to access it from
	 * activities per {@link RobotServiceContainer}.
	 */
	public static final String EXTRA_SERVICEID = "eu.fpetersen.robobrain.services.robotservice.extra.SERVICEID";

	/**
	 * The MAC address of the robot
	 */
	public static final String EXTRA_ROBOTADDRESS = "eu.fpetersen.robobrain.robot.ROBOTADDRESS";

	/**
	 * Message to display in Alert
	 */
	public static final String EXTRA_ALERTMESSAGE = "eu.fpetersen.robobrain.starter.intent.actions.ALERTMESSAGE";

	/**
	 * Error tag to display in Alert
	 */
	public static final String EXTRA_ERRORTAG = "eu.fpetersen.robobrain.starter.intent.actions.ERRORTAG";

}
