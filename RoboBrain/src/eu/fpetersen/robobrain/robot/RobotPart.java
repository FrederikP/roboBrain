package eu.fpetersen.robobrain.robot;

/**
 * @author frederik
 * 
 * 
 *         When implementing a RobotPart class, please make sure to supply a
 *         default constructor (i.e. don't specify any constructor). For now
 *         there is no way to supply more parameters on initialization. This
 *         will change soon. Initialization values will then be entered through
 *         the .xml config file of the robot.
 * 
 */
public abstract class RobotPart {
	private Robot robot;

	protected void initialize(Robot robot) {
		this.robot = robot;
	}

	protected RobotPart() {
	}

	public Robot getRobot() {
		return robot;
	}

}
