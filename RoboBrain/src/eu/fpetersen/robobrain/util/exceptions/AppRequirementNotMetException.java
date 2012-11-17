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
package eu.fpetersen.robobrain.util.exceptions;

import android.content.Context;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * @author Frederik Petersen
 * 
 */
public class AppRequirementNotMetException extends Exception {

	private static final long serialVersionUID = 1L;
	private boolean mIsWarning = false;

	/**
	 * @param detailMessage
	 */
	public AppRequirementNotMetException(String detailMessage, boolean isWarning) {
		super(detailMessage);
		mIsWarning = isWarning;
	}

	/**
	 * @param starter
	 */
	public void showAlert(Context context) {
		if (mIsWarning) {
			RoboLog.alertWarning(context, getMessage());
		} else {
			RoboLog.alertError(context, getMessage());
		}
	}

}
