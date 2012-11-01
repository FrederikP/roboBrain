package eu.fpetersen.robobrain.speech;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import eu.fpetersen.robobrain.ui.Starter;
import eu.fpetersen.robobrain.util.RoboLog;

public class SpeechRecognizerService extends Service {

	protected static final String TAG = "RobotBrain - SpeechRecognizerService";
	private SpeechRecognizer speechR;
	private static SpeechRecognizerService instance;

	protected void setupSpeechRecognition() {

		speechR = SpeechRecognizer
				.createSpeechRecognizer(SpeechRecognizerService.this);
		setSpeechRecognitionListener();

	}

	private void setSpeechRecognitionListener() {
		speechR.setRecognitionListener(new RecognitionListener() {

			public void onReadyForSpeech(Bundle params) {
				// Log.d(TAG, "onReadyForSpeech");
			}

			public void onBeginningOfSpeech() {
				// Log.d(TAG, "onBeginningOfSpeech");
			}

			public void onRmsChanged(float rmsdB) {
				// Log.d(TAG, "onRmsChanged");
			}

			public void onBufferReceived(byte[] buffer) {
				// Log.d(TAG, "onBufferReceived");
			}

			public void onEndOfSpeech() {
				// Log.d(TAG, "onEndofSpeech");
			}

			public void onError(int error) {
				// Log.e(TAG, "error " + error);
				// RoboLog.log("Error, Recognition Service is busy");
				startSpeechListening();
			}

			public void onResults(Bundle results) {
				Log.d(TAG, "onResults " + results);
				final ArrayList<String> data = results
						.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				for (int i = 0; i < data.size(); i++) {
					data.set(i, data.get(i).toLowerCase());
					Log.d(TAG, "result " + data.get(i));
				}
				Runnable interpretTask = new Runnable() {

					public void run() {
						SpeechResultManager.getInstance().allocateNewResults(
								data);
					}
				};
				Thread thread = new Thread(interpretTask);
				thread.start();

				startSpeechListening();
			}

			public void onPartialResults(Bundle partialResults) {
				// Log.d(TAG, "onPartialResults");
			}

			public void onEvent(int eventType, Bundle params) {
				// Log.d(TAG, "onEvent " + eventType);
			}
		});
	}

	@Override
	public void onCreate() {
		instance = SpeechRecognizerService.this;
		if (SpeechRecognizer.isRecognitionAvailable(SpeechRecognizerService
				.getInstance())) {
			Starter.getInstance().runOnUiThread(new Runnable() {

				public void run() {
					setupSpeechRecognition();
				}
			});
		} else {
			RoboLog.log("No Speech Recognition available on this device.");
			this.stopSelf();
		}
	}

	@Override
	public void onDestroy() {
		if (speechR != null) {
			Starter.getInstance().runOnUiThread(new Runnable() {

				public void run() {
					speechR.stopListening();
					speechR.cancel();
					speechR.destroy();
				}
			});
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startSpeechListening();
		return super.onStartCommand(intent, flags, startId);
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
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static SpeechRecognizerService getInstance() {
		return instance;
	}

}
