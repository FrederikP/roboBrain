package eu.fpetersen.robobrain.behavior;

import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;

/**
 * Makes the robot go back and forth. Very simple behavior. Checks front and
 * back proximity sensors for distance to next obstacle so it knows when to
 * switch direction. If there are obstacles in back and front, the robot stops
 * moving, until one of the obstacles is gone.
 * 
 * @author Frederik Petersen
 */
public class BackAndForthBehavior extends Behavior {

	@Override
	protected void behaviorLoop() {
		Robot robot = getRobot();
		if (robot.getMainMotor().getState().equals(MotorState.STOPPED)) {
			if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 1) {
				robot.getMainMotor().backOff(240);
				toConsole("STOPPED -> BACKWARD");
			} else if (robot.getFrontSensor().getValue() >= 20) {
				robot.getMainMotor().advance(240);
				toConsole("STOPPED -> FORWARD");
			}
		} else if (robot.getMainMotor().getState().equals(MotorState.FORWARD)) {
			if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 1) {
				robot.getMainMotor().backOff(240);
				toConsole("FORWARD -> BACKWARD");
				toConsole("Frontsensor=" + robot.getFrontSensor().getValue());
			} else if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMainMotor().stop(0);
				toConsole("FORWARD -> STOPPED");
			}
		} else if (robot.getMainMotor().getState().equals(MotorState.BACKWARD)) {
			if (robot.getFrontSensor().getValue() >= 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMainMotor().advance(240);
				toConsole("BACKWARD-> FORWARD");
			} else if (robot.getFrontSensor().getValue() < 20
					&& robot.getBackSensor().getValue() == 0) {
				robot.getMainMotor().stop(0);
				toConsole("BACKWARD-> STOPPED");
			}
		}
	}

	@Override
	protected void onStop() {
		getRobot().stop();
	}

}
