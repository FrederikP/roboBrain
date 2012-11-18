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

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.behavior.DanceBehavior;
import eu.fpetersen.robobrain.color.RgbColor;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests {@link DanceBehavior}. Since there are a lot of random events, the
 * behavior is not tested very thoroughly.
 * 
 * @author Frederik Petersen
 * 
 */
public class DanceBehaviorTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		System.setProperty(getContext().getString(R.string.envvar_testing), "true");
		super.setUp();
	}

	/**
	 * Tries to test and cover as much of the {@link DanceBehavior} as possible
	 */
	public void testRunningDanceBehavior() {
		MockRobotFactory fact = new MockRobotFactory(getContext());
		Robot robot = fact.createSimpleRobot("TestBot");
		Behavior danceB = new DanceBehavior();
		BehaviorInitializer initializer = new BehaviorInitializer("DanceBehavior", "Dance");
		initializer.initialize(danceB, robot);
		assertNotNull(danceB.getId());

		// Turn on
		danceB.startBehavior();
		Helper.sleepMillis(200);
		assertTrue(danceB.isTurnedOn());

		// Set some values, just to make sure there is gonna be no error
		// Also wait for a while, just to see what happens
		Helper.sleepMillis(2000);
		robot.getFrontSensor().setValue(10);
		Helper.sleepMillis(100);
		robot.getFrontSensor().setValue(10);
		robot.getBackSensor().setValue(0);
		Helper.sleepMillis(100);

		RgbColor color = robot.getHeadColorLed().getColor();
		assertNotNull(color);

		// Turn off
		danceB.stopBehavior();
		Helper.sleepMillis(200);
		assertFalse(danceB.isTurnedOn());

	}

}
