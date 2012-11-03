package eu.fpetersen.robobrain.color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps RGBColor to a color Name.
 * 
 * @author Frederik Petersen
 * 
 */
public class RGBColorTable {
	private Map<String, RGBColor> colors = new HashMap<String, RGBColor>();

	protected RGBColorTable() {

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
	public void addColor(String name, RGBColor color) {
		colors.put(name, color);
	}

	/**
	 * Get complete List of names of all colors in the color table. Sorted by
	 * the number of words. Most words first.
	 * 
	 * @return List of names of all colors in the color table
	 */
	public List<String> getNames() {
		Set<String> keys = colors.keySet();
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
	public RGBColor getColorForName(String name) {
		if (colors.containsKey(name)) {
			return colors.get(name);
		} else {
			return null;
		}
	}

}
