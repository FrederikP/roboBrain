package eu.fpetersen.robobrain.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.ui.Console;

/**
 * Receives intents to display messages in the console activity of the app.
 * 
 * @author Frederik Petersen
 * 
 */
public class ConsoleReceiver extends BroadcastReceiver {

	/**
	 * Console Activity this Receiver receives intents for
	 */
	private Console console;

	public ConsoleReceiver(Console console) {
		this.console = console;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(RoboBrainIntent.ACTION_OUTPUT)) {
			console.appendText(intent
					.getStringExtra(RoboBrainIntent.EXTRA_OUTPUT));
		}

	}

}
