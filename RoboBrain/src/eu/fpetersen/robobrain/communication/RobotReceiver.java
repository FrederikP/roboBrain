package eu.fpetersen.robobrain.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Receives intents filled with data sent from Arduino Devices. Source can be
 * identified by MAC address.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, final Intent intent) {

		String data = null;

		// the type of data which is added to the intent
		final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE,
				-1);

		if (dataType == AmarinoIntent.STRING_EXTRA) {
			data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
			data = data.replace("\r", "");
			data = data.replace("\n", "");
			String address = intent
					.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			CommandCenter cc = CommandCenter.getCCForAddress(address);
			Robot robot = cc.getRobot();
			if (robot != null) {
				String frontPrefix = "FRONTPROX:";
				String backPrefix = "BACKPROX:";
				String consolePrefix = "CONSOLE:";
				String stoppedAfterDelay = "STOPPEDAFTERDELAY";
				if (data.startsWith(frontPrefix)) {
					String substring = data.substring(frontPrefix.length());
					int proxValue = Integer.parseInt(substring);
					if (proxValue == 0) {
						Log.v("RobotReceiver", "Front Prox returned 0");
					} else {
						robot.getFrontSensor().setValue(proxValue);
					}
				} else if (data.startsWith(backPrefix)) {
					String substring = data.substring(backPrefix.length());
					int proxValue = Integer.parseInt(substring);
					robot.getBackSensor().setValue(proxValue);
				} else if (data.contains(stoppedAfterDelay)) {
					robot.getMainMotor().delayActionDone();
				} else if (data.startsWith(consolePrefix)) {
					String substring = data.substring(consolePrefix.length());
					RoboLog.log(RobotService.getInstance(), substring);
				}

				/*
				 * //Uncomment for debugging purposes: (Slow phone can be
				 * overwhelmed by a high rate of data
				 * 
				 * Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
				 * cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, data);
				 * context.sendBroadcast(cIntent);
				 */
			}
		}

	}
}
