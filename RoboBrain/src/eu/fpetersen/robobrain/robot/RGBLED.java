package eu.fpetersen.robobrain.robot;


public class RGBLED extends RobotPart {

	private int green = 0;
	private int red = 0;
	private int blue = 0;

	public RGBLED(Robot robot) {
		super(robot);
	}

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
