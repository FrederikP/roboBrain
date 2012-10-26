package eu.fpetersen.robobrain.speech;

import java.util.List;

import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

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

	public void allocateNewResults(List<String> latestResultList) {
		this.latestResultList = latestResultList;
		broadcastResults();
	}

	private void broadcastResults() {
		Intent intent = new Intent(RoboBrainIntent.ACTION_SPEECH);
		intent.putExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS,
				(String[]) latestResultList.toArray());
		SpeechRecognizerService.getInstance().sendBroadcast(intent);
	}
}
