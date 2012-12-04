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
package eu.fpetersen.robobrain.util.test;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.util.SleepHelper;

/**
 * @author Frederik Petersen
 * 
 */
public class SleepHelperTest extends AndroidTestCase {

	private SleepHelper sleepHelper;

	@Override
	protected void setUp() throws Exception {
		sleepHelper = new SleepHelper(getContext());
		super.setUp();
	}

	public void testSleeping() {
		long millis = System.currentTimeMillis();
		sleepHelper.sleepMillis(200);
		assertTrue(System.currentTimeMillis() - 200 >= millis);
	}

	public void testInterruptingSleep() {
		Runnable sleepTask = new Runnable() {

			public void run() {
				sleepHelper.sleepMillis(10000);
			}
		};
		final Thread sleepThread = new Thread(sleepTask);
		sleepThread.start();

		sleepThread.interrupt();
		Helper.sleepMillis(5000);
		assertFalse(sleepThread.isAlive());

	}

}
