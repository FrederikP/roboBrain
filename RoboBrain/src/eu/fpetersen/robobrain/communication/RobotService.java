package eu.fpetersen.robobrain.communication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;

public class RobotService extends Service {

	private static final String TAG = "RoboBrain-Service";

	// change this to your Bluetooth device address
	private static final String DEVICE_ADDRESS = "07:12:05:03:53:76";

	private static RobotService instance;

	private boolean running = false;

	private BehaviorReceiver bReceiver;

	private RobotReceiver rReceiver;

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
		CommandCenter.getInstance(DEVICE_ADDRESS);
		bReceiver = new BehaviorReceiver();
		rReceiver = new RobotReceiver();
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
		running = true;
		return START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		Log.v(TAG, "Stopping RoboBrain service");
		running = false;
		unregisterReceiver(bReceiver);
		unregisterReceiver(rReceiver);
		CommandCenter.disconnectAll();
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Destroying RoboBrain service");
		running = false;
		unregisterReceiver(rReceiver);
		unregisterReceiver(bReceiver);
		CommandCenter.disconnectAll();
		super.onDestroy();
	}

}
