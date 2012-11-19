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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Handles version information management
 * 
 * @author Frederik Petersen
 * 
 */
public class VersionHelper {

	/**
	 * Returns version number of RoboBrain app for the given PackageManager
	 * 
	 * @param packageManager
	 * @param context
	 * @return Version of Robobrain, Empty String if it could not be found
	 */
	public static String getVersion(PackageManager packageManager, Context context) {
		String version = "";
		try {
			PackageInfo pInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			if (pInfo != null) {
				version = pInfo.versionName;
			}
		} catch (NameNotFoundException e) {
			RoboLog.alertError(context, "Version could not be set");
		}

		return version;

	}

}
