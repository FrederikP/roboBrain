package eu.fpetersen.robobrain.behavior;

import android.util.Log;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;

public class ObstAvoidanceBehavior extends Behavior {

	private static final String TAG = "ObstAvoidanceBehavior";

	private final int speed = 240;

	private long backWardTime = 0;

	public ObstAvoidanceBehavior(Robot robot, String name) {
		super(robot, name);
	}

	@Override
	public void startBehavior() {
		backWardTime = 0;
		super.startBehavior();
	}

	@Override
	protected void behaviorLoop() {
		Robot robot = getRobot();
		if (!robot.getMainMotor().getState()
				.equals(MotorState.STOPPINGWITHDELAY)
				&& !robot.getMainMotor().getState()
						.equals(MotorState.TURNING_LEFT)
				&& !robot.getMainMotor().getState()
						.equals(MotorState.TURNING_RIGHT)) {
			if (robot.getMainMotor().getState().equals(MotorState.FORWARD)) {
				goingForward();
			} else if (robot.getMainMotor().getState()
					.equals(MotorState.BACKWARD)) {
				goingBackward();
			} else if (robot.getMainMotor().getState()
					.equals(MotorState.STOPPED)) {
				startGoingForward();
			}
		}
	}

	private void startGoingForward() {
		getRobot().getMainMotor().advance(speed);
	}

	private void goingBackward() {
		Robot robot = getRobot();
		if (backWardTime < System.currentTimeMillis()
				|| robot.getBackSensor().getValue() == 0) {
			backWardTime = 0;
			checkForBestRouteAndTurn();
		}
	}

	private void checkForBestRouteAndTurn() {
		Robot robot = getRobot();
		try {
			robot.getHeadServo().setToAngle(50);
			Thread.sleep(300);
			int rightMeasurement = robot.getFrontSensor().getValue();
			robot.getHeadServo().setToAngle(140);
			Thread.sleep(300);
			int leftMeasurement = robot.getFrontSensor().getValue();
			robot.getHeadServo().setToAngle(95);
			if (rightMeasurement >= leftMeasurement) {
				robot.getMainMotor().turnRight(45);
			} else {
				robot.getMainMotor().turnLeft(45);
			}
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted while waiting for servo to turn", e);
		}
	}

	private void goingForward() {
		Robot robot = getRobot();
		if (robot.getFrontSensor().getValue() < 30) {
			robot.getMainMotor().backOff(speed);
			backWardTime = System.currentTimeMillis() + 1000;
		}

	}

}
