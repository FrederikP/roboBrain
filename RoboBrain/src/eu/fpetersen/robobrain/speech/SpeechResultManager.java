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

import java.util.List;

import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;

/**
 * Saves the latest Speech results and broadcasts them.
 * 
 * @author Frederik Petersen
 * 
 */
public class SpeechResultManager {

	private List<String> latestResultList;
	private static SpeechResultManager instance;

	private SpeechResultManager() {
	}

	public static SpeechResultManager getInstance() {
		if (instance == null) {
			instance = new SpeechResultManager();
		}
		return instance;
	}

	/**
	 * Save new results and call {@link SpeechResultManager#broadcastResults()}
	 * 
	 * @param latestResultList
	 */
	public void allocateNewResults(Context context,
			List<String> latestResultList) {
		this.latestResultList = latestResultList;
		broadcastResults(context);
	}

	/**
	 * Send out intent to be receives by {@link DistributingSpeechReceiver}
	 */
	private void broadcastResults(Context context) {
		Intent intent = new Intent(RoboBrainIntent.ACTION_SPEECH);
		intent.putExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS,
				latestResultList.toArray(new String[latestResultList.size()]));
		context.sendBroadcast(intent);
	}
}
