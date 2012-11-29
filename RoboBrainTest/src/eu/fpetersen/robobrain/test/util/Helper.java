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
package eu.fpetersen.robobrain.test.util;

/**
 * Helper class for all Test classes. A central repository for methods used
 * throughout the testing system
 * 
 * @author Frederik Petersen
 * 
 */
public class Helper {

	/**
	 * Set the Thread to sleep for the given amount of milli seconds
	 * 
	 * @param millis
	 *            Time to sleep in milli seconds
	 */
	public static void sleepMillis(long millis) {
		try {
			int multiplier = 1;
			if (System.getenv("buildonjenkins") != null
					&& System.getenv("buildonjenkins").matches("true")) {
				multiplier = 5;
			}
			Thread.sleep(millis * multiplier);
		} catch (InterruptedException e) {
			// Usually works. Is only used for tests so try again. And look at
			// stacktrace.
			e.printStackTrace();
		}
	}

}
