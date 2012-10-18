package eu.fpetersen.robobrain.communication;

import eu.fpetersen.robobrain.ui.Console;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConsoleReceiver extends BroadcastReceiver {

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
