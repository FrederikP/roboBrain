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

public class DistributingSpeechReceiverTest extends AndroidTestCase {

	DistributingSpeechReceiver distSR;

	@Override
	protected void setUp() throws Exception {
		distSR = new DistributingSpeechReceiver();
		getContext().registerReceiver(distSR,
				new IntentFilter(RoboBrainIntent.ACTION_SPEECH));
		super.setUp();
	}

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
