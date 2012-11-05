package eu.fpetersen.robobrain.robot.test;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link Motor}
 * 
 * @author Frederik Petersen
 * 
 */
public class MotorTest extends AndroidTestCase {

	/**
	 * Test if Motor is in the right state, after called methods
	 */
	public void testStateManagement() {
		int speed = 200;
		int angle = 90;
		Robot mockRobot = RobotFactory.getInstance(
				new MockRobotService(getContext()))
				.createSimpleRobot("TestBot");
		Motor motor = (Motor) RobotPartFactory.getInstance().createRobotPart(
				"Motor", mockRobot);

		motor.advance(speed);
		assertEquals(MotorState.FORWARD, motor.getState());

		motor.backOff(speed);
		assertEquals(MotorState.BACKWARD, motor.getState());

		motor.turnLeft(angle);
		assertEquals(MotorState.TURNING_LEFT, motor.getState());

		motor.delayActionDone();
		assertEquals(MotorState.STOPPED, motor.getState());

		motor.turnRight(angle);
		assertEquals(MotorState.TURNING_RIGHT, motor.getState());

		motor.delayActionDone();
		assertEquals(MotorState.STOPPED, motor.getState());

		motor.advance(speed);
		assertEquals(MotorState.FORWARD, motor.getState());

		motor.stop(0);
		assertEquals(MotorState.STOPPED, motor.getState());

		motor.advance(speed);
		assertEquals(MotorState.FORWARD, motor.getState());

		motor.stop(500);
		assertEquals(MotorState.STOPPINGWITHDELAY, motor.getState());

		motor.delayActionDone();
		assertEquals(MotorState.STOPPED, motor.getState());

	}

}
