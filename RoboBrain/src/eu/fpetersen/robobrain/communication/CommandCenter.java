package eu.fpetersen.robobrain.communication;

import java.util.ArrayList;
import java.util.List;

import at.abraxas.amarino.Amarino;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.robot.Robot;

/**
 * Each command center holds a reference to one robot and it's behaviors. The
 * static context of the class holds a reference to a Map of all Behaviors and
 * their identifying UUIDs.
 * 
 * @author Frederik Petersen
 * 
 */
public class CommandCenter {

	private Robot robot;
	private List<Behavior> behaviors = new ArrayList<Behavior>();

	/**
	 * Creates a new CommandCenter for the given robot and it's behaviors.
	 * 
	 * @param robot
	 *            The robot this CommandCenter is created for
	 * @param behaviors
	 *            The behaviors of the robot above.
	 */
	CommandCenter(Robot robot, List<Behavior> behaviors) {
		this.robot = robot;

		addBehaviors(behaviors);
	}

	/**
	 * Add behaviors to robot
	 * 
	 * @param behaviors
	 *            List of behaviors to add
	 */
	private void addBehaviors(List<Behavior> behaviors) {
		for (Behavior b : behaviors) {
			this.behaviors.add(b);
		}
	}

	/**
	 * Connect to the Arduino device of this CommandCenters Robot.
	 */
	public void connect() {
		RobotService rService = RobotService.getInstance();

		Amarino.connect(rService, robot.getAddress());
	}

	/**
	 * Disconnect from the Arduino device of this CommandCenters Robot.
	 */
	public void disconnect() {

		RobotService rService = RobotService.getInstance();

		Amarino.disconnect(rService, robot.getAddress());
	}

	/**
	 * 
	 * @return The robot of this CommandCenter
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * 
	 * @return The behaviors of this CommandCenters robot.
	 */
	public List<Behavior> getBehaviors() {
		return behaviors;
	}

}
