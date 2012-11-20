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

import java.io.InputStream;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.color.RgbColor;
import eu.fpetersen.robobrain.color.RgbColorTable;
import eu.fpetersen.robobrain.color.RgbColorTableFactory;

/**
 * Tests the {@link RgbColorTableFactory} class
 * 
 * @author Frederik Petersen
 * 
 */
public class RgbColorTableFactoryTest extends AndroidTestCase {

	/**
	 * Test if RGBColorTable is successfully generated from the file in the
	 * resources of the app
	 */
	public void testColorsFromFileCreation() {
		RgbColorTableFactory rgbFac = RgbColorTableFactory.getInstance();
		assertNotNull(rgbFac);
		RgbColorTable table = rgbFac.getStandardColorTableFromTextFile(getContext().getResources()
				.openRawResource(R.raw.rgb));
		assertNotNull(table);
		assertTrue(table.getNames().size() > 0);
		String newColorName = "Some Color";
		table.addColor(newColorName, new RgbColor(newColorName, 22, 22, 22));
		RgbColor newColor = table.getColorForName(newColorName);
		assertNotNull(newColor);

		RgbColor notExistantColor = table.getColorForName("BarschGruen");
		assertNull(notExistantColor);

		RgbColor randomColor = table.getRandomColor();
		assertNotNull(randomColor);
		int red = randomColor.getRed();
		int blue = randomColor.getBlue();
		int green = randomColor.getGreen();

		assertTrue(red >= 0 && red <= 255 && blue >= 0 && blue <= 255 && green >= 0 && green <= 255);
	}

	/**
	 * Test if empty ColorTable is created when entering empty file
	 */
	public void testColorsFromEmptyFileCreation() {
		RgbColorTableFactory rgbFac = RgbColorTableFactory.getInstance();
		assertNotNull(rgbFac);
		InputStream is = getContext().getResources().openRawResource(R.raw.empty);
		assertNotNull(is);
		RgbColorTable table = rgbFac.getStandardColorTableFromTextFile(is);
		assertNotNull(table);
		assertTrue(table.getNames().size() == 0);
		String newColorName = "Some Color";
		table.addColor(newColorName, new RgbColor(newColorName, 22, 22, 22));
		RgbColor newColor = table.getColorForName(newColorName);
		assertNotNull(newColor);

		RgbColor notExistantColor = table.getColorForName("BarschGruen");
		assertNull(notExistantColor);
	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
