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