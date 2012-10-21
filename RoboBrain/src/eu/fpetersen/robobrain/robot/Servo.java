package eu.fpetersen.robobrain.robot;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RobotService;

public class Servo {

	private String address;

	public Servo(String address) {
		this.address = address;
	}

	public void setToAngle(int angle) {
		Amarino.sendDataToArduino(RobotService.getInstance(), address, 'C',
				angle);
	}

}
