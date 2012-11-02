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

public class CommandCenter {

	private static HashMap<Robot, CommandCenter> ccPerRobot = new HashMap<Robot, CommandCenter>();

	private Robot robot;
	private List<Behavior> behaviors = new ArrayList<Behavior>();
	private static Map<UUID, Behavior> allBehaviors = new HashMap<UUID, Behavior>();

	public static Behavior getBehaviorForUUID(UUID uuid) {
		return allBehaviors.get(uuid);
	}

	private CommandCenter(Robot robot, List<Behavior> behaviors) {
		// TODO do this somewhere else. factory or something...
		this.robot = robot;

		for (Behavior b : behaviors) {
			addBehavior(b);
		}
	}

	private void addBehavior(Behavior b) {
		behaviors.add(b);
		allBehaviors.put(b.getId(), b);
	}

	public void connect() {
		RobotService rService = RobotService.getInstance();

		// this is how you tell Amarino to connect to a specific BT device from
		// within your own code
		Amarino.connect(rService, robot.getAddress());
	}

	public void disconnect() {

		RobotService rService = RobotService.getInstance();

		// if you connect in onStart() you must not forget to disconnect when
		// your app is closed
		Amarino.disconnect(rService, robot.getAddress());
	}

	public static void createInstance(Robot robot, List<Behavior> behaviors) {
		CommandCenter cc = ccPerRobot.get(robot);
		if (cc == null) {
			cc = new CommandCenter(robot, behaviors);
			ccPerRobot.put(robot, cc);
		}
	}

	public static void connectAll() {
		for (CommandCenter cc : ccPerRobot.values()) {
			cc.connect();
		}
	}

	public static void disconnectAll() {
		for (CommandCenter cc : ccPerRobot.values()) {
			cc.disconnect();
		}
	}

	public static Collection<CommandCenter> getAllCCs() {
		return ccPerRobot.values();
	}

	public Robot getRobot() {
		return robot;
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	public static CommandCenter getCCForAddress(Robot robot) {
		return ccPerRobot.get(robot);
	}

	public static CommandCenter getCCForAddress(String address) {
		for (CommandCenter center : ccPerRobot.values()) {
			if (center.getRobot().getAddress().matches(address)) {
				return center;
			}
		}
		// TODO Exception handling
		return null;
	}

	public static void removeAll() {
		ccPerRobot.clear();
	}

}
