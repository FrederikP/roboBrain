package eu.fpetersen.robobrain.robot;

/**
 * Representation of a servo connected to an Arduino device.
 * 
 * @author Frederik Petersen
 * 
 */
public class Servo extends RobotPart {

	/**
	 * Set servo to specified angle
	 * 
	 * @param angle
	 *            Angle in degrees
	 */
	public void setToAngle(int angle) {
		getRobot().sendToArduino('C', angle);
	}

}
