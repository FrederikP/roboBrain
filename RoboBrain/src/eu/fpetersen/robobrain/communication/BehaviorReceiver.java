package eu.fpetersen.robobrain.communication;

import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.behavior.Behavior;

public class BehaviorReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().matches(RoboBrainIntent.ACTION_BEHAVIORTRIGGER)) {
			boolean startIt = intent.getBooleanExtra(
					RoboBrainIntent.EXTRA_BEHAVIORSTATE, false);
			UUID uuid = (UUID) intent
					.getSerializableExtra(RoboBrainIntent.EXTRA_BEHAVIORUUID);
			final Behavior b = CommandCenter.getBehaviorForUUID(uuid);
			if (startIt) {
				Runnable behaviorStarter = new Runnable() {
					public void run() {
						b.startBehavior();
					}
				};
				Thread thread = new Thread(behaviorStarter);
				thread.start();
			} else {
				Runnable behaviorStopper = new Runnable() {
					public void run() {
						b.stopBehavior();
					}
				};
				Thread thread = new Thread(behaviorStopper);
				thread.start();
			}

		}
	}

}
