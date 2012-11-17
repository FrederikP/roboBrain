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
package eu.fpetersen.robobrain.color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.StringTokenizer;

import android.content.Context;
import eu.fpetersen.robobrain.R;

/**
 * Singleton factory for creating {@link RgbColorTable}s for example from a
 * whitespace separated list of color names and RGB values
 * 
 * @author Frederik Petersen
 * 
 */
public class RgbColorTableFactory {

	private static RgbColorTableFactory sInstance;

	private RgbColorTableFactory() {

	}

	public static RgbColorTableFactory getInstance() {
		if (sInstance == null) {
			sInstance = new RgbColorTableFactory();
		}
		return sInstance;
	}

	/**
	 * Create a standard RGBColorTable from the textfile in the apps resources.
	 * Colors with names that include numbers are not included.
	 * 
	 * @return RGBColorTable filled with colors that don't include numbers in
	 *         the names.
	 */
	public RgbColorTable getStandardColorTableFromTextFile(Context context) {
		RgbColorTable table = new RgbColorTable();

		// The InputStream opens the resourceId and sends it to the buffer
		InputStream is = context.getResources().openRawResource(R.raw.rgb);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String readLine = null;

		try {
			// While the BufferedReader readLine is not null
			while ((readLine = br.readLine()) != null) {
				addColorFromTextLine(table, readLine, false);
			}

			// Close the InputStream and BufferedReader
			is.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return table;
	}

	/**
	 * Adds one Color from a line of the text file. But only if no numbers are
	 * included in the color name.
	 * 
	 * @param table
	 *            The color table the color is to be added to
	 * @param readLine
	 *            The line which includes color information
	 * @param includeNumberNames
	 *            Set true if you want to include colors which names include
	 *            numbers
	 */
	private void addColorFromTextLine(RgbColorTable table, String readLine,
			boolean includeNumberNames) {
		readLine = readLine.trim();
		StringTokenizer tokenizer = new StringTokenizer(readLine);
		int r = Integer.parseInt(tokenizer.nextToken());
		int g = Integer.parseInt(tokenizer.nextToken());
		int b = Integer.parseInt(tokenizer.nextToken());
		StringBuffer nameBuffer = new StringBuffer(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			nameBuffer.append(" " + tokenizer.nextToken());
		}
		String name = nameBuffer.toString();
		if (includeNumberNames
				|| (!name.contains("0") && !name.contains("1") && !name.contains("2")
						&& !name.contains("3") && !name.contains("4") && !name.contains("5")
						&& !name.contains("6") && !name.contains("7") && !name.contains("8") && !name
							.contains("9"))) {
			addColor(table, r, g, b, name.toLowerCase(Locale.US));
		}

	}

	/**
	 * Add one color to the Color Table
	 * 
	 * @param table
	 *            The table the number is to be added to
	 * @param r
	 *            The value for RED
	 * @param g
	 *            The value for GREEN
	 * @param b
	 *            The value for BLUE
	 * @param name
	 *            The name of the color
	 */
	private void addColor(RgbColorTable table, int r, int g, int b, String name) {
		table.addColor(name, new RgbColor(name, r, g, b));
	}

}
