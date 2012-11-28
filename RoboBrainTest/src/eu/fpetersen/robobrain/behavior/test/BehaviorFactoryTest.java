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
import eu.fpetersen.robobrain.behavior.BackAndForthBehavior;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorFactory;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.test.mock.AbstractBehavior;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.mock.PrivateConstructorBehavior;

/**
 * Tests the {@link BehaviorFactory} class.
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorFactoryTest extends AndroidTestCase {

	private MockRobotService mService;

	/**
	 * Tests Behavior Creation by trying to instantiate the
	 * {@link BackAndForthBehavior}
	 */
	public void testBehaviorCreation() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot robot = factory.createSimpleRobot("TESTBOT");
		BehaviorFactory bFac = BehaviorFactory.getInstance(mService);
		Behavior behavior = bFac.createBehavior(
				new BehaviorInitializer(BackAndForthBehavior.class.getName(), "speech"), robot);
		assertNotNull(behavior);
		assertTrue(behavior instanceof BackAndForthBehavior);
		assertNotNull(behavior.getId());
		assertEquals(robot, behavior.getRobot());
		assertFalse(behavior.isTurnedOn());
	}

	/**
	 * Tests negative Behavior Creation by trying to instantiate behaviors that
	 * do not exist / have a private constructor / are abstract
	 * {@link BackAndForthBehavior}
	 */
	public void testNegativeBehaviorCreation() {
		mService = new MockRobotService(getContext());
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot robot = factory.createSimpleRobot("TESTBOT");
		BehaviorFactory bFac = BehaviorFactory.getInstance(mService);

		// Non existant class
		Behavior behavior = bFac.createBehavior(new BehaviorInitializer("WorldDominationBehavior",
				"worlddomination"), robot);
		assertNull(behavior);

		// Abstract class
		behavior = bFac.createBehavior(new BehaviorInitializer(AbstractBehavior.class.getName(),
				"abstract"), robot);
		assertNull(behavior);

		// Private constructor class
		behavior = bFac.createBehavior(
				new BehaviorInitializer(PrivateConstructorBehavior.class.getName(), "private"),
				robot);
		assertNull(behavior);

	}

	@Override
	protected void tearDown() throws Exception {
		if (mService != null) {
			mService.destroy();
		}
		// ///CLOVER:FLUSH
		super.tearDown();
	}
}
