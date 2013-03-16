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
package eu.fpetersen.robobrain.robot.parts;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a servo connected to an Arduino device.
 * 
 * @author Frederik Petersen
 * 
 */
public class Servo extends RobotPart {

	private int mAngle = 0;

	/**
	 * Set servo to specified angle
	 * 
	 * @param angle
	 *            Angle in degrees
	 */
	public void setToAngle(int angle) {
		mAngle = angle;
		getRobot().sendToArduino(getFlag("toAngle"), angle);
	}

	@Override
	protected List<String> getRequiredFlagIds() {
		List<String> requiredFlagIds = new ArrayList<String>();
		requiredFlagIds.add("toAngle");
		return requiredFlagIds;
	}

	@Override
	public void onReceive(String data) {
		// Nothing to do.. for now.
	}

	/**
	 * @return
	 */
	public int getAngle() {
		return mAngle;
	}

}
