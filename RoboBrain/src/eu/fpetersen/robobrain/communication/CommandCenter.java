package eu.fpetersen.robobrain.communication;

import java.util.HashMap;

import android.content.IntentFilter;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

import eu.fpetersen.robobrain.ui.Console;

public class CommandCenter {

	private RobotReceiver rReceiver;
	private String address;
	private Console console;

	public static HashMap<String, CommandCenter> ccPerMac = new HashMap<String, CommandCenter>();

	private CommandCenter(String address, Console console) {
		rReceiver = new RobotReceiver(console);
		this.address = address;
		this.console = console;

	}
	

	public void connect() {
		// in order to receive broadcasted intents we need to register our
		// receiver
		console.registerReceiver(rReceiver, new IntentFilter(
				AmarinoIntent.ACTION_RECEIVED));

		// this is how you tell Amarino to connect to a specific BT device from
		// within your own code
		Amarino.connect(console, address);
	}
	
	public void disconnect() {

		// if you connect in onStart() you must not forget to disconnect when
		// your app is closed
		Amarino.disconnect(console, address);

		// do never forget to unregister a registered receiver
		console.unregisterReceiver(rReceiver);
	}

	public static CommandCenter getInstance(String adress, Console console) {
		CommandCenter cc = ccPerMac.get(adress);
		if (cc != null) {
			return cc;
		} else {
			return new CommandCenter(adress, console);
		}
	}

}
