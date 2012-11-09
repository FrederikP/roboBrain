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

import eu.fpetersen.robobrain.color.RGBColor;

/**
 * Represents a RGBLED connected to the analog pins of an Arduino device. Where
 * the range of pin values goes from 0 to 1023
 * 
 * @author Frederik Petersen
 * 
 */
public class RGBLED extends RobotPart {

	private int green = 0;
	private int red = 0;
	private int blue = 0;

	/**
	 * Set the RGBLED color to the specified RGB color
	 * 
	 * @param red
	 *            Red color value 0-255
	 * @param green
	 *            Green color value 0-255
	 * @param blue
	 *            Blue color value 0-255
	 */
	public void set(int red, int green, int blue) {
		if (this.red == red && this.green == green && this.blue == blue) {
			// Already set to this value no need to contact Arduino/Hardware
		} else {
			this.red = red;
			this.green = green;
			this.blue = blue;
			int[] colors = { red * 4, green * 4, blue * 4 };
			getRobot().sendToArduino('D', colors);
		}
	}

	/**
	 * 
	 * 
	 * @param color
	 *            The color the LED should be set to
	 */
	public void set(RGBColor color) {
		set(color.getRed(), color.getGreen(), color.getBlue());
	}

}
