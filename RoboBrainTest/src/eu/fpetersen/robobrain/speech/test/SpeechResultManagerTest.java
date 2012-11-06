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
package eu.fpetersen.robobrain.speech.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.speech.SpeechResultManager;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests the {@link SpeechResultManager}
 * 
 * @author Frederik Petersen
 * 
 */
public class SpeechResultManagerTest extends AndroidTestCase {

	/**
	 * Test allocating mocked speech results to the speech result manager
	 */
	public void testAllocatingResults() {
		SpeechResultManager resultManager = SpeechResultManager.getInstance();
		final Set<String> checkFunctionalitySet = new HashSet<String>();
		assertNotNull(resultManager);

		BroadcastReceiver mockReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String[] results = intent
						.getStringArrayExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS);
				assertNotNull(results);
				checkFunctionalitySet.add(results[0]);
			}
		};

		String testString = UUID.randomUUID().toString();

		getContext().registerReceiver(mockReceiver,
				new IntentFilter(RoboBrainIntent.ACTION_SPEECH));
		List<String> mockData = new ArrayList<String>();
		mockData.add(testString);
		resultManager.allocateNewResults(getContext(), mockData);

		Helper.sleepMillis(100);

		assertTrue(checkFunctionalitySet.contains(testString));

	}

}
