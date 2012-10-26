package eu.fpetersen.robobrain.robot;

public abstract class RobotPart {
	private Robot robot;

	public RobotPart(Robot robot) {
		super();
		this.robot = robot;
	}

	public Robot getRobot() {
		return robot;
	}

}
