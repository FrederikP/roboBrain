package eu.fpetersen.robobrain.speech;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

public class DistributingSpeechReceiver extends BroadcastReceiver {

	private Set<SpeechReceiver> receivers;

	public DistributingSpeechReceiver() {
		receivers = new HashSet<SpeechReceiver>();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().matches(RoboBrainIntent.ACTION_SPEECH)
				&& intent.hasExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS)) {
			String[] resultArray = intent
					.getStringArrayExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS);
			List<String> results = new ArrayList<String>();
			for (String result : resultArray) {
				results.add(result);
			}

			for (SpeechReceiver rec : receivers) {
				rec.onReceive(results);
			}
		}

	}

	public void addReceiver(SpeechReceiver rec) {
		receivers.add(rec);
	}

	public void removeReceiver(SpeechReceiver rec) {
		receivers.remove(rec);
	}

}
