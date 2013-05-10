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
package eu.fpetersen.robobrain.behavior.followobject;

/**
 * @author Frederik Petersen
 * 
 */
public class FollowObjectIntent {

	// -----ACTIONS-----//
	public static final String ACTION_LEFT = "eu.fpetersen.robobrain.behavior.followobject.intent.action.LEFT";

	public static final String ACTION_RIGHT = "eu.fpetersen.robobrain.behavior.followobject.intent.action.RIGHT";

	public static final String ACTION_FORWARD = "eu.fpetersen.robobrain.behavior.followobject.intent.actions.FORWARD";

	public static final String ACTION_BACKWARD = "eu.fpetersen.robobrain.behavior.followobject.intent.actions.BACKWARD";

	public static final String ACTION_STOP = "eu.fpetersen.robobrain.behavior.followobject.intent.actions.STOP";

	public static final String ACTION_QUIT = "eu.fpetersen.robobrain.behavior.followobject.intent.actions.QUIT";
}
