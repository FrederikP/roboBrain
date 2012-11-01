package eu.fpetersen.robobrain.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorMappingFactory;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;

public class RobotService extends Service {

	private static final String TAG = "RoboBrain-Service";

	private static RobotService instance;

	private boolean running = false;

	private BehaviorReceiver bReceiver;

	private RobotReceiver rReceiver;

	private DistributingSpeechReceiver dSpeechReceiver;

	public boolean isRunning() {
		return running;
	}

	public static RobotService getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "Creating RoboBrain service");
		instance = this;
		RobotFactory robotFactory = RobotFactory.getInstance(this);
		Map<String, Robot> robots = robotFactory.createRobots();
		BehaviorMappingFactory behaviorFactory = BehaviorMappingFactory
				.getInstance();
		Map<String, List<String>> behaviorMapping = behaviorFactory
				.createMappings();
		for (String robotName : robots.keySet()) {
			// TODO Exception handling
			Robot robot = robots.get(robotName);
			List<String> behaviorNames = behaviorMapping.get(robotName);
			List<Behavior> behaviors = new ArrayList<Behavior>();
			for (String bName : behaviorNames) {
				behaviors.add(Behavior.createBehavior(bName, robot));
			}
			CommandCenter.createInstance(robot, behaviors);
		}

		bReceiver = new BehaviorReceiver();
		rReceiver = new RobotReceiver();
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
		CommandCenter.connectAll();
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
		return START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		Log.v(TAG, "Stopping RoboBrain service");
		running = false;
		CommandCenter.disconnectAll();
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Destroying RoboBrain service");
		running = false;
		unregisterReceiver(rReceiver);
		unregisterReceiver(bReceiver);
		unregisterReceiver(dSpeechReceiver);
		CommandCenter.disconnectAll();

		super.onDestroy();
	}

	public DistributingSpeechReceiver getDistributingSpeechReceiver() {
		return dSpeechReceiver;
	}

}
