package eu.fpetersen.robobrain.robot;

import java.util.HashMap;
import java.util.Map;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.util.RoboLog;

public class Robot {
	private String name;
	private Motor motor;
	private ProximitySensor frontSensor;
	private RGBLED headLED;
	private String address;
	private Map<String, RobotPart> parts;

	public void addPart(String id, RobotPart part) {
		parts.put(id, part);
	}

	public RobotPart getPart(String id) {
		return parts.get(id);
	}

	public Motor getMotor() {
		return motor;
	}

	public ProximitySensor getFrontSensor() {
		return frontSensor;
	}

	public ProximitySensor getBackSensor() {
		return backSensor;
	}

	public Servo getFrontSensorServo() {
		return frontSensorServo;
	}

	private ProximitySensor backSensor;
	private Servo frontSensorServo;

	public Robot(String address, String name) {
		this.name = name;
		this.address = address;
		parts = new HashMap<String, RobotPart>();
		motor = new Motor(Robot.this);
		frontSensor = new ProximitySensor(Robot.this);
		backSensor = new ProximitySensor(Robot.this);
		frontSensorServo = new Servo(Robot.this);
		headLED = new RGBLED(Robot.this);
	}

	public String getName() {
		return name;
	}

	public void stop() {
		motor.stop(0);
	}

	public RGBLED getHeadLED() {
		return headLED;
	}

	public void sendToArduino(char flag, String data) {

		RoboLog.log("Send data to Robot " + name + ": Flag: " + flag
				+ " Data: " + data);
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	public void sendToArduino(char flag, String[] data) {

		String dataString = new String("");
		for (int i = 0; i < data.length; i++) {
			dataString = dataString + data[i];
			if (i < data.length - 1) {
				dataString = dataString + ",";
			}
		}
		RoboLog.log("Send data to Robot " + name + ": Flag: " + flag
				+ " Data: " + dataString);
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	public void sendToArduino(char flag, int data) {
		RoboLog.log("Send data to Robot " + name + ": Flag: " + flag
				+ " Data: " + Integer.toString(data));
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	public void sendToArduino(char flag, int[] data) {
		String dataString = new String("");
		for (int i = 0; i < data.length; i++) {
			dataString = dataString + Integer.toString(data[i]);
			if (i < data.length - 1) {
				dataString = dataString + ",";
			}
		}
		RoboLog.log("Send data to Robot " + name + ": Flag: " + flag
				+ " Data: " + dataString);
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

}
