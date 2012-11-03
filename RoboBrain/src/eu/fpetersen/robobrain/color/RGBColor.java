package eu.fpetersen.robobrain.color;

/**
 * Classical representation of the RGB color model.
 * 
 * @author Frederik Petersen
 * 
 */
public class RGBColor {

	private int red;
	private int green;
	private int blue;

	public RGBColor(String name, int red, int green, int blue) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

}
