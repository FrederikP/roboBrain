package eu.fpetersen.robobrain.robot;

public class Robot {
	private String name;
	private Motor motor;
	private ProximitySensor frontSensor;
	private RGBLED headLED;

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
		motor = new Motor(address);
		frontSensor = new ProximitySensor();
		backSensor = new ProximitySensor();
		frontSensorServo = new Servo(address);
		headLED = new RGBLED(address);
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

}
