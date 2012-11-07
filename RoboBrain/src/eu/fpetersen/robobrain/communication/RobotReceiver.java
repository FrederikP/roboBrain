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

	private RobotService service;

	public RobotReceiver(RobotService service) {
		this.service = service;
	}

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
			CommandCenter cc = service.getCCForAddress(address);
			if (cc != null) {
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
						String substring = data.substring(consolePrefix
								.length());
						RoboLog.log(service, substring);
					}

					/*
					 * //Uncomment for debugging purposes: (Slow phone can be
					 * overwhelmed by a high rate of data
					 * 
					 * Intent cIntent = new
					 * Intent(RoboBrainIntent.ACTION_OUTPUT);
					 * cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, data);
					 * context.sendBroadcast(cIntent);
					 */
				}
			}
		}

	}
}
