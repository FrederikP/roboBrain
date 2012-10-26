package eu.fpetersen.robobrain.robot;


public class Servo extends RobotPart {

	public Servo(Robot robot) {
		super(robot);
	}

	public void setToAngle(int angle) {
		getRobot().sendToArduino('C', angle);
	}

}
