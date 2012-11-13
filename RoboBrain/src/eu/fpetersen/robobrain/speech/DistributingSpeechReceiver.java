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
package eu.fpetersen.robobrain.speech;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.communication.SpeechControlledBehaviorSwitcher;

/**
 * Distributes the results of speech recognition to all registered
 * {@link SpeechReceiver}s
 * 
 * @author Frederik Petersen
 * 
 */
public class DistributingSpeechReceiver extends BroadcastReceiver {

	/**
	 * All registered SpeechReceivers that are being fed with Speech Recognition
	 * Results, except for the {@link SpeechControlledBehaviorSwitcher} see
	 * below.
	 */
	private Set<SpeechReceiver> mReceivers;

	private SpeechControlledBehaviorSwitcher mSpeechControlledBehaviorSwitcher;

	public DistributingSpeechReceiver() {
		mReceivers = new HashSet<SpeechReceiver>();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isBehaviorCommand = false;
		if (intent.getAction().matches(RoboBrainIntent.ACTION_SPEECH)
				&& intent.hasExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS)) {
			String[] resultArray = intent.getStringArrayExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS);
			List<String> results = new ArrayList<String>();
			isBehaviorCommand = addResultsToListAndCheckForBehaviorCommand(resultArray, results);

			if (isBehaviorCommand && mSpeechControlledBehaviorSwitcher != null) {
				mSpeechControlledBehaviorSwitcher.onReceive(results);
			} else {
				for (SpeechReceiver rec : mReceivers) {
					rec.onReceive(results);
				}
			}
		}

	}

	/**
	 * 
	 * @param resultArray
	 *            Speech results to be checked vor behavior command and to be
	 *            filled into list
	 * @param results
	 *            List to be filled with results. If behavior is found in it,
	 *            only filled with "behavior" containing lines.
	 * @return True if results contain Behavior Command
	 */
	private boolean addResultsToListAndCheckForBehaviorCommand(String[] resultArray,
			List<String> results) {
		boolean isBehaviorCommand = false;
		for (String result : resultArray) {
			if (isBehaviorCommand) {
				if (result.toLowerCase().contains("behavior")) {
					results.add(result);
				}
			} else {
				if (result.toLowerCase().contains("behavior")) {
					results.clear();
					isBehaviorCommand = true;
				}
				results.add(result);
			}
		}
		return isBehaviorCommand;
	}

	public void addReceiver(SpeechReceiver rec) {
		mReceivers.add(rec);
	}

	public void removeReceiver(SpeechReceiver rec) {
		mReceivers.remove(rec);
	}

	public void setSpeechControlledBehaviorSwitcher(
			SpeechControlledBehaviorSwitcher SpeechControlledBehaviorSwitcher) {
		this.mSpeechControlledBehaviorSwitcher = SpeechControlledBehaviorSwitcher;
	}

}
