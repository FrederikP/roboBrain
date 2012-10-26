package eu.fpetersen.robobrain.robot;


public class Motor extends RobotPart {

	private MotorState state = MotorState.STOPPED;

	public Motor(Robot robot) {
		super(robot);
	}

	public void backOff(int speed) {
		getRobot().sendToArduino('B', speed);
		setState(MotorState.BACKWARD);
	}

	public void advance(int speed) {
		getRobot().sendToArduino('A', speed);
		setState(MotorState.FORWARD);
	}

	public void stop(int delay) {
		getRobot().sendToArduino('S', delay);
		if (delay <= 0) {
			setState(MotorState.STOPPED);
		} else {
			setState(MotorState.STOPPINGWITHDELAY);
		}
	}

	public void turnRight(int angle) {
		getRobot().sendToArduino('R', angle);
		setState(MotorState.TURNING_RIGHT);
	}

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

	public void delayActionDone() {
		setState(MotorState.STOPPED);
	}

	public enum MotorState {
		STOPPED, FORWARD, BACKWARD, TURNING_RIGHT, TURNING_LEFT, STOPPINGWITHDELAY
	}

}
