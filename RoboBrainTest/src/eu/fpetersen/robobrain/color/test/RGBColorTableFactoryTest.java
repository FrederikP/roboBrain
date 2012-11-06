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
package eu.fpetersen.robobrain.color.test;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.color.RGBColor;
import eu.fpetersen.robobrain.color.RGBColorTable;
import eu.fpetersen.robobrain.color.RGBColorTableFactory;

/**
 * Tests the {@link RGBColorTableFactory} class
 * 
 * @author Frederik Petersen
 * 
 */
public class RGBColorTableFactoryTest extends AndroidTestCase {

	/**
	 * Test if RGBColorTable is successfully generated from the file in the
	 * resources of the app
	 */
	public void testColorsFromFileCreation() {
		RGBColorTableFactory rgbFac = RGBColorTableFactory.getInstance();
		assertNotNull(rgbFac);
		RGBColorTable table = rgbFac
				.getStandardColorTableFromTextFile(getContext());
		assertNotNull(table);
		assertTrue(table.getNames().size() > 0);
		String newColorName = "Some Color";
		table.addColor(newColorName, new RGBColor(newColorName, 22, 22, 22));
		RGBColor newColor = table.getColorForName(newColorName);
		assertNotNull(newColor);
	}

}
