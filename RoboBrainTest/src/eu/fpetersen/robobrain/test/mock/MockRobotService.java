package eu.fpetersen.robobrain.test.mock;

import java.util.ArrayList;
import java.util.Collection;

import eu.fpetersen.robobrain.behavior.BackAndForthBehavior;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;

public class MockRobotService extends RobotService {

	/**
	 * To make the status be set from testing classes.
	 * 
	 * @param running
	 *            True if service is supposed to be running, false if otherwise
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public Collection<CommandCenter> getAllCCs() {
		Collection<CommandCenter> ccs = new ArrayList<CommandCenter>();
		Robot robot = RobotFactory.getInstance(MockRobotService.this)
				.createSimpleRobot("TestBot");
		Behavior b1 = new BackAndForthBehavior();
		b1.initialize(robot, "BackAndForth");
		ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
		behaviors.add(b1);
		ccs.add(new CommandCenter(robot, behaviors));
		return ccs;
	}
}
