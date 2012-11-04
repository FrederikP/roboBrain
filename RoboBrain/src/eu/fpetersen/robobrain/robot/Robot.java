package eu.fpetersen.robobrain.robot;

import java.util.HashMap;
import java.util.Map;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Representation of the controlled robot. It's hardware is controlled by an
 * Arduino compatible board. Other hardware is directly controlled by Android
 * device this App runs on. The robot and it's parts are configured in xml files
 * in the /robobrain/robots folder on the sd card. Some standard named parts
 * have direct access methods other parts have to accessed by calling
 * {@link Robot#getPart(String)} and casting.
 * 
 * @author Frederik Petersen
 * 
 */
public class Robot {
	private String name;
	private Map<String, RobotPart> parts;
	private String address;

	/**
	 * Create robot with given address and name. Empty Part map is initialized.
	 * 
	 * @param address
	 *            MAC Address of robots Arduino device.
	 * @param name
	 *            Name of the robot.
	 */
	public Robot(String address, String name) {
		this.name = name;
		this.address = address;
		parts = new HashMap<String, RobotPart>();
	}

	/**
	 * Stop the robots movement by stopping the main Motor.
	 */
	public void stop() {
		getMainMotor().stop(0);
	}

	/**
	 * Send String to arduino Device
	 * 
	 * @param flag
	 *            Flag that the method on the Arduino device is registered with
	 * @param data
	 *            String to be sent to the desired method on the Arduino device.
	 */
	public void sendToArduino(char flag, String data) {

		RoboLog.log(RobotService.getInstance(), "Send data to Robot " + name
				+ ": Flag: " + flag + " Data: " + data);
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	/**
	 * Send String Array to arduino Device
	 * 
	 * @param flag
	 *            Flag that the method on the Arduino device is registered with
	 * @param data
	 *            String array to be sent to the desired method on the Arduino
	 *            device.
	 */
	public void sendToArduino(char flag, String[] data) {

		String dataString = new String("");
		for (int i = 0; i < data.length; i++) {
			dataString = dataString + data[i];
			if (i < data.length - 1) {
				dataString = dataString + ",";
			}
		}
		RoboLog.log(RobotService.getInstance(), "Send data to Robot " + name
				+ ": Flag: " + flag + " Data: " + dataString);
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	/**
	 * Send Integer to arduino Device
	 * 
	 * @param flag
	 *            Flag that the method on the Arduino device is registered with
	 * @param data
	 *            Integer to be sent to the desired method on the Arduino
	 *            device.
	 */
	public void sendToArduino(char flag, int data) {
		RoboLog.log(RobotService.getInstance(), "Send data to Robot " + name
				+ ": Flag: " + flag + " Data: " + Integer.toString(data));
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	/**
	 * Send Integer array to arduino Device
	 * 
	 * @param flag
	 *            Flag that the method on the Arduino device is registered with
	 * @param data
	 *            Integer array to be sent to the desired method on the Arduino
	 *            device.
	 */
	public void sendToArduino(char flag, int[] data) {
		String dataString = new String("");
		for (int i = 0; i < data.length; i++) {
			dataString = dataString + Integer.toString(data[i]);
			if (i < data.length - 1) {
				dataString = dataString + ",";
			}
		}
		RoboLog.log(RobotService.getInstance(), "Send data to Robot " + name
				+ ": Flag: " + flag + " Data: " + dataString);
		Amarino.sendDataToArduino(RobotService.getInstance(), address, flag,
				data);
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	/**
	 * Add RobotPart to this robot
	 * 
	 * @param id
	 *            Part id, so it can be accessed later by calling
	 *            {@link Robot#getPart(String)}. Id is configured in xml file.
	 * @param part
	 *            The part that is to be added.
	 */
	public void addPart(String id, RobotPart part) {
		parts.put(id, part);
	}

	/**
	 * 
	 * @param id
	 *            Is set in xml
	 * @return RobotPart identified by ID
	 */
	public RobotPart getPart(String id) {
		return parts.get(id);
	}

	/**
	 * 
	 * @return Standard motor
	 */
	public Motor getMainMotor() {
		return (Motor) getPart("main_motor");
	}

	/**
	 * 
	 * @return Standard front proximity sensor
	 */
	public ProximitySensor getFrontSensor() {
		return (ProximitySensor) getPart("front_proxsensor");
	}

	/**
	 * 
	 * @return Standard back proximity sensor
	 */
	public ProximitySensor getBackSensor() {
		return (ProximitySensor) getPart("back_proxsensor");
	}

	/**
	 * 
	 * @return Standard head Servo
	 */
	public Servo getHeadServo() {
		return (Servo) getPart("head_servo");
	}

	/**
	 * 
	 * @return Standard head color LED
	 */
	public RGBLED getHeadColorLED() {
		return (RGBLED) getPart("headcolor_rgbled");
	}

}
