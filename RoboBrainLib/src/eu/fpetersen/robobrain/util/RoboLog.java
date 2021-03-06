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
package eu.fpetersen.robobrain.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

/**
 * Allows logging to android logging system and robobrains console at the same
 * time
 * 
 * @author Frederik Petersen
 * 
 */
public class RoboLog {

	private String mTag = "RoboLog";
	private Context mContext = null;

	public RoboLog(String tag, Context context) {
		mTag = tag;
		mContext = context;
	}

	/**
	 * Log message to robobrains console and androids loggin system
	 * 
	 * @param message
	 *            Message to be logged
	 * @param toUIConsole
	 *            True if message should be send to Console activity. Set to
	 *            false if it would be called way to much. This hugely
	 *            influences performance
	 */
	public void log(String message, boolean toUIConsole) {
		Log.v(mTag, message);
		if (toUIConsole) {
			Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
			cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, message);
			mContext.sendBroadcast(cIntent);
		}
	}

	/**
	 * Print error to log and console and try showing Alert in starter activity
	 * 
	 * @param context
	 * @param message
	 *            Error message to display to user and in log
	 */
	public void alertError(String message) {
		String errorTag = "[ERROR]";
		log(message + errorTag, true);
		broadcastAlertIntent(message, errorTag);

	}

	/**
	 * Print warning to log and console and try showing Alert in starter
	 * activity
	 * 
	 * @param context
	 * @param message
	 *            Warning message to display to user and in log
	 */
	public void alertWarning(String message) {
		String errorTag = "[WARNING]";
		log(message + errorTag, true);
		broadcastAlertIntent(message, errorTag);
	}

	/**
	 * Broadcast alert intent to be received by UI activity
	 * 
	 * @param message
	 * @param errorTag
	 */
	private void broadcastAlertIntent(String message, String errorTag) {
		Intent intent = new Intent(RoboBrainIntent.ACTION_SHOWALERT);
		intent.putExtra(RoboBrainIntent.EXTRA_ALERTMESSAGE, message);
		intent.putExtra(RoboBrainIntent.EXTRA_ERRORTAG, errorTag);
		mContext.sendBroadcast(intent);

	}

}
