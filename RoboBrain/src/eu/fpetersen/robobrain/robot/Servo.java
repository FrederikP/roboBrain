package eu.fpetersen.robobrain.robot;

public class Servo extends RobotPart {

	public void setToAngle(int angle) {
		getRobot().sendToArduino('C', angle);
	}

}
