package eu.fpetersen.robobrain.color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RGBColorTable {
	private Map<String, RGBColor> colors = new HashMap<String, RGBColor>();

	protected RGBColorTable() {

	}

	public void addColor(String name, RGBColor color) {
		colors.put(name, color);
	}

	public Set<String> getNames() {
		return colors.keySet();
	}

	public RGBColor getColorForName(String name) {
		return colors.get(name);
	}

}
