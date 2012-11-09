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
 *     
 *     Snippets taken from  https://github.com/felixpalmer/android-visualizer/blob/master/src/com/pheelicks/visualizer/VisualizerView.java
 *     {
 *     		Copyright 2011, Felix Palmer
 *
 * 			Licensed under the MIT license:
 * 			http://creativecommons.org/licenses/MIT/
 * 		}
 ******************************************************************************/
package eu.fpetersen.robobrain.behavior;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Environment;
import eu.fpetersen.robobrain.color.RGBColor;
import eu.fpetersen.robobrain.color.RGBColorTable;
import eu.fpetersen.robobrain.color.RGBColorTableFactory;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.Motor;
import eu.fpetersen.robobrain.robot.Motor.MotorState;
import eu.fpetersen.robobrain.robot.ProximitySensor;
import eu.fpetersen.robobrain.robot.RGBLED;
import eu.fpetersen.robobrain.robot.Servo;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Make the robot play music and move to it.
 * 
 * @author Frederik Petersen
 * 
 */
public class DanceBehavior extends Behavior {

	private MediaPlayer mPlayer;

	private Visualizer mVisualizer;

	@Override
	protected void onStart() {
		setupRgbTimer(1000);
		startPlayingRandomMusic();
	}

	@Override
	protected void behaviorLoop() {
		if (getRobot().getMainMotor().getState() != MotorState.STOPPED) {
			if (getRobot().getFrontSensor().getValue() < 30
					&& getRobot().getMainMotor().getState() != MotorState.FORWARD) {
				RoboLog.log(getRobot().getRobotService(),
						"Stopping due to obstacle in front");
				getRobot().getMainMotor().stop(0);
			}
			if (getRobot().getBackSensor().getValue() == 0
					&& getRobot().getMainMotor().getState() != MotorState.BACKWARD) {
				RoboLog.log(getRobot().getRobotService(),
						"Stopping due to obstacle in back");
				getRobot().getMainMotor().stop(0);
			}
		}

		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {

			} else {
				startPlayingRandomMusic();
			}
		} else {
			stopBehavior();
		}
	}

	@Override
	protected void onStop() {
		getRobot().getMainMotor().stop(0);
		getRobot().getHeadColorLED().set(0, 0, 0);
	}

	@Override
	protected void fillRequirements(Requirements requirements) {
		requirements.addPart("main_motor", Motor.class.getName());
		requirements.addPart("head_servo", Servo.class.getName());
		requirements.addPart("front_proxsensor",
				ProximitySensor.class.getName());
		requirements
				.addPart("back_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("headcolor_rgbled", RGBLED.class.getName());
	}

	/**
	 * Starts playing the supplied file
	 * 
	 * @param musicFile
	 *            The media file to play.
	 */
	private void startMusic(File musicFile) {

		if (musicFile.exists()) {
			stopMusic();
			mPlayer = MediaPlayer.create(getRobot().getRobotService(),
					Uri.fromFile(musicFile));
			mPlayer.start();
		}
	}

	/**
	 * Stops music if playing. Also releases the music player
	 */
	private void stopMusic() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.release();
			if (mVisualizer != null) {
				mVisualizer.release();
				mVisualizer = null;
			}
			mPlayer = null;
		}
	}

	/**
	 * Setup a timer that automatically changes the color to some random color
	 * 
	 * @param milliSeconds
	 *            Rate at which to change color in milli seconds
	 */
	private void setupRgbTimer(final long milliSeconds) {
		TimerTask rgbTask = new TimerTask() {

			private RGBColorTable colorTable = RGBColorTableFactory
					.getInstance().getStandardColorTableFromTextFile(
							getRobot().getRobotService());

			@Override
			public void run() {
				RGBColor randomColor = colorTable.getRandomColor();
				getRobot().getHeadColorLED().set(randomColor);
			}
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(rgbTask, 0, milliSeconds);
	}

	/**
	 * Randomly choose music file from /robobrain/music/ dir and play it
	 */
	private void startPlayingRandomMusic() {
		File sdcard = Environment.getExternalStorageDirectory();
		File musicDir = new File(sdcard, "robobrain/music");
		if (!musicDir.exists() || !musicDir.isDirectory()) {
			RoboLog.alertError(getRobot().getRobotService(),
					"No music can be played. No /music directory in /robobrain");
		} else {
			FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".mp3") || filename.endsWith(".wma")
							|| filename.endsWith(".ogg")) {
						return true;
					}
					return false;
				}
			};
			String[] musicFiles = musicDir.list(filter);
			int numberOfFiles = musicFiles.length;
			if (musicFiles.length < 1) {
				RoboLog.alertError(getRobot().getRobotService(),
						"No music in directory /robobrain/music");
			} else {
				int random = (int) (Math.random() * numberOfFiles);
				startMusic(new File(musicDir.getAbsolutePath()
						+ musicFiles[random]));
				setupFFTListener();
			}
		}

	}

	/**
	 * Use the {@link Visualizer} to extract FFT data from the
	 * {@link MediaPlayer}
	 */
	private void setupFFTListener() {
		if (mVisualizer == null && mPlayer != null) {
			mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
			mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
			Visualizer.OnDataCaptureListener listener = new Visualizer.OnDataCaptureListener() {

				public void onWaveFormDataCapture(Visualizer visualizer,
						byte[] waveform, int samplingRate) {
					// Don't do anything, Shouldn't be called anyways
				}

				public void onFftDataCapture(Visualizer visualizer,
						final byte[] fft, int samplingRate) {
					Runnable reactTask = new Runnable() {

						public void run() {
							reactToFFTData(fft);
						}
					};

					Thread thread = new Thread(reactTask);
					thread.start();

				}
			};
			mVisualizer.setDataCaptureListener(listener,
					Visualizer.getMaxCaptureRate() / 32, false, true);

			// Enabled Visualizer and disable when we're done with the stream
			mVisualizer.setEnabled(true);
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					mVisualizer.setEnabled(false);
				}

			});

		}
	}

	/**
	 * Everytime fft data coming from audio is updated. This can be used to
	 * react to patterns in music.
	 * 
	 * @param fft
	 *            The fast forier transform data coming from the media
	 */
	protected void reactToFFTData(byte[] fft) {
		// TODO Do something useful here. For now: Save raw data to file, to
		// analyze:
		File sdcard = Environment.getExternalStorageDirectory();
		File musicDir = new File(sdcard, "robobrain/tmp");
		if (!musicDir.exists()) {
			musicDir.mkdir();
		}

		File fftDataTextFile = new File(musicDir, "fftData.txt");
		if (fftDataTextFile.exists()) {
			fftDataTextFile.delete();
		}
		BufferedWriter out;
		try {
			// Create file
			FileWriter fstream = new FileWriter("fftData.txt");
			out = new BufferedWriter(fstream);
			int currentPosInMillis = mPlayer.getCurrentPosition();
			int seconds = (int) (currentPosInMillis / 1000);
			int millis = (int) (currentPosInMillis % 1000);
			out.append(seconds + "." + millis + "-->");
			for (byte b : fft) {
				Byte by = new Byte(b);
				out.append(by.intValue() + " ");
			}
			// Close the output stream
			out.close();

		} catch (IOException e) {
			RoboLog.alertError(getRobot().getRobotService(),
					"Failed to create fftData.txt");
		}
	}
}
