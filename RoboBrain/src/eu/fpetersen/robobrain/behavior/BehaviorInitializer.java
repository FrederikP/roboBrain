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
package eu.fpetersen.robobrain.behavior;

import eu.fpetersen.robobrain.robot.Robot;

/**
 * Holds the values necessary for initializing the behavior, after creating it.
 * 
 * @author Frederik Petersen
 * 
 * 
 */
public class BehaviorInitializer {

	private String mName;

	private String mSpeechName;

	public BehaviorInitializer(String name, String speechName) {
		super();
		this.mName = name;
		this.mSpeechName = speechName.toLowerCase();
	}

	public void initialize(Behavior b, Robot r) {
		b.initialize(r, mName, mSpeechName);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

}
