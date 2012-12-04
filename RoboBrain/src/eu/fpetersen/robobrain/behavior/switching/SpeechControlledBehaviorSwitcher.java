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
package eu.fpetersen.robobrain.behavior.switching;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.speech.SpeechReceiver;

/**
 * Allows switching behaviors per voice.
 * 
 * @author Frederik Petersen
 * 
 */
public class SpeechControlledBehaviorSwitcher implements SpeechReceiver {

	private BehaviorSwitcher mSwitcher;

	public SpeechControlledBehaviorSwitcher(BehaviorSwitcher switcher) {
		mSwitcher = switcher;
	}

	public void onReceive(List<String> results) {
		for (String result : results) {
			if (result.toLowerCase(Locale.getDefault()).contains("behavior")) {
				boolean found = false;
				if (result.contains("start")) {
					found = toggleBehaviorForWords(
							result.toLowerCase(Locale.getDefault()).replace("behavior", "")
									.replace("start", ""), true);
				} else if (result.contains("stop")) {
					found = toggleBehaviorForWords(
							result.toLowerCase(Locale.getDefault()).replace("behavior", "")
									.replace("stop", ""), false);
				}
				if (found) {
					break;
				}
			}
		}
	}

	/**
	 * Look for word that identifies a behavior by it's speech attribute
	 * assigned in the behaviormapping.xml. Start/Stop that behavior if found.
	 * 
	 * Watch out: If multiple behaviors have the same speechName value, only the
	 * first behavior found is started/stopped. If the robot that has the
	 * behavior is already running a behavior, stop that behavior, before
	 * starting the new one.
	 * 
	 * @param result
	 * @param start
	 *            true if behavior should be started, false if stopped
	 * @return true if found a behavior to toggle, false if not
	 */
	private boolean toggleBehaviorForWords(String result, boolean start) {
		Map<UUID, Behavior> allBehaviors = mSwitcher.getRobotService().getAllBehaviors();
		for (Entry<UUID, Behavior> entry : allBehaviors.entrySet()) {
			String speechName = entry.getValue().getSpeechName().toLowerCase(Locale.getDefault());
			if (speechName == null || speechName.matches("")) {
				continue;
			}
			if (!start && result.contains(" all ")) {
				mSwitcher.stopAllBehaviors();
			}

			if (result.contains(speechName)) {
				if (start) {
					mSwitcher.startBehavior(entry.getKey());
				} else {
					mSwitcher.stopBehavior(entry.getKey());
				}
				return true;
			}
		}
		return false;
	}

}
