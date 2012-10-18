package eu.fpetersen.robobrain.communication;

import java.util.HashMap;

import android.content.IntentFilter;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;


public class CommandCenter {

	private RobotReceiver rReceiver;
	private String address;

	private static HashMap<String, CommandCenter> ccPerMac = new HashMap<String, CommandCenter>();

	private CommandCenter(String address) {
		rReceiver = new RobotReceiver();
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

}
