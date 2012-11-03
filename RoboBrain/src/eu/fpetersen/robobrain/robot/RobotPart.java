package eu.fpetersen.robobrain.robot;

/**
 * @author Frederik Petersen
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

	/**
	 * This needs to be called after instantiated the RobotPart and it exists
	 * because the default constructor is needed to dynamically decide which
	 * subclass is to be created
	 * 
	 * @param robot
	 *            Robot the Part belongs to.
	 */
	protected void initialize(Robot robot) {
		this.robot = robot;
	}

	protected RobotPart() {
	}

	public Robot getRobot() {
		return robot;
	}

}
