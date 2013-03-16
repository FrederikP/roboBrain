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
package eu.fpetersen.robobrain.behavior.switching;

import java.util.UUID;

import android.content.IntentFilter;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Central behavior switcher that allows external elements to change the running
 * behavior for robots. Intents are recognized by a {@link BehaviorReceiver} and
 * speech controlled switching is enabled with a
 * {@link SpeechControlledBehaviorSwitcher}.
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorSwitcher {

	private RobotService mService;

	private BehaviorReceiver mBehaviorReceiver;

	private SpeechControlledBehaviorSwitcher mSpeechControlledBehaviorSwitcher;

	private RoboLog mLog;

	public BehaviorSwitcher(RobotService service) {
		mService = service;

		mLog = new RoboLog("BehaviorSwitcher", service);

		setupBehaviorReceiver();

		setupSpeechControlledBehaviorReceiver();

	}

	/**
	 * Sets up the {@link SpeechControlledBehaviorSwitcher} to receive voice
	 * commands and call the correct methods in the {@link BehaviorSwitcher}
	 */
	private void setupSpeechControlledBehaviorReceiver() {
		mSpeechControlledBehaviorSwitcher = new SpeechControlledBehaviorSwitcher(
				BehaviorSwitcher.this);

		mService.getDistributingSpeechReceiver().setSpeechControlledBehaviorSwitcher(
				mSpeechControlledBehaviorSwitcher);
	}

	/**
	 * Sets up the {@link BehaviorReceiver} to receive switching signals (for
	 * example from UI)
	 */
	private void setupBehaviorReceiver() {
		mBehaviorReceiver = new BehaviorReceiver(BehaviorSwitcher.this);
		IntentFilter behaviorReceiverFilter = new IntentFilter();
		behaviorReceiverFilter.addAction(RoboBrainIntent.ACTION_BEHAVIORTRIGGER);
		behaviorReceiverFilter.addAction(RoboBrainIntent.ACTION_STOPALLBEHAVIORS);
		mService.registerReceiver(mBehaviorReceiver, behaviorReceiverFilter);
	}

	/**
	 * Call this when service ends
	 */
	public void destroy() {
		mService.unregisterReceiver(mBehaviorReceiver);
	}

	/**
	 * Starts Behavior identified by {@link UUID}. Does not start it, if it's
	 * already running. If other Behavior is running, it's shut down first. Then
	 * new behavior is started.
	 * 
	 * @param uuid
	 *            Identifies behavior to be started
	 */
	public void startBehavior(UUID uuid) {
		final Behavior b = mService.getBehaviorForUUID(uuid);
		if (b == null || b.getRobot() == null) {
			mLog.alertError("No CC for Behavior UUID: " + uuid);
			return;
		}
		CommandCenter cc = mService.getCCForAddress(b.getRobot().getAddress());

		stopRunningBehavior(cc);

		b.startBehavior();

	}

	/**
	 * Stops running behavior of supplied command center
	 * 
	 * @param cc
	 *            CommandCenter that is checked for running Behavior
	 */
	private void stopRunningBehavior(CommandCenter cc) {
		Behavior runningBehavior = cc.getRunningBehavior();
		if (runningBehavior != null) {
			mLog.log(
					"Behavior with the follwing UUID is being stopped: " + runningBehavior.getId(),
					true);
			runningBehavior.stopBehavior();
		}
	}

	/**
	 * Stops Behavior identified by {@link UUID}. If it's not started, nothing
	 * happens.
	 * 
	 * @param uuid
	 *            Identifies behavior to be stopped
	 */
	public void stopBehavior(UUID uuid) {
		final Behavior b = mService.getBehaviorForUUID(uuid);
		if (b == null) {
			mLog.alertError("Could not find behavior for UUID: " + uuid);
			return;
		}
		b.stopBehavior();
	}

	/**
	 * @return
	 */
	public RobotService getRobotService() {
		return mService;
	}

	/**
	 * Turns off all running behaviors
	 */
	public void stopAllBehaviors() {
		for (CommandCenter cc : mService.getAllCCs()) {
			Behavior runningB = cc.getRunningBehavior();
			if (runningB != null) {
				runningB.stopBehavior();
			}
		}
	}

}
