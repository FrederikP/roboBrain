package eu.fpetersen.robobrain.behavior;

import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import eu.fpetersen.robobrain.color.RGBColor;
import eu.fpetersen.robobrain.color.RGBColorTable;
import eu.fpetersen.robobrain.color.RGBColorTableFactory;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.RGBLED;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.ui.Starter;
import eu.fpetersen.robobrain.util.RoboLog;

public class ReactToSpeechBehavior extends Behavior {

	protected static final String TAG = "ReactToSpeech-Behavior";
	private SpeechRecognizer speechR;
	private RGBColorTable colorTable;

	public ReactToSpeechBehavior(Robot robot, String name) {
		super(robot, name);
		colorTable = RGBColorTableFactory.getInstance()
				.getStandardColorTableFromTextFile();
	}

	protected void setupSpeechRecognition() {

		speechR = SpeechRecognizer.createSpeechRecognizer(RobotService
				.getInstance());
		speechR.setRecognitionListener(new RecognitionListener() {

			public void onReadyForSpeech(Bundle params) {
				Log.d(TAG, "onReadyForSpeech");
			}

			public void onBeginningOfSpeech() {
				Log.d(TAG, "onBeginningOfSpeech");
			}

			public void onRmsChanged(float rmsdB) {
				Log.d(TAG, "onRmsChanged");
			}

			public void onBufferReceived(byte[] buffer) {
				Log.d(TAG, "onBufferReceived");
			}

			public void onEndOfSpeech() {
				Log.d(TAG, "onEndofSpeech");
			}

			public void onError(int error) {
				Log.e(TAG, "error " + error);
				startSpeechListening();
			}

			public void onResults(Bundle results) {
				Log.d(TAG, "onResults " + results);
				ArrayList<String> data = results
						.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				for (int i = 0; i < data.size(); i++) {
					data.set(i, data.get(i).toLowerCase());
					Log.d(TAG, "result " + data.get(i));
				}
				interpretSpeechResults(data);
				startSpeechListening();
			}

			public void onPartialResults(Bundle partialResults) {
				Log.d(TAG, "onPartialResults");
			}

			public void onEvent(int eventType, Bundle params) {
				Log.d(TAG, "onEvent " + eventType);
			}
		});

	}

	protected void interpretSpeechResults(ArrayList<String> data) {
		RGBLED led = getRobot().getHeadLED();
		Set<String> colorNames = colorTable.getNames();
		for (String s : data) {
			for (String colorName : colorNames) {
				boolean match = false;
				if (colorName.contains(" ")) {
					match = checkMultipleWordColorName(s, colorName);
				} else {
					if (s.contains(colorName)) {
						match = true;
					}
				}

				if (match) {
					RGBColor color = colorTable.getColorForName(colorName);
					led.set(color.getRed(), color.getGreen(), color.getBlue());
				}
			}
		}

	}

	private boolean checkMultipleWordColorName(String s, String colorName) {
		boolean match = false;
		StringTokenizer tokenizer = new StringTokenizer(colorName);
		int matchCount = 0;
		int wordCount = tokenizer.countTokens();
		while (tokenizer.hasMoreTokens()) {
			if (s.contains(tokenizer.nextToken())) {
				matchCount++;
			}
		}
		if (matchCount == wordCount) {
			match = true;
		}
		return match;
	}

	@Override
	public void startBehavior() {
		if (SpeechRecognizer.isRecognitionAvailable(RobotService.getInstance())) {
			Starter.getInstance().runOnUiThread(new Runnable() {

				public void run() {
					setupSpeechRecognition();
				}
			});
			startSpeechListening();
			super.startBehavior();
		} else {
			RoboLog.log("Cannot connect to speech Recognition Service.");
			stopBehavior();
		}

	}

	protected void startSpeechListening() {
		final Intent intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"voice.recognition.test");
		Starter.getInstance().runOnUiThread(new Runnable() {

			public void run() {

				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
				speechR.startListening(intent);

			}
		});
	}

	@Override
	protected void behaviorLoop() {

	}

	@Override
	public void stopBehavior() {
		if (speechR != null) {
			Starter.getInstance().runOnUiThread(new Runnable() {

				public void run() {
					speechR.stopListening();
					speechR.cancel();
					speechR.destroy();
				}
			});
		}
		super.stopBehavior();
	}

}
