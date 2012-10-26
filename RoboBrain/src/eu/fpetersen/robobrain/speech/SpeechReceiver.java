package eu.fpetersen.robobrain.speech;

import java.util.List;

public interface SpeechReceiver {

	void onReceive(List<String> results);

}
