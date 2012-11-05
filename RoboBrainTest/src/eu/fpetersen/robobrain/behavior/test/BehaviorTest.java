package eu.fpetersen.robobrain.behavior.test;

import java.util.HashMap;
import java.util.Map;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.test.util.Helper;

public class BehaviorTest extends AndroidTestCase {

	public void testBasicBehavior() {
		final String started = "started";

		final Map<String, Boolean> testMap = new HashMap<String, Boolean>();
		testMap.put(started, false);
		final Behavior behavior = new Behavior() {

			boolean calledOnce = false;

			@Override
			protected void onStop() {
				testMap.put(started, false);
				calledOnce = false;
			}

			@Override
			protected void behaviorLoop() {

				if (!calledOnce) {
					testMap.put(started, true);
					calledOnce = true;
				}
			}
		};

		Runnable runThis = new Runnable() {

			public void run() {
				behavior.startBehavior();
			}
		};

		Thread thread = new Thread(runThis);
		thread.start();
		Helper.sleepMillis(200);

		assertTrue(testMap.get(started));

		runThis = new Runnable() {

			public void run() {
				behavior.stopBehavior();
			}
		};

		thread = new Thread(runThis);
		thread.start();
		Helper.sleepMillis(200);

		assertFalse(testMap.get(started));

	}

}
