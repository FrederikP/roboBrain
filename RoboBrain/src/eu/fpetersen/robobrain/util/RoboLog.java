package eu.fpetersen.robobrain.util;

import android.content.Intent;
import android.util.Log;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.RobotService;

/**
 * Allows logging to android logging system and robobrains console at the same
 * time
 * 
 * @author Frederik Petersen
 * 
 */
public class RoboLog {

	private static final String TAG = "RoboLog";

	/**
	 * Log message to robobrains console and androids loggin system
	 * 
	 * @param message
	 *            Message to be logged
	 */
	public static void log(String message) {
		Log.v(TAG, message);
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, message);
		RobotService.getInstance().sendBroadcast(cIntent);
	}

}
