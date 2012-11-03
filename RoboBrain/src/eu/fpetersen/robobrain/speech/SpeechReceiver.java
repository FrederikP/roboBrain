package eu.fpetersen.robobrain.speech;

import java.util.List;

/**
 * Can be registered with a {@link DistributingSpeechReceiver} to be called when
 * SpeechRecognition Results are available
 * 
 * @author Frederik Petersen
 * 
 */
public interface SpeechReceiver {

	/**
	 * Called when the {@link DistributingSpeechReceiver} receives new Speech
	 * Recognition Results
	 * 
	 * @param results
	 *            List of Speech Recognition results that all represent a
	 *            possible speech input. First one is most likely
	 */
	void onReceive(List<String> results);

}
