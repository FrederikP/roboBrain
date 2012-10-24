package eu.fpetersen.robobrain.behavior;

import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;

public class BackAndForthBehavior extends Behavior {

	public BackAndForthBehavior(Robot robot, String name) {
		super(robot, name);
	}

	@Override
	protected void behaviorLoop() {
		Robot robot = getRobot();
		if (robot.getMotor().getState().equals(MotorState.STOPPED)) {
			if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 1) {
				robot.getMotor().backOff(240);
				toConsole("STOPPED -> BACKWARD");
			} else if (robot.getFrontSensor().getValue() >= 20) {
				robot.getMotor().advance(240);
				toConsole("STOPPED -> FORWARD");
			}
		} else if (robot.getMotor().getState().equals(MotorState.FORWARD)) {
			if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 1) {
				robot.getMotor().backOff(240);
				toConsole("FORWARD -> BACKWARD");
				toConsole("Frontsensor=" + robot.getFrontSensor().getValue());
			} else if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMotor().stop(0);
				toConsole("FORWARD -> STOPPED");
			}
		} else if (robot.getMotor().getState().equals(MotorState.BACKWARD)) {
			if (robot.getFrontSensor().getValue() >= 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMotor().advance(240);
				toConsole("BACKWARD-> FORWARD");
			} else if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMotor().stop(0);
				toConsole("BACKWARD-> STOPPED");
			}
		}
	}

}
