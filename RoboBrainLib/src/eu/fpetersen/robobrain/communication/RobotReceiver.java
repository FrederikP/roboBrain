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

import java.util.StringTokenizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.parts.RobotPart;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Receives intents filled with data sent from Arduino Devices. Source can be
 * identified by MAC address.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotReceiver extends BroadcastReceiver {

	private RobotService mService;

	private RoboLog mLog;

	public RobotReceiver(RobotService service) {
		this.mService = service;
		mLog = new RoboLog("RobotReceiver", service);
	}

	@Override
	public void onReceive(Context context, final Intent intent) {

		String data = null;

		// the type of data which is added to the intent
		final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);

		if (dataType == AmarinoIntent.STRING_EXTRA) {
			data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
			data = data.replace("\r", "");
			data = data.replace("\n", "");
			String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			CommandCenter cc = mService.getCCForAddress(address);
			if (cc == null) {
				return;
			}
			Robot robot = cc.getRobot();
			StringTokenizer tokenizer = new StringTokenizer(data, ":");
			if (tokenizer.countTokens() != 2) {
				return;
			}
			String prefix = tokenizer.nextToken();
			data = tokenizer.nextToken();
			if (prefix.matches("console")) {
				mLog.log(data, true);
			} else {
				RobotPart part = robot.getPart(prefix);
				if (part == null) {
					return;
				}
				part.onReceive(data);
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
