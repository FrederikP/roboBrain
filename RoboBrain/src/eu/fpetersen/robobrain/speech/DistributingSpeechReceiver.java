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
	 * Results
	 */
	private Set<SpeechReceiver> receivers;

	public DistributingSpeechReceiver() {
		receivers = new HashSet<SpeechReceiver>();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().matches(RoboBrainIntent.ACTION_SPEECH)
				&& intent.hasExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS)) {
			String[] resultArray = intent
					.getStringArrayExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS);
			List<String> results = new ArrayList<String>();
			for (String result : resultArray) {
				results.add(result);
			}

			for (SpeechReceiver rec : receivers) {
				rec.onReceive(results);
			}
		}

	}

	public void addReceiver(SpeechReceiver rec) {
		receivers.add(rec);
	}

	public void removeReceiver(SpeechReceiver rec) {
		receivers.remove(rec);
	}

}
