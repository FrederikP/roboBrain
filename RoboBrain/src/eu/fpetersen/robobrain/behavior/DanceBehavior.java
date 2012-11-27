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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.color.RgbColor;
import eu.fpetersen.robobrain.color.RgbColorTable;
import eu.fpetersen.robobrain.color.RgbColorTableFactory;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.robot.parts.ProximitySensor;
import eu.fpetersen.robobrain.robot.parts.RgbLed;
import eu.fpetersen.robobrain.robot.parts.Servo;
import eu.fpetersen.robobrain.robot.parts.Motor.MotorState;

/**
 * Make the robot play music and move to it.
 * 
 * @author Frederik Petersen
 * 
 */
public class DanceBehavior extends Behavior {

	protected static final int SPEED = 200;
	protected static final int ANGLE = 200;
	private MediaPlayer mPlayer;

	@Override
	protected void onStart() {
		getRobot().getHeadServo().setToAngle(95);
		setupRgbTimer(250);
		startPlayingRandomMusic();
		startMovingRandomly(500);
	}

	@Override
	protected void behaviorLoop() {

		checkForObstacle();

		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {

			} else {
				startPlayingRandomMusic();
			}
		}
	}

	/**
	 * Makes sure that the robot does not hit an obstacle
	 */
	private void checkForObstacle() {
		if (getRobot().getMainMotor().getState() != MotorState.STOPPED) {
			if (getRobot().getFrontSensor().getValue() < 30
					&& getRobot().getMainMotor().getState() != MotorState.FORWARD) {
				mLog.log("Stopping due to obstacle in front", true);
				getRobot().getMainMotor().stop(0);
			}
			if (getRobot().getBackSensor().getValue() == 0
					&& getRobot().getMainMotor().getState() != MotorState.BACKWARD) {
				mLog.log("Stopping due to obstacle in back", true);
				getRobot().getMainMotor().stop(0);
			}
		}
	}

	@Override
	protected void onStop() {
		stopMusic();
		getRobot().getMainMotor().stop(0);
		getRobot().getHeadColorLed().set(0, 0, 0);
		getRobot().getHeadServo().setToAngle(95);
	}

	@Override
	protected void fillRequirements(Requirements requirements) {
		requirements.addPart("main_motor", Motor.class.getName());
		requirements.addPart("head_servo", Servo.class.getName());
		requirements.addPart("front_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("back_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("headcolor_rgbled", RgbLed.class.getName());
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
			mPlayer = MediaPlayer.create(getRobot().getRobotService(), Uri.fromFile(musicFile));
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
		final Timer timer = new Timer();

		TimerTask rgbTask = new TimerTask() {

			private RgbColorTable colorTable = RgbColorTableFactory.getInstance()
					.getStandardColorTableFromTextFile(
							getRobot().getRobotService().getResources().openRawResource(R.raw.rgb));

			@Override
			public void run() {
				if (!isTurnedOn()) {
					timer.cancel();
				} else {
					RgbColor randomColor = colorTable.getRandomColor();
					getRobot().getHeadColorLed().set(randomColor);
				}
			}
		};

		timer.scheduleAtFixedRate(rgbTask, 0, milliSeconds);
	}

	/**
	 * Randomly choose music file from /robobrain/music/ dir and play it
	 */
	private void startPlayingRandomMusic() {
		File sdcard = Environment.getExternalStorageDirectory();
		File musicDir = new File(sdcard, "robobrain/music");
		if (!musicDir.exists() || !musicDir.isDirectory()) {
			mLog.alertError("No music can be played. No /music directory in /robobrain");
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
				mLog.alertError("No music in directory /robobrain/music");
			} else {
				Random random = new Random();
				int rand = random.nextInt(numberOfFiles);
				startMusic(new File(musicDir, musicFiles[rand]));
			}
		}

	}

	/**
	 * Moves randomly changing movement at the rate specified.
	 * 
	 * @param millis
	 *            Rate at which to change movement
	 */
	private void startMovingRandomly(int millis) {
		final Timer timer = new Timer();

		TimerTask movementTask = new TimerTask() {

			@Override
			public void run() {
				if (!isTurnedOn()) {
					timer.cancel();
				} else {
					Random random = new Random();
					int randomMotorMovement = random.nextInt(5);
					if (randomMotorMovement == 0) {
						getRobot().getMainMotor().advance(SPEED);
					} else if (randomMotorMovement == 1) {
						getRobot().getMainMotor().backOff(SPEED);
					} else if (randomMotorMovement == 2) {
						getRobot().getMainMotor().turnLeft(ANGLE);
					} else if (randomMotorMovement == 3) {
						getRobot().getMainMotor().turnRight(ANGLE);
					} else if (randomMotorMovement == 4) {
						getRobot().getMainMotor().stop(0);
					}

					int randomSensorMovement = random.nextInt(3);
					if (randomSensorMovement == 0) {
						getRobot().getHeadServo().setToAngle(95);
					} else if (randomSensorMovement == 1) {
						getRobot().getHeadServo().setToAngle(140);
					} else if (randomSensorMovement == 2) {
						getRobot().getHeadServo().setToAngle(50);
					}
				}
			}
		};

		timer.scheduleAtFixedRate(movementTask, 0, millis);

	}

}
