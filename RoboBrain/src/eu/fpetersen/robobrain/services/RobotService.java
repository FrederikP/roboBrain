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
package eu.fpetersen.robobrain.services;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorFactory;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.behavior.BehaviorMappingFactory;
import eu.fpetersen.robobrain.behavior.switching.BehaviorSwitcher;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotReceiver;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechReceiver;
import eu.fpetersen.robobrain.ui.Starter;
import eu.fpetersen.robobrain.util.ExternalStorageManager;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Main Service of this App. When started from {@link Starter} activity, it
 * creates robots and behaviors as configured in xml files on sdcard.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotService extends Service {

	/**
	 * Static reference to a Map of all existing CommandCenters keyed by their
	 * Robots.
	 */
	private Map<Robot, CommandCenter> mCcPerRobot = new HashMap<Robot, CommandCenter>();

	/**
	 * Static reference to a Map of all existing Behaviors identified by their
	 * UUIDs. By having this additional Map, Behaviors can easily be triggered
	 * from other components like the UI
	 */
	private Map<UUID, Behavior> mAllBehaviors = new HashMap<UUID, Behavior>();

	protected boolean mRunning = false;

	private BehaviorSwitcher mBehaviorSwitcher;

	private RobotReceiver mRobotReceiver;

	private DistributingSpeechReceiver mDistributingSpeechReceiver;

	private BroadcastReceiver mDisconnectedReceiver;

	protected PowerManager.WakeLock mWakeLock;

	private UUID id;

	protected RoboLog mLog;

	/**
	 * 
	 * @return True if service is running, false if not.
	 */
	public boolean isRunning() {
		return mRunning;
	}

	@Override
	public void onCreate() {
		mLog = new RoboLog("RobotService", RobotService.this);

		mLog.log("Creating RoboBrain service", true);

		id = RobotServiceContainer.addRobotService(RobotService.this);

		// Make sure device stays awake
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "RoboBrain");
		mWakeLock.acquire();

		mRobotReceiver = new RobotReceiver(RobotService.this);
		mDistributingSpeechReceiver = new DistributingSpeechReceiver();

		mDisconnectedReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (getAllCCs() != null) {
					if (intent.getAction().matches(AmarinoIntent.ACTION_CONNECTED)) {
						String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
						CommandCenter cc = getCCForAddress(address);
						if (cc != null) {
							String robotName = cc.getRobot().getName();
							mLog.alertError(robotName + " unexpectedly disconnected.");
						}

					}
				}
			}
		};

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String testingEnvvar;
		try {
			testingEnvvar = System.getProperty(getString(R.string.envvar_testing));
		} catch (Exception e) {
			testingEnvvar = null;
		}
		final boolean testing = (testingEnvvar != null && testingEnvvar.matches("true"));
		mLog.log("Starting RoboBrain service", true);
		if (getAllCCs().isEmpty()) {
			if (testing) {
				setupCommandCentersTest();
			} else {
				setupCommandCenters();
			}
		}

		final Map<String, Boolean> connectionTable = new HashMap<String, Boolean>();

		// Setup IntentFilter to receive Intents from Amarino about Connection
		// details
		IntentFilter amarinoConnectionFilter = new IntentFilter();
		amarinoConnectionFilter.addAction(AmarinoIntent.ACTION_CONNECTED);
		amarinoConnectionFilter.addAction(AmarinoIntent.ACTION_CONNECTION_FAILED);
		final BroadcastReceiver connectionDetailReceiver = setupConnectionDetailReceiver(connectionTable);
		registerReceiver(connectionDetailReceiver, amarinoConnectionFilter);

		connectAll();

		Runnable doThatInNewThread = new Runnable() {

			public void run() {
				if (!testing) {
					checkConnectionDetails(connectionTable, 16);
				}

				unregisterReceiver(connectionDetailReceiver);

				// in order to receive broadcasted intents we need to register
				// our
				// receiver
				registerReceiver(mRobotReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));

				// Register distributing Speech Receiver to redistribute Speech
				// input
				// from Speech Recognition Service to all registered Speech
				// Receivers
				registerReceiver(mDistributingSpeechReceiver, new IntentFilter(
						RoboBrainIntent.ACTION_SPEECH));

				mBehaviorSwitcher = new BehaviorSwitcher(RobotService.this);

				mRunning = true;

				registerReceiver(mDisconnectedReceiver, new IntentFilter(
						AmarinoIntent.ACTION_DISCONNECTED));

				updateStarterUI(false);

			}
		};

		Thread restThread = new Thread(doThatInNewThread);

		restThread.start();

		return START_STICKY;
	}

	/**
	 * Returned Broadcast Receiver will listen for connectiond detail intents
	 * from Amarino
	 * 
	 * @param connectionTable
	 *            this table will be filled with connection information about
	 *            device.
	 * @return The configured broadcast receiver
	 */
	private BroadcastReceiver setupConnectionDetailReceiver(
			final Map<String, Boolean> connectionTable) {
		BroadcastReceiver connectionDetailReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().matches(AmarinoIntent.ACTION_CONNECTED)) {
					String mac = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
					connectionTable.put(mac, true);
				} else if (intent.getAction().matches(AmarinoIntent.ACTION_CONNECTION_FAILED)) {
					String mac = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
					connectionTable.put(mac, false);
				}
			}
		};
		return connectionDetailReceiver;
	}

	/**
	 * Waits and checks the connection table for max. 8 secs
	 * 
	 * @param connectionTable
	 */
	private void checkConnectionDetails(final Map<String, Boolean> connectionTable, int timeout) {

		int waitedSeconds = 0;
		while (connectionTable.size() < getAllCCs().size() && waitedSeconds < timeout) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				mLog.alertError("Thread was interupted while waiting for connection status.");
			}
			waitedSeconds++;
		}

		if (waitedSeconds >= timeout) {
			if (connectionTable.size() == 0) {
				mLog.alertError("There was no response from Amarino. Have you installed Amarino toolkit?");
			} else {
				mLog.alertError("There was no response from Amarino for at least one device. "
						+ "Are you sure the MAC addresses in the config are correct? Is every robot"
						+ " registered with Amarino?");
			}
		}

		for (Entry<String, Boolean> connectionEntry : connectionTable.entrySet()) {
			if (!connectionEntry.getValue()) {
				String robotName = getCCForAddress(connectionEntry.getKey()).getRobot().getName();
				// Device was successfully registered with Amarino. The
				// connection failed anyways. Turned off?
				mLog.alertError("Connection failed for robot: " + robotName
						+ ". Are you sure this robot is turned on?");
			}
		}

	}

	/**
	 * This is called when running integration tests. It does not require the sd
	 * card to be filled with conf files. Files are loaded from App's raw
	 * resources
	 */
	private void setupCommandCentersTest() {
		RobotFactory robotFactory = RobotFactory.getInstance(this);
		InputStream robotFile = getResources().openRawResource(R.raw.testbot);
		Robot robot = robotFactory.createRobotFromXml(robotFile);
		if (robot == null) {
			mLog.alertError("Setting up test robot failed. What the heck?");
		}
		BehaviorMappingFactory behaviorFactory = BehaviorMappingFactory
				.getInstance(RobotService.this);
		InputStream mappingFile = getResources().openRawResource(R.raw.behaviormapping);
		Map<String, List<BehaviorInitializer>> behaviorMapping = behaviorFactory
				.createMappings(mappingFile);
		BehaviorFactory bFac = BehaviorFactory.getInstance(RobotService.this);

		List<BehaviorInitializer> behaviorInitializers = behaviorMapping.get(robot.getName());
		List<Behavior> behaviors = new ArrayList<Behavior>();
		for (BehaviorInitializer initializer : behaviorInitializers) {
			Behavior behavior = bFac.createBehavior(initializer, robot);
			if (behavior.getRequirements().fulfillsRequirements(robot)) {
				behaviors.add(behavior);
			}
		}
		createCommandCenter(robot, behaviors);

	}

	/**
	 * This the the Standard CommandCenter setup. It uses the xml conf files to
	 * setup Robot's and their behaviors
	 */
	private void setupCommandCenters() {
		ExternalStorageManager esManager = new ExternalStorageManager(RobotService.this);

		if (!esManager.sdCardIsMounted()) {
			mLog.alertWarning("No sdcard mounted. No robot data could be loaded from robobrain dir.");
			return;
		}

		File robotsXmlDir = esManager.getRobotsXmlDir();
		RobotFactory robotFactory = RobotFactory.getInstance(this);
		Map<String, Robot> robots = robotFactory.createRobots(robotsXmlDir);
		if (robots.size() == 0) {
			mLog.alertWarning("No robot configured. See documentation for explanation on xml config.");
		}
		BehaviorMappingFactory behaviorFactory = BehaviorMappingFactory
				.getInstance(RobotService.this);
		// Enter null for standard sd location
		Map<String, List<BehaviorInitializer>> behaviorMapping = behaviorFactory
				.createMappings(null);
		if (behaviorMapping != null) {
			BehaviorFactory bFac = BehaviorFactory.getInstance(RobotService.this);
			for (Entry<String, Robot> robotEntry : robots.entrySet()) {
				Robot robot = robotEntry.getValue();
				List<BehaviorInitializer> behaviorInitializers = behaviorMapping.get(robotEntry
						.getKey());
				List<Behavior> behaviors = new ArrayList<Behavior>();
				for (BehaviorInitializer initializer : behaviorInitializers) {
					Behavior behavior = bFac.createBehavior(initializer, robot);
					if (behavior.getRequirements().fulfillsRequirements(robot)) {
						behaviors.add(behavior);
					}
				}
				createCommandCenter(robot, behaviors);
			}
		}
	}

	@Override
	public boolean stopService(Intent name) {
		mLog.log("Stopping RoboBrain service", true);
		if (mRunning) {
			stopRunningService();
		}
		updateStarterUI(false);

		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		mLog.log("Destroying RoboBrain service", true);
		if (mRunning) {
			stopRunningService();
		}

		updateStarterUI(true);

		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}

		RobotServiceContainer.removeRobotService(id);

		super.onDestroy();
	}

	/**
	 * Try updating status information in UI
	 */
	private void updateStarterUI(boolean removeReference) {
		Intent intent = new Intent(RoboBrainIntent.ACTION_STARTERUIUPDATE);
		if (!removeReference) {
			intent.putExtra(RoboBrainIntent.EXTRA_SERVICEID, id);
		}
		sendBroadcast(intent);
	}

	/**
	 * 
	 * @return The {@link DistributingSpeechReceiver} of the service which is
	 *         supposed to distribute the speech input results to all registered
	 *         {@link SpeechReceiver}s
	 */
	public DistributingSpeechReceiver getDistributingSpeechReceiver() {
		return mDistributingSpeechReceiver;
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
	protected void createCommandCenter(Robot robot, List<Behavior> behaviors) {
		CommandCenter cc = mCcPerRobot.get(robot);
		if (cc == null) {
			cc = new CommandCenter(robot, behaviors);
			mCcPerRobot.put(robot, cc);
			for (Behavior b : behaviors) {
				mAllBehaviors.put(b.getId(), b);
			}
		}
	}

	/**
	 * Connect to all Arduino Devices
	 */
	private void connectAll() {
		for (CommandCenter cc : mCcPerRobot.values()) {
			cc.connect();
		}
	}

	/**
	 * Disconnect from all Arduino Devices
	 */
	private void disconnectAll() {
		for (CommandCenter cc : mCcPerRobot.values()) {
			cc.disconnect();
		}
	}

	/**
	 * Get all existing CommandCenters
	 * 
	 * @return All existing CommandCenters
	 */
	public Collection<CommandCenter> getAllCCs() {
		return mCcPerRobot.values();
	}

	/**
	 * Get the CommandCenter for the specified MACAddress
	 * 
	 * @param address
	 *            MAC address of the Arduino Device
	 * @return CommandCenter for the specified MACAddress
	 */
	public CommandCenter getCCForAddress(String address) {
		for (CommandCenter center : mCcPerRobot.values()) {
			if (center.getRobot().getAddress().matches(address)) {
				return center;
			}
		}
		mLog.alertError("CommandCenter not found for MAC: " + address);
		return null;
	}

	/**
	 * Remove all CommandCenters and behavior references
	 */
	public void removeAllCCsAndBehaviors() {
		mCcPerRobot.clear();
		mAllBehaviors.clear();
	}

	/**
	 * Return behavior for UUID. If it does not exist null is returned.
	 * 
	 * @param uuid
	 *            Identifies the Behavior
	 * @return The behavior identified by the UUID
	 */
	public Behavior getBehaviorForUUID(UUID uuid) {
		if (mAllBehaviors.containsKey(uuid)) {
			return mAllBehaviors.get(uuid);
		} else {
			return null;
		}
	}

	/**
	 * Do all what's required to disconnect and stop the RoboBrain connections
	 * and service
	 */
	private void stopRunningService() {
		unregisterReceiver(mDisconnectedReceiver);
		mRunning = false;
		unregisterReceiver(mRobotReceiver);

		unregisterReceiver(mDistributingSpeechReceiver);
		if (mBehaviorSwitcher != null) {
			mBehaviorSwitcher.destroy();
		}
		disconnectAll();
		removeAllCCsAndBehaviors();
	}

	/**
	 * 
	 * @return A map containing all behaviors with UUIDs.
	 */
	public Map<UUID, Behavior> getAllBehaviors() {
		return mAllBehaviors;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
