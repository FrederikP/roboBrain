package eu.fpetersen.robobrain.robot;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RobotService;

public class RGBLED {

	private int green = 0;
	private int red = 0;
	private int blue = 0;

	private String address;

	public RGBLED(String address) {
		this.address = address;
	}

	public void set(int red, int green, int blue) {
		if (this.red == red && this.green == green && this.blue == blue) {
			// Already set to this value no need to contact Arduino/Hardware
		} else {
			this.red = red;
			this.green = green;
			this.blue = blue;
			int[] colors = { red, green, blue };
			Amarino.sendDataToArduino(RobotService.getInstance(), address, 'D',
					colors);
		}
	}

}
