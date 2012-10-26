package eu.fpetersen.robobrain.communication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechRecognizerService;

public class RobotService extends Service {

	private static final String TAG = "RoboBrain-Service";

	// change this to your Bluetooth device address
	private static final String DEVICE_ADDRESS = "07:12:05:03:53:76";

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
		CommandCenter.getInstance(DEVICE_ADDRESS);
		bReceiver = new BehaviorReceiver();
		rReceiver = new RobotReceiver();
		dSpeechReceiver = new DistributingSpeechReceiver();
		createSpeechRecognizerService();
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
		if (SpeechRecognizerService.getInstance() == null) {
			createSpeechRecognizerService();
		}

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
		unregisterReceiver(bReceiver);
		unregisterReceiver(rReceiver);
		unregisterReceiver(dSpeechReceiver);
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
		stopSpeechRecognizerService();
		super.onDestroy();
	}

	private void createSpeechRecognizerService() {
		startService(new Intent(RobotService.this,
				SpeechRecognizerService.class));
	}

	private void stopSpeechRecognizerService() {
		stopService(new Intent(RobotService.this, SpeechRecognizerService.class));
	}

	public DistributingSpeechReceiver getDistributingSpeechReceiver() {
		return dSpeechReceiver;
	}

}
