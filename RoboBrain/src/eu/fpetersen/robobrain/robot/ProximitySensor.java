package eu.fpetersen.robobrain.robot;

//TODO: Find a way to differ between digital and analog value ranges 
//(i.e. binary infrared sensor and a cm value ranged ultrasonix sensor)

/**
 * A simple proximity sensor representation for an Arduino sensor.
 * 
 * @author Frederik Petersen
 * 
 */
public class ProximitySensor extends Sensor {

	/**
	 * Represents the value as set by the sensor.
	 */
	private int value = 0;

	public void setValue(int proxValue) {
		value = proxValue;
	}

	public int getValue() {
		return value;
	}

}
