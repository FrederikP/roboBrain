package eu.fpetersen.robobrain.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.IntentFilter;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.behavior.BackAndForthBehavior;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.robot.Robot;

public class CommandCenter {

	private static HashMap<String, CommandCenter> ccPerMac = new HashMap<String, CommandCenter>();

	private RobotReceiver rReceiver;
	private String address;
	private Robot robot;

	private List<Behavior> behaviors = new ArrayList<Behavior>();
	private static Map<UUID, Behavior> allBehaviors = new HashMap<UUID, Behavior>();

	private CommandCenter(String address) {
		// TODO do this somewhere else. factory or something...
		robot = new Robot(address, "YARP");
		behaviors.add(new BackAndForthBehavior(robot, "Back-and-Forth"));
		rReceiver = new RobotReceiver(robot);
		this.address = address;

	}

	public void connect() {
		RobotService rService = RobotService.getInstance();
		// in order to receive broadcasted intents we need to register our
		// receiver
		rService.registerReceiver(rReceiver, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));

		// this is how you tell Amarino to connect to a specific BT device from
		// within your own code
		Amarino.connect(rService, address);
	}

	public void disconnect() {

		RobotService rService = RobotService.getInstance();

		// if you connect in onStart() you must not forget to disconnect when
		// your app is closed
		Amarino.disconnect(rService, address);

		// do never forget to unregister a registered receiver
		rService.unregisterReceiver(rReceiver);
	}

	public static CommandCenter getInstance(String address) {
		CommandCenter cc = ccPerMac.get(address);
		if (cc == null) {
			cc = new CommandCenter(address);
			ccPerMac.put(address, cc);
		}
		return cc;
	}

	public static void connectAll() {
		for (CommandCenter cc : ccPerMac.values()) {
			cc.connect();
		}
	}

	public static void disconnectAll() {
		for (CommandCenter cc : ccPerMac.values()) {
			cc.disconnect();
		}
	}

	public static Collection<CommandCenter> getAllCCs() {
		return ccPerMac.values();
	}

	public Robot getRobot() {
		return robot;
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

}
