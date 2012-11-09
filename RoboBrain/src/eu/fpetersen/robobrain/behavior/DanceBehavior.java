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
package eu.fpetersen.robobrain.behavior;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
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

	@Override
	protected void onStart() {
		setupRgbTimer(1000);
		startRandomPlayingMusic();
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
	private void startRandomPlayingMusic() {
		File sdcard = Environment.getExternalStorageDirectory();
		File musicDir = new File(sdcard, "robobrain/music");
		if (!musicDir.exists() || !musicDir.isDirectory()) {
			RoboLog.alertError(getRobot().getRobotService(),
					"No music can be played. No /music directory in /robobrain");
		} else {
			FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".mp3") || filename.endsWith(".wma")) {
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
			}
		}

	}
}