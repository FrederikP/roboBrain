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

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import eu.fpetersen.robobrain.util.exceptions.AppRequirementNotMetException;

/**
 * Methods for checking the availability of required software
 * 
 * @author Frederik Petersen
 * 
 */
public class AppRequirementsChecker {

	private Context mContext;

	public AppRequirementsChecker(Context context) {
		mContext = context;
	}

	/**
	 * Checks for all required packages that RoboBrain needs
	 * 
	 * @param context
	 * @param packageManager
	 * @return True if all packages installed with required version, false if
	 *         not
	 */
	public boolean checkForRequirements(PackageManager packageManager) {
		try {
			checkForAmarino(packageManager);
		} catch (AppRequirementNotMetException e) {
			e.showAlert(mContext);
			return false;
		}
		return true;
	}

	/**
	 * Amarino is necessary for the Bluetooth connection Android <-> Arduino
	 * 
	 * @param packageManager
	 * @throws AppRequirementNotMetException
	 *             when requirement is not met
	 */
	private void checkForAmarino(PackageManager packageManager)
			throws AppRequirementNotMetException {
		List<PackageInfo> listOfAllApps = packageManager.getInstalledPackages(0);
		Integer versionCode = null;
		for (PackageInfo info : listOfAllApps) {
			if (info.packageName.matches("at.abraxas.amarino")) {
				versionCode = info.versionCode;
			}
		}

		if (versionCode == null) {
			throw new AppRequirementNotMetException(
					"Amarino toolkit not installed. Please get it from www.amarino-toolkit.net and install.",
					false);
		} else if (versionCode < 13) {
			throw new AppRequirementNotMetException(
					"RoboBrain was only tested with newer Amarino Version (0.55) "
							+ "If you experience problems get it from www.amarino-toolkit.net and install.",
					true);
		}
	}

}
