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

import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;
import eu.fpetersen.robobrain.speech.SpeechReceiver;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests the {@link DistributingSpeechReceiver}.
 * 
 * @author Frederik Petersen
 * 
 */
public class DistributingSpeechReceiverTest extends AndroidTestCase {

	DistributingSpeechReceiver distSR;

	@Override
	protected void setUp() throws Exception {
		distSR = new DistributingSpeechReceiver();
		getContext().registerReceiver(distSR,
				new IntentFilter(RoboBrainIntent.ACTION_SPEECH));
		super.setUp();
	}

	/**
	 * Tests the {@link DistributingSpeechReceiver} by setting up some
	 * {@link SpeechReceiver}s and checking if they receive the speech result
	 * data if intent is sent to {@link DistributingSpeechReceiver}
	 */
	public void testDistributingResults() {
		final Set<Integer> successfulReceivers = new HashSet<Integer>();
		final Set<SpeechReceiver> receivers = new HashSet<SpeechReceiver>();
		for (int i = 0; i < 10; i++) {
			final int number = i;
			SpeechReceiver receiver = new SpeechReceiver() {

				public void onReceive(List<String> results) {
					successfulReceivers.add(number);
				}
			};
			receivers.add(receiver);
			distSR.addReceiver(receiver);
		}

		assertTrue(successfulReceivers.size() == 0);

		sendMockResultsToDistReceiver();

		assertTrue(successfulReceivers.size() == 10);

		successfulReceivers.clear();

		for (SpeechReceiver receiver : receivers) {
			distSR.removeReceiver(receiver);
		}

		sendMockResultsToDistReceiver();

		assertTrue(successfulReceivers.size() == 0);

	}

	/**
	 * Send a mocked up result data intent to the DistributingSpeechReceiver
	 */
	private void sendMockResultsToDistReceiver() {
		ArrayList<String> resultMock = new ArrayList<String>();
		resultMock.add("One");
		resultMock.add("Two");
		resultMock.add("Three");

		Intent intent = new Intent(RoboBrainIntent.ACTION_SPEECH);
		intent.putExtra(RoboBrainIntent.EXTRA_SPEECH_RESULTS,
				resultMock.toArray(new String[resultMock.size()]));
		getContext().sendBroadcast(intent);

		Helper.sleepMillis(400);
	}

}
