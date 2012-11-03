package eu.fpetersen.robobrain.robot;

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

}
