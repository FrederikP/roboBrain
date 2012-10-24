package eu.fpetersen.robobrain.color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RGBColorTable {
	private Map<String, RGBColor> colors = new HashMap<String, RGBColor>();

	protected RGBColorTable() {

	}

	public void addColor(String name, RGBColor color) {
		colors.put(name, color);
	}

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

	public RGBColor getColorForName(String name) {
		return colors.get(name);
	}

}
