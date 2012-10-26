package eu.fpetersen.robobrain.robot;

public class ProximitySensor extends Sensor {

	public ProximitySensor(Robot robot) {
		super(robot);
	}

	private int value = 0;

	public void setValue(int proxValue) {
		value = proxValue;
	}

	public int getValue() {
		return value;
	}

}
