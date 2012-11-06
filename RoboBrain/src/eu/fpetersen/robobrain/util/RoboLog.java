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

	private static final String TAG = "RoboLog";

	/**
	 * Log message to robobrains console and androids loggin system
	 * 
	 * @param message
	 *            Message to be logged
	 */
	public static void log(Context context, String message) {
		Log.v(TAG, message);
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, message);
		context.sendBroadcast(cIntent);
	}

}
