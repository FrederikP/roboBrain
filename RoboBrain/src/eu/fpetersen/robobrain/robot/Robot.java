package eu.fpetersen.robobrain.robot;

import java.util.HashMap;
import java.util.Map;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.util.RoboLog;

public class Robot {
	private String name;
	private Map<String, RobotPart> parts;
	private String address;

	public Robot(String address, String name) {
		this.name = name;
		this.address = address;
		parts = new HashMap<String, RobotPart>();
	}

	public void stop() {
		getMainMotor().stop(0);
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

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public void addPart(String id, RobotPart part) {
		parts.put(id, part);
	}

	public RobotPart getPart(String id) {
		return parts.get(id);
	}

	public Motor getMainMotor() {
		return (Motor) getPart("main_motor");
	}

	public ProximitySensor getFrontSensor() {
		return (ProximitySensor) getPart("front_proxsensor");
	}

	public ProximitySensor getBackSensor() {
		return (ProximitySensor) getPart("back_proxsensor");
	}

	public Servo getHeadServo() {
		return (Servo) getPart("head_servo");
	}

	public RGBLED getHeadColorLED() {
		return (RGBLED) getPart("headcolor_rgbled");
	}

}
