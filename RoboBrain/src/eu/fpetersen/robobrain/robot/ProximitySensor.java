package eu.fpetersen.robobrain.robot;

public class ProximitySensor extends Sensor {
	private int value = 0;

	public void setValue(int proxValue) {
		value = proxValue;
	}

	public int getValue() {
		return value;
	}

}
