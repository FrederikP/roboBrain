package eu.fpetersen.robobrain.communication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RobotService extends Service {

	// change this to your Bluetooth device address
	private static final String DEVICE_ADDRESS = "07:12:05:03:53:76";

	private static RobotService instance;

	private boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public static RobotService getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		instance = this;
		CommandCenter.getInstance(DEVICE_ADDRESS);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CommandCenter.connectAll();
		running = true;
		return START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		running = false;
		CommandCenter.disconnectAll();
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {
		running = false;
		CommandCenter.disconnectAll();
		super.onDestroy();
	}

}
