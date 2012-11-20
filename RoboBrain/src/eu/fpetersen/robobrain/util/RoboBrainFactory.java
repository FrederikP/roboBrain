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

import eu.fpetersen.robobrain.services.RobotService;

/**
 * Bundle service reference for all of the App's factories
 * 
 * @author Frederik Petersen
 * 
 */
public abstract class RoboBrainFactory {

	private RobotService mService;

	protected RoboLog mLog;

	public RoboBrainFactory(RobotService service) {
		this.mService = service;
		mLog = new RoboLog("Factory", service);
	}

	public RobotService getService() {
		return mService;
	}

	public void setService(RobotService service) {
		this.mService = service;
	}

}
