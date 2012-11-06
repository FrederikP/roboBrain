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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorFactory;
import eu.fpetersen.robobrain.behavior.BehaviorMappingFactory;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechReceiver;
import eu.fpetersen.robobrain.ui.Starter;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Main Service of this App. When started from {@link Starter} activity, it
 * creates robots and behaviors as configured in xml files on sdcard.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotService extends Service {

	private static final String TAG = "RoboBrain-Service";

	/**
	 * Static reference to a Map of all existing CommandCenters keyed by their
	 * Robots.
	 */
	private Map<Robot, CommandCenter> ccPerRobot = new HashMap<Robot, CommandCenter>();

	/**
	 * Static reference to a Map of all existing Behaviors identified by their
	 * UUIDs. By having this additional Map, Behaviors can easily be triggered
	 * from other components like the UI
	 */
	private Map<UUID, Behavior> allBehaviors = new HashMap<UUID, Behavior>();

	protected boolean running = false;

	private BehaviorReceiver bReceiver;

	private RobotReceiver rReceiver;

	private DistributingSpeechReceiver dSpeechReceiver;

	/**
	 * 
	 * @return True if service is running, false if not.
	 */
	public boolean isRunning() {
		return running;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "Creating RoboBrain service");

		bReceiver = new BehaviorReceiver(RobotService.this);
		rReceiver = new RobotReceiver(RobotService.this);
		dSpeechReceiver = new DistributingSpeechReceiver();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Starting RoboBrain service");
		if (getAllCCs().isEmpty()) {
			RobotFactory robotFactory = RobotFactory.getInstance(this);
			Map<String, Robot> robots = robotFactory.createRobots();
			BehaviorMappingFactory behaviorFactory = BehaviorMappingFactory
					.getInstance(RobotService.this);
			// Enter null for standard sd location
			Map<String, List<String>> behaviorMapping = behaviorFactory
					.createMappings(null);
			BehaviorFactory bFac = BehaviorFactory
					.getInstance(RobotService.this);
			for (String robotName : robots.keySet()) {
				// TODO Exception handling
				Robot robot = robots.get(robotName);
				List<String> behaviorNames = behaviorMapping.get(robotName);
				List<Behavior> behaviors = new ArrayList<Behavior>();
				for (String bName : behaviorNames) {
					behaviors.add(bFac.createBehavior(bName, robot));
				}
				createCommandCenter(robot, behaviors);
			}
		}

		connectAll();
		IntentFilter behaviorReceiverFilter = new IntentFilter();
		behaviorReceiverFilter
				.addAction(RoboBrainIntent.ACTION_BEHAVIORTRIGGER);
		behaviorReceiverFilter
				.addAction(RoboBrainIntent.ACTION_STOPALLBEHAVIORS);
		registerReceiver(bReceiver, behaviorReceiverFilter);

		// in order to receive broadcasted intents we need to register our
		// receiver
		registerReceiver(rReceiver, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));

		// Register distributing Speech Receiver to redistribute Speech input
		// from Speech Recognition Service to all registered Speech Receivers
		registerReceiver(dSpeechReceiver, new IntentFilter(
				RoboBrainIntent.ACTION_SPEECH));

		running = true;

		updateStarterUI(RobotService.this);

		return START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		Log.v(TAG, "Stopping RoboBrain service");
		running = false;
		disconnectAll();
		removeAllCCsAndBehaviors();
		updateStarterUI(RobotService.this);

		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Destroying RoboBrain service");
		if (running) {
			running = false;
			unregisterReceiver(rReceiver);
			unregisterReceiver(bReceiver);
			unregisterReceiver(dSpeechReceiver);
			disconnectAll();
			removeAllCCsAndBehaviors();
		}

		updateStarterUI(null);

		super.onDestroy();
	}

	/**
	 * Try updating status information in UI
	 */
	private void updateStarterUI(RobotService service) {
		Starter starter = Starter.getInstance();
		if (starter != null) {
			Starter.getInstance().setRobotService(service);
		} else {
			RoboLog.log(RobotService.this,
					"Status couldn't be updated in UI as Starter activity was not found");
		}
	}

	/**
	 * 
	 * @return The {@link DistributingSpeechReceiver} of the service which is
	 *         supposed to distribute the speech input results to all registered
	 *         {@link SpeechReceiver}s
	 */
	public DistributingSpeechReceiver getDistributingSpeechReceiver() {
		return dSpeechReceiver;
	}

	/**
	 * Creates a new CommandCenter for the given Robot and behaviors and adds it
	 * the static map of all Command Centers.
	 * 
	 * @param robot
	 *            The robot this CommandCenter is created for
	 * @param behaviors
	 *            The behaviors of the robot above.
	 */
	private void createCommandCenter(Robot robot, List<Behavior> behaviors) {
		CommandCenter cc = ccPerRobot.get(robot);
		if (cc == null) {
			cc = new CommandCenter(robot, behaviors);
			ccPerRobot.put(robot, cc);
			for (Behavior b : behaviors) {
				allBehaviors.put(b.getId(), b);
			}
		}
	}

	/**
	 * Connect to all Arduino Devices
	 */
	private void connectAll() {
		for (CommandCenter cc : ccPerRobot.values()) {
			cc.connect();
		}
	}

	/**
	 * Disconnect from all Arduino Devices
	 */
	private void disconnectAll() {
		for (CommandCenter cc : ccPerRobot.values()) {
			cc.disconnect();
		}
	}

	/**
	 * Get all existing CommandCenters
	 * 
	 * @return All existing CommandCenters
	 */
	public Collection<CommandCenter> getAllCCs() {
		return ccPerRobot.values();
	}

	/**
	 * Get the CommandCenter for the specified MACAddress
	 * 
	 * @param address
	 *            MAC address of the Arduino Device
	 * @return CommandCenter for the specified MACAddress
	 */
	public CommandCenter getCCForAddress(String address) {
		for (CommandCenter center : ccPerRobot.values()) {
			if (center.getRobot().getAddress().matches(address)) {
				return center;
			}
		}
		// TODO Exception handling
		return null;
	}

	/**
	 * Remove all CommandCenters and behavior references
	 */
	public void removeAllCCsAndBehaviors() {
		ccPerRobot.clear();
		allBehaviors.clear();
	}

	/**
	 * Return behavior for UUID. If it does not exist null is returned.
	 * 
	 * @param uuid
	 *            Identifies the Behavior
	 * @return The behavior identified by the UUID
	 */
	public Behavior getBehaviorForUUID(UUID uuid) {
		if (allBehaviors.containsKey(uuid)) {
			return allBehaviors.get(uuid);
		} else {
			return null;
		}
	}

}
