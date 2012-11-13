/*******************************************************************************
 * RoboBrain - Control your Arduino Robots per Android Device
 * Copyright (c) 2012 Frederik Petersen.
 * All rights reserved.
 * 
 * This file is part of RoboBrain.
 * 
 *     RoboBrain is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     RoboBrain is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with RoboBrain.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Frederik Petersen - Project Owner, initial Implementation
 ******************************************************************************/
package eu.fpetersen.robobrain.services;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechResultManager;
import eu.fpetersen.robobrain.ui.Starter;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Is started and stopped by the {@link Starter} activity, just like the
 * {@link RobotService}. It tries to receive speech input and saves them to the
 * {@link SpeechResultManager} which then broadcasts them to be receives by a
 * {@link DistributingSpeechReceiver}
 * 
 * @author Frederik Petersen
 * 
 */
public class SpeechRecognizerService extends Service {

	protected static final String TAG = "RobotBrain - SpeechRecognizerService";
	private SpeechRecognizer mSpeechR;
	private static SpeechRecognizerService sInstance;

	/**
	 * Setup the Speech Recognizer with the Android API
	 */
	protected void setupSpeechRecognition() {

		mSpeechR = SpeechRecognizer.createSpeechRecognizer(SpeechRecognizerService.this);
		setSpeechRecognitionListener();

	}

	/**
	 * Set the SpeechRecognitionListener to handle results and events from the
	 * {@link SpeechRecognizer}
	 */
	private void setSpeechRecognitionListener() {
		mSpeechR.setRecognitionListener(new RecognitionListener() {

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
								SpeechRecognizerService.this, data);
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
		sInstance = SpeechRecognizerService.this;
		if (Starter.getInstance() != null) {
			if (SpeechRecognizer.isRecognitionAvailable(SpeechRecognizerService.getInstance())) {
				Starter.getInstance().runOnUiThread(new Runnable() {

					public void run() {
						setupSpeechRecognition();
					}
				});
			} else {
				RoboLog.alertWarning(sInstance, "No Speech Recognition available on this device.");
				this.stopSelf();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mSpeechR != null) {
			Starter.getInstance().runOnUiThread(new Runnable() {

				public void run() {
					mSpeechR.stopListening();
					mSpeechR.cancel();
					mSpeechR.destroy();
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
		final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");
		Starter.getInstance().runOnUiThread(new Runnable() {

			public void run() {
				if (mSpeechR != null) {
					intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
					mSpeechR.startListening(intent);
				} else {
					stopSelf();
				}

			}
		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static SpeechRecognizerService getInstance() {
		return sInstance;
	}

}