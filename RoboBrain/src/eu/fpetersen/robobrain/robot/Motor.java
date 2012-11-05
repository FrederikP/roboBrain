package eu.fpetersen.robobrain.robot;

/**
 * Represents Arduino controlled motor unit of the robot.
 * 
 * @author Frederik Petersen
 * 
 */
public class Motor extends RobotPart {

	/**
	 * Represents in which state the Motor is.
	 */
	private MotorState state = MotorState.STOPPED;

	/**
	 * Back off at given speed
	 * 
	 * @param speed
	 *            The speed the robot is supposed to back off with.
	 */
	public void backOff(int speed) {
		getRobot().sendToArduino('B', speed);
		setState(MotorState.BACKWARD);

	}

	/**
	 * Advance at given speed
	 * 
	 * @param speed
	 *            The speed the robot is supposed to advance with.
	 */
	public void advance(int speed) {
		getRobot().sendToArduino('A', speed);
		setState(MotorState.FORWARD);
	}

	/**
	 * Stops the robot with the given delay
	 * 
	 * @param delay
	 *            Delay the robot is supposed to stop with.
	 */
	public void stop(int delay) {
		getRobot().sendToArduino('S', delay);
		if (delay <= 0 && state != MotorState.STOPPED) {
			setState(MotorState.STOPPED);
		} else {
			setState(MotorState.STOPPINGWITHDELAY);
		}
	}

	/**
	 * Turn right with given angle in deg
	 * 
	 * @param angle
	 *            Angle to turn with in degrees
	 */
	public void turnRight(int angle) {
		getRobot().sendToArduino('R', angle);
		setState(MotorState.TURNING_RIGHT);
	}

	/**
	 * Turn leftwith given angle in deg
	 * 
	 * @param angle
	 *            Angle to turn with in degrees
	 */
	public void turnLeft(int angle) {
		getRobot().sendToArduino('L', angle);
		setState(MotorState.TURNING_LEFT);
	}

	public MotorState getState() {
		return state;
	}

	private void setState(MotorState state) {
		this.state = state;
	}

	/**
	 * When delay action is done, the Motor state needs to be set to
	 * {@link MotorState#STOPPED}. This happens after turning or stopping with
	 * delay
	 */
	public void delayActionDone() {
		setState(MotorState.STOPPED);
	}

	/**
	 * The different states the motor can be in
	 * 
	 * @author Frederik Petersen
	 * 
	 */
	public enum MotorState {
		STOPPED, FORWARD, BACKWARD, TURNING_RIGHT, TURNING_LEFT, STOPPINGWITHDELAY
	}

}
