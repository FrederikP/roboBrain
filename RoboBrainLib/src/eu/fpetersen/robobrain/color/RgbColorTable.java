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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Maps {@link RgbColor}s to names.
 * 
 * @author Frederik Petersen
 * 
 */
public class RgbColorTable {
	private Map<String, RgbColor> mColors = new HashMap<String, RgbColor>();

	protected RgbColorTable() {

	}

	/**
	 * Add a RGB color to the table, needs a name as an identifier. If name
	 * already exists, color is overwritten.
	 * 
	 * @param name
	 *            Color name identifying the color
	 * @param color
	 *            RGB color to be added
	 */
	public void addColor(String name, RgbColor color) {
		mColors.put(name, color);
	}

	/**
	 * Get complete List of names of all colors in the color table. Sorted by
	 * the number of words. Most words first.
	 * 
	 * @return List of names of all colors in the color table
	 */
	public List<String> getNames() {
		Set<String> keys = mColors.keySet();
		List<String> keysSortedByWordCount = new ArrayList<String>();
		for (String oneName : keys) {
			keysSortedByWordCount.add(oneName);
		}

		Collections.sort(keysSortedByWordCount, new Comparator<String>() {

			public int compare(String s1, String s2) {
				int s1words = s1.split(" ").length;
				int s2words = s2.split(" ").length;
				if (s1words > s2words) {
					return -1;
				} else if (s1words == s2words) {
					return 0;
				} else {
					return 1;
				}

			}
		});
		return keysSortedByWordCount;
	}

	/**
	 * Returns RGB color for identifying name. If name is not in table, null is
	 * returned.
	 * 
	 * @param name
	 *            Identifying name of the color
	 * @return RGB color representation
	 */
	public RgbColor getColorForName(String name) {
		if (mColors.containsKey(name)) {
			return mColors.get(name);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return a random color from the table
	 */
	public RgbColor getRandomColor() {
		int size = mColors.size();
		Collection<RgbColor> rgbColors = mColors.values();
		Random random = new Random();
		int rand = random.nextInt(size);
		return rgbColors.toArray(new RgbColor[rgbColors.size()])[rand];
	}

}
