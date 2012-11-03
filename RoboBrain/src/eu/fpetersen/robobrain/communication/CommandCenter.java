package eu.fpetersen.robobrain.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	/**
	 * Static reference to a Map of all existing CommandCenters keyed by their
	 * Robots.
	 */
	private static Map<Robot, CommandCenter> ccPerRobot = new HashMap<Robot, CommandCenter>();

	private Robot robot;
	private List<Behavior> behaviors = new ArrayList<Behavior>();

	/**
	 * Static reference to a Map of all existing Behaviors identified by their
	 * UUIDs. By having this additional Map, Behaviors can easily be triggered
	 * from other components like the UI
	 */
	private static Map<UUID, Behavior> allBehaviors = new HashMap<UUID, Behavior>();

	/**
	 * Return behavior for UUID. If it does not exist null is returned.
	 * 
	 * @param uuid
	 *            Identifies the Behavior
	 * @return The behavior identified by the UUID
	 */
	public static Behavior getBehaviorForUUID(UUID uuid) {
		if (allBehaviors.containsKey(uuid)) {
			return allBehaviors.get(uuid);
		} else {
			return null;
		}
	}

	/**
	 * Creates a new CommandCenter for the given robot and it's behaviors.
	 * 
	 * @param robot
	 *            The robot this CommandCenter is created for
	 * @param behaviors
	 *            The behaviors of the robot above.
	 */
	private CommandCenter(Robot robot, List<Behavior> behaviors) {
		this.robot = robot;

		for (Behavior b : behaviors) {
			addBehavior(b);
		}
	}

	/**
	 * This method needs to be called whenever a new behavior is added to a
	 * command center as it needs to be referenced from several locations.
	 * 
	 * @param b
	 *            Behavior to be added
	 */
	private void addBehavior(Behavior b) {
		behaviors.add(b);
		allBehaviors.put(b.getId(), b);
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
	 * Creates a new CommandCenter for the given Robot and behaviors and adds it
	 * the static map of all Command Centers.
	 * 
	 * @param robot
	 *            The robot this CommandCenter is created for
	 * @param behaviors
	 *            The behaviors of the robot above.
	 */
	public static void createInstance(Robot robot, List<Behavior> behaviors) {
		CommandCenter cc = ccPerRobot.get(robot);
		if (cc == null) {
			cc = new CommandCenter(robot, behaviors);
			ccPerRobot.put(robot, cc);
		}
	}

	/**
	 * Connect to all Arduino Devices
	 */
	public static void connectAll() {
		for (CommandCenter cc : ccPerRobot.values()) {
			cc.connect();
		}
	}

	/**
	 * Disconnect from all Arduino Devices
	 */
	public static void disconnectAll() {
		for (CommandCenter cc : ccPerRobot.values()) {
			cc.disconnect();
		}
	}

	/**
	 * Get all existing CommandCenters
	 * 
	 * @return All existing CommandCenters
	 */
	public static Collection<CommandCenter> getAllCCs() {
		return ccPerRobot.values();
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

	/**
	 * Get the CommandCenter for the specified Robot
	 * 
	 * @param robot
	 *            Robot of the CommandCenter to be returned
	 * @return CommandCenter for the specified robot
	 */
	public static CommandCenter getCCForRobot(Robot robot) {
		return ccPerRobot.get(robot);
	}

	/**
	 * Get the CommandCenter for the specified MACAddress
	 * 
	 * @param address
	 *            MAC address of the Arduino Device
	 * @return CommandCenter for the specified MACAddress
	 */
	public static CommandCenter getCCForAddress(String address) {
		for (CommandCenter center : ccPerRobot.values()) {
			if (center.getRobot().getAddress().matches(address)) {
				return center;
			}
		}
		// TODO Exception handling
		return null;
	}

	/**
	 * Remove all CommandCenters and behavior references
	 */
	public static void removeAll() {
		ccPerRobot.clear();
		allBehaviors.clear();
	}

}
