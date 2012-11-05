package eu.fpetersen.robobrain.robot.test;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.robot.RobotPart;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link Robot}
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotTest extends AndroidTestCase {

	/**
	 * Test adding the standard Motor, setting its speed and stopping the robot.
	 */
	public void testPartAddingWithMainMotor() {
		Robot robot = RobotFactory.getInstance(
				new MockRobotService(getContext()))
				.createSimpleRobot("TestBot");

		assertNotNull(robot);

		RobotPart motorPart = RobotPartFactory.getInstance().createRobotPart(
				"Motor", robot);

		assertNotNull(motorPart);

		robot.addPart("main_motor", motorPart);

		Motor motor = robot.getMainMotor();

		assertNotNull(motor);

		motor.advance(200);

		assertEquals(MotorState.FORWARD, motor.getState());

		robot.stop();

		assertEquals(MotorState.STOPPED, motor.getState());

	}

}
