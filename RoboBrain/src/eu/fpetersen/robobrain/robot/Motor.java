package eu.fpetersen.robobrain.robot;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RobotService;

public class Motor {

	private String address;

	private MotorState state = MotorState.STOPPED;

	public Motor(String address) {
		this.address = address;
	}

	public void backOff(int speed) {
		Amarino.sendDataToArduino(RobotService.getInstance(), address, 'B',
				speed);
		setState(MotorState.BACKWARD);
	}

	public void advance(int speed) {
		Amarino.sendDataToArduino(RobotService.getInstance(), address, 'A',
				speed);
		setState(MotorState.FORWARD);
	}

	public void stop(int delay) {
		Amarino.sendDataToArduino(RobotService.getInstance(), address, 'S',
				delay);
		setState(MotorState.STOPPED);
	}

	public MotorState getState() {
		return state;
	}

	private void setState(MotorState state) {
		this.state = state;
	}

	public enum MotorState {
		STOPPED, FORWARD, BACKWARD
	}

}
