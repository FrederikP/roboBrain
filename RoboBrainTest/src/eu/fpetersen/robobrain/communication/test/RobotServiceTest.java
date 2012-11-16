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
package eu.fpetersen.robobrain.communication.test;

import android.content.Intent;
import android.test.ServiceTestCase;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests the RobotService, isolated from activities, etc.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotServiceTest extends ServiceTestCase<RobotService> {

	/**
	 * @param serviceClass
	 *            RobotService Class - The class we wish to test
	 */
	public RobotServiceTest() {
		super(RobotService.class);
	}

	/**
	 * Test starting the Robot Service per Intent
	 */
	public void testStartingRobotService() {
		startService(new Intent(getContext(), RobotService.class));

		int countSecs = 0;
		RobotService service = getService();
		while (service == null && countSecs < 5) {
			Helper.sleepMillis(1000);
			countSecs++;
			service = getService();
		}

		assertNotNull(service);

		// Check if onCreate() was called
		assertNotNull(service.getDistributingSpeechReceiver());

		countSecs = 0;
		while (!service.isRunning() && countSecs < 20) {
			Helper.sleepMillis(1000);
			countSecs++;
		}

		assertTrue(service.isRunning());

		shutdownService();

		countSecs = 0;
		while (service.isRunning() && countSecs < 5) {
			Helper.sleepMillis(1000);
			countSecs++;
		}
		assertFalse(service.isRunning());

	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
