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

/**
 * Handle sleeping
 * 
 * @author Frederik Petersen
 * 
 */
public class SleepHelper {

	private RoboLog mLog;

	public SleepHelper(Context context) {
		mLog = new RoboLog("SleepHelper", context);
	}

	/**
	 * Set the Thread to sleep for the given amount of milli seconds
	 * 
	 * @param context
	 * @param millis
	 *            Time to sleep in milli seconds
	 */
	public void sleepMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			mLog.alertError("Thread interrupted while sleeping");
			e.printStackTrace();
		}
	}

}
