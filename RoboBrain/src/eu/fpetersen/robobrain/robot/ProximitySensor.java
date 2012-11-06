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
package eu.fpetersen.robobrain.robot;

//TODO: Find a way to differ between digital and analog value ranges 
//(i.e. binary infrared sensor and a cm value ranged ultrasonix sensor)

/**
 * A simple proximity sensor representation for an Arduino sensor.
 * 
 * @author Frederik Petersen
 * 
 */
public class ProximitySensor extends Sensor {

	/**
	 * Represents the value as set by the sensor.
	 */
	private int value = 0;

	public void setValue(int proxValue) {
		value = proxValue;
	}

	public int getValue() {
		return value;
	}

}
