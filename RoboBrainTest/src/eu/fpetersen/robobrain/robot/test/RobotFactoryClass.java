package eu.fpetersen.robobrain.robot.test;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

public class RobotFactoryClass extends AndroidTestCase {

	public void testSimpleRobotCreation() {
		String robotName = "TestBot";
		Robot robot = RobotFactory.getInstance(
				new MockRobotService(getContext()))
				.createSimpleRobot(robotName);
		assertNotNull(robot);
		assertEquals(robot.getName(), robotName);
	}

}
