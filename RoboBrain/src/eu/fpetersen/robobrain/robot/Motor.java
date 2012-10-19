package eu.fpetersen.robobrain.robot;

import android.content.Intent;
import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
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
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, "ToArduino:B:" + speed);
		RobotService.getInstance().sendBroadcast(cIntent);
	}

	public void advance(int speed) {
		Amarino.sendDataToArduino(RobotService.getInstance(), address, 'A',
				speed);
		setState(MotorState.FORWARD);
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, "ToArduino:A:" + speed);
		RobotService.getInstance().sendBroadcast(cIntent);
	}

	public void stop(int delay) {
		Amarino.sendDataToArduino(RobotService.getInstance(), address, 'S',
				delay);
		setState(MotorState.STOPPED);
		Intent cIntent = new Intent(RoboBrainIntent.ACTION_OUTPUT);
		cIntent.putExtra(RoboBrainIntent.EXTRA_OUTPUT, "ToArduino:S:" + delay);
		RobotService.getInstance().sendBroadcast(cIntent);
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
