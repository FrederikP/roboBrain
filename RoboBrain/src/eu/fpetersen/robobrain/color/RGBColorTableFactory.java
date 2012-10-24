package eu.fpetersen.robobrain.color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.ui.R;

public class RGBColorTableFactory {

	private static RGBColorTableFactory instance;

	private RGBColorTableFactory() {

	}

	public static RGBColorTableFactory getInstance() {
		if (instance == null) {
			instance = new RGBColorTableFactory();
		}
		return instance;
	}

	public RGBColorTable getStandardColorTableFromTextFile() {
		RGBColorTable table = new RGBColorTable();

		// The InputStream opens the resourceId and sends it to the buffer
		InputStream is = RobotService.getInstance().getResources()
				.openRawResource(R.raw.rgb);
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

	private void addColorFromTextLine(RGBColorTable table, String readLine,
			boolean includeNumberNames) {
		readLine = readLine.trim();
		StringTokenizer tokenizer = new StringTokenizer(readLine);
		int r = Integer.parseInt(tokenizer.nextToken());
		int g = Integer.parseInt(tokenizer.nextToken());
		int b = Integer.parseInt(tokenizer.nextToken());
		String name = new String(tokenizer.nextToken());
		if (tokenizer.hasMoreTokens()) {
			name = name + " " + tokenizer.nextToken();
		}
		if (includeNumberNames
				|| (!name.contains("0") && !name.contains("1")
						&& !name.contains("2") && !name.contains("3")
						&& !name.contains("4") && !name.contains("5")
						&& !name.contains("6") && !name.contains("7")
						&& !name.contains("8") && !name.contains("9"))) {
			addColor(table, r, g, b, name.toLowerCase());
		}

	}

	private void addColor(RGBColorTable table, int r, int g, int b, String name) {
		table.addColor(name, new RGBColor(name, r, g, b));
	}

}
