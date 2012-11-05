package eu.fpetersen.robobrain.speech;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

/**
 * Saves the latest Speech results and broadcasts them.
 * 
 * @author Frederik Petersen
 * 
 */
public class SpeechResultManager {

	private List<String> latestResultList;
	private static SpeechResultManager instance;

	private SpeechResultManager() {
	}

	public static SpeechResultManager getInstance() {
		if (instance == null) {
			instance = new SpeechResultManager();
		}
		return instance;
	}

	/**
	 * Save new results and call {@link SpeechResultManager#broadcastResults()}
	 * 
	 * @param latestResultList
	 */
	public void allocateNewResults(Context context,
			List<String> latestResultList) {
		this.latestResultList = latestResultList;
		broadcastResults(context);
	}

	/**
	 * Send out intent to be receives by {@link DistributingSpeechReceiver}
	 */
	private void broadcastResults(Context context) {
		Intent intent = new Intent(RoboBrainIntent.ACTION_SPEECH);
		intent.putExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS,
				latestResultList.toArray(new String[latestResultList.size()]));
		context.sendBroadcast(intent);
	}
}
