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
		RobotService service = startService();

		assertNotNull(service);

		// Check if onCreate() was called
		assertNotNull(service.getDistributingSpeechReceiver());

		double countSecs = 0;
		while (!service.isRunning() && countSecs < 20) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}

		assertTrue(service.isRunning());

		shutdownService();

		countSecs = 0;
		while (service.isRunning() && countSecs < 5) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}
		assertFalse(service.isRunning());

	}

	private RobotService startService() {
		startService(new Intent(getContext(), RobotService.class));

		double countSecs = 0;
		RobotService service = getService();
		while (service == null && countSecs < 5) {
			Helper.sleepMillis(000);
			countSecs = countSecs + 0.1;
			service = getService();
		}
		return service;
	}

	public void testIfOnBindReturnsNull() {
		RobotService service = startService();
		assertNull(service.onBind(new Intent()));
		stopService(service);
	}

	public void testStoppingService() {
		RobotService service = startService();
		service.stopService(new Intent(getContext(), RobotService.class));
		stopService(service);
	}

	private void stopService(RobotService service) {
		double countSecs = 0;
		while (service.isRunning() && countSecs < 10) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}
		assertFalse(service.isRunning());
	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
