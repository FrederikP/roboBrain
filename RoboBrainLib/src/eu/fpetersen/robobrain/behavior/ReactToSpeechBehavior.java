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

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.speech.SpeechRecognizer;
import eu.fpetersen.robobrain.color.RgbColor;
import eu.fpetersen.robobrain.color.RgbColorTable;
import eu.fpetersen.robobrain.color.RgbColorTableFactory;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.robot.parts.Motor.MotorState;
import eu.fpetersen.robobrain.robot.parts.ProximitySensor;
import eu.fpetersen.robobrain.robot.parts.RgbLed;
import eu.fpetersen.robobrain.robot.parts.Servo;
import eu.fpetersen.robobrain.speech.SpeechReceiver;
import eu.fpetersen.robobrainlib.R;

/**
 * This Behavior reacts to speech commands. For now it changes the Robot's LED
 * color to what the user says. There are different colornames imported from a
 * long list of color names. When speech is detected, the longest possible color
 * name is matched.
 * 
 * Now there is also a reaction to movement commands: Stop, Forward, Backward,
 * Left, Right
 * 
 * @author Frederik Petersen
 * 
 * 
 */
public class ReactToSpeechBehavior extends Behavior implements SpeechReceiver {

	protected static final String TAG = "ReactToSpeech-Behavior";
	private static final int SPEED = 200;
	private static final int ANGLE = 30;
	private RgbColorTable mColorTable;
	private List<String> mColorNames;

	@Override
	public void initialize(Robot robot, String name, String speechName) {
		super.initialize(robot, name, speechName);
		mColorTable = RgbColorTableFactory.getInstance().getStandardColorTableFromTextFile(
				getRobot().getRobotService().getResources().openRawResource(R.raw.rgb));
		mColorNames = mColorTable.getNames();
	}

	/**
	 * Evaluate speech results to look for colornames. Colornames are evaluated
	 * from most words to one word, to set a priority on longer color names. If
	 * color names are longer than one word,
	 * {@link eu.fpetersen.robobrain.behavior.ReactToSpeechBehavior#checkMultipleWordColorName(String, String)
	 * checkMultipleWordColorName()} is called.
	 * 
	 * @param results
	 *            Result String list of Speech results.
	 */
	private void setLED(List<String> results) {
		RgbLed led = getRobot().getHeadColorLed();
		boolean colorMatch = false;
		for (String s : results) {
			for (String colorName : mColorNames) {
				if (colorName.contains(" ")) {
					colorMatch = checkMultipleWordColorName(s, colorName);
				} else {
					if (s.contains(colorName)) {
						colorMatch = true;
					}
				}

				if (colorMatch) {
					RgbColor color = mColorTable.getColorForName(colorName);
					mLog.log("Displaying color: " + colorName, false);
					led.set(color.getRed(), color.getGreen(), color.getBlue());
					break;
				}
			}
			if (colorMatch) {
				break;
			}
		}
	}

	/**
	 * Check if name can be matched with that colorName. All words must be found
	 * in input string
	 * 
	 * @param s
	 *            Input String for which the method checks if colorname is
	 *            included
	 * @param colorName
	 *            Colorname for which the String s is checked.
	 * @return True if colorName is in String s.
	 */
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

		getRobot().getRobotService().getDistributingSpeechReceiver()
				.addReceiver(ReactToSpeechBehavior.this);
		String prop = System.getProperty((getRobot().getRobotService()
				.getString(R.string.envvar_testing)));
		boolean testing = (prop != null && prop.matches("true"));

		if (testing || SpeechRecognizer.isRecognitionAvailable(getRobot().getRobotService())) {
			super.startBehavior();
		} else {
			mLog.alertWarning("Cannot connect to speech Recognition Service.");
			stopBehavior();
		}

	}

	@Override
	protected void behaviorLoop() {
		if (getRobot().getMainMotor().getState() != MotorState.STOPPED) {
			if (getRobot().getFrontSensor().getValue() < 30
					&& getRobot().getMainMotor().getState() == MotorState.FORWARD) {
				mLog.log("Stopping due to obstacle in front", true);
				getRobot().getMainMotor().stop(0);
			}
			if (getRobot().getBackSensor().getValue() == 0
					&& getRobot().getMainMotor().getState() == MotorState.BACKWARD) {
				mLog.log("Stopping due to obstacle in back", true);
				getRobot().getMainMotor().stop(0);
			}
		}
	}

	@Override
	public void stopBehavior() {
		getRobot().getRobotService().getDistributingSpeechReceiver()
				.removeReceiver(ReactToSpeechBehavior.this);
		super.stopBehavior();
	}

	public void onReceive(List<String> results) {
		setMotor(results);
		setLED(results);
	}

	/**
	 * Check the speech resultList for hints about motor state switching
	 * 
	 * @param results
	 *            Speech Result List
	 */
	private void setMotor(List<String> results) {
		// First of all, if Motor is not stopped. And stop is in results -> Stop
		// Robot, nothing else
		Motor motor = getRobot().getMainMotor();
		if (motor.getState() != MotorState.STOPPED) {
			for (String resultLine : results) {
				if (resultLine.toLowerCase(Locale.US).contains("stop")) {
					mLog.log("Received voice command to stop", true);
					motor.stop(0);
					return;
				}
			}
		}

		// If stop is not in results, do whatever is found first
		for (String resultLine : results) {
			if (resultLine.toLowerCase(Locale.US).contains("forward")) {
				mLog.log("Received voice command to advance", true);
				motor.advance(SPEED);
				break;
			} else if (resultLine.toLowerCase(Locale.US).contains("backward")) {
				mLog.log("Received voice command to backoff", true);
				motor.backOff(SPEED);
				break;
			} else if (resultLine.toLowerCase(Locale.US).contains("right")) {
				mLog.log("Received voice command to turn right", true);
				motor.turnRight(ANGLE);
				break;
			} else if (resultLine.toLowerCase(Locale.US).contains("left")) {
				mLog.log("Received voice command to turn left", true);
				motor.turnLeft(ANGLE);
				break;
			}
		}

	}

	@Override
	protected void onStop() {
		getRobot().getHeadColorLed().set(0, 0, 0);
	}

	@Override
	protected void fillRequirements(Requirements requirements) {
		requirements.addPart("main_motor", Motor.class.getName());
		requirements.addPart("head_servo", Servo.class.getName());
		requirements.addPart("front_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("back_proxsensor", ProximitySensor.class.getName());
		requirements.addPart("headcolor_rgbled", RgbLed.class.getName());
	}

	@Override
	protected void onStart() {
		// Nothing to setup for now.
	}

}
