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
package eu.fpetersen.robobrain.behavior.switching.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.behavior.BackAndForthBehavior;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.behavior.ObstAvoidanceBehavior;
import eu.fpetersen.robobrain.behavior.switching.BehaviorSwitcher;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.speech.SpeechResultManager;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.util.Helper;

/**
 * Tests the {@link BehaviorSwitcher} class
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorSwitcherTest extends AndroidTestCase {

	private BehaviorSwitcher mSwitcher;
	private MockRobotService mService;

	@Override
	protected void setUp() throws Exception {
		mService = new MockRobotService(getContext());
		mSwitcher = new BehaviorSwitcher(mService);
		super.setUp();
	}

	public void testStartingAndStoppingBehavior() {
		MockRobotFactory fac = new MockRobotFactory(mService);
		Robot robot = fac.createSimpleRobot("TESTBOT");
		List<Behavior> behaviors = new ArrayList<Behavior>();

		Behavior simpleBehavior = new BackAndForthBehavior();
		BehaviorInitializer simpleBehaviorInitializer = new BehaviorInitializer(
				"BackAndForthBehavior", "Simple");
		simpleBehaviorInitializer.initialize(simpleBehavior, robot);

		behaviors.add(simpleBehavior);

		mService.addCC(robot, behaviors);

		Behavior behavior = mService.getBehaviorForUUID(simpleBehavior.getId());
		assertNotNull(behavior);
		assertEquals(simpleBehavior, behavior);

		mSwitcher.startBehavior(simpleBehavior.getId());

		CommandCenter cc = mService.getCCForAddress(robot.getAddress());
		assertNotNull(cc);

		double waitedSecs = 0;
		while (!simpleBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		assertTrue(simpleBehavior.isTurnedOn());

		assertEquals(cc.getRunningBehavior(), simpleBehavior);

		mSwitcher.stopBehavior(simpleBehavior.getId());

		waitedSecs = 0;
		while (simpleBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertFalse(simpleBehavior.isTurnedOn());
		assertNull(cc.getRunningBehavior());

	}

	public void testStartingBehaviorWhenOtherIsRunning() {
		MockRobotFactory fac = new MockRobotFactory(mService);
		Robot robot = fac.createSimpleRobot("TESTBOT");
		List<Behavior> behaviors = new ArrayList<Behavior>();

		Behavior simpleBehavior = new BackAndForthBehavior();
		BehaviorInitializer simpleBehaviorInitializer = new BehaviorInitializer(
				"BackAndForthBehavior", "Simple");
		simpleBehaviorInitializer.initialize(simpleBehavior, robot);

		behaviors.add(simpleBehavior);

		Behavior obstBehavior = new ObstAvoidanceBehavior();
		BehaviorInitializer obstBehaviorInitializer = new BehaviorInitializer("ObstacleBehavior",
				"Obstacle");
		obstBehaviorInitializer.initialize(obstBehavior, robot);

		behaviors.add(obstBehavior);

		mService.addCC(robot, behaviors);

		// Start one service

		Behavior behavior = mService.getBehaviorForUUID(simpleBehavior.getId());
		assertNotNull(behavior);
		assertEquals(simpleBehavior, behavior);

		mSwitcher.startBehavior(simpleBehavior.getId());

		CommandCenter cc = mService.getCCForAddress(robot.getAddress());
		assertNotNull(cc);
		double waitedSecs = 0;
		while (!simpleBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertTrue(simpleBehavior.isTurnedOn());

		assertEquals(cc.getRunningBehavior(), simpleBehavior);

		// Start other service
		behavior = mService.getBehaviorForUUID(obstBehavior.getId());
		assertNotNull(behavior);
		assertEquals(obstBehavior, behavior);

		mSwitcher.startBehavior(obstBehavior.getId());
		waitedSecs = 0;
		while (!obstBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertNotNull(cc);
		assertTrue(obstBehavior.isTurnedOn());

		assertEquals(cc.getRunningBehavior(), obstBehavior);

		// Stop that service

		mSwitcher.stopBehavior(obstBehavior.getId());

		waitedSecs = 0;
		while (obstBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertFalse(obstBehavior.isTurnedOn());
		assertFalse(simpleBehavior.isTurnedOn());
		assertNull(cc.getRunningBehavior());

	}

	public void testTurningOffAllBehavior() {
		MockRobotFactory fac = new MockRobotFactory(mService);
		Robot robot = fac.createSimpleRobot("TESTBOT");
		List<Behavior> behaviors = new ArrayList<Behavior>();

		Behavior simpleBehavior = new BackAndForthBehavior();
		BehaviorInitializer simpleBehaviorInitializer = new BehaviorInitializer(
				"BackAndForthBehavior", "Simple");
		simpleBehaviorInitializer.initialize(simpleBehavior, robot);

		behaviors.add(simpleBehavior);

		mService.addCC(robot, behaviors);

		Behavior behavior = mService.getBehaviorForUUID(simpleBehavior.getId());
		assertNotNull(behavior);
		assertEquals(simpleBehavior, behavior);

		mSwitcher.startBehavior(simpleBehavior.getId());

		CommandCenter cc = mService.getCCForAddress(robot.getAddress());
		assertNotNull(cc);
		double waitedSecs = 0;
		while (!simpleBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertTrue(simpleBehavior.isTurnedOn());

		assertEquals(cc.getRunningBehavior(), simpleBehavior);

		mSwitcher.stopAllBehaviors();
		waitedSecs = 0;
		while (simpleBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertFalse(simpleBehavior.isTurnedOn());
		assertNull(cc.getRunningBehavior());

	}

	/**
	 * Test speechCommanding Behavior, some without speech attribute
	 * 
	 */
	public void testSpeechCommand() {
		MockRobotFactory fac = new MockRobotFactory(mService);
		Robot robot = fac.createSimpleRobot("TESTBOT");
		List<Behavior> behaviors = new ArrayList<Behavior>();

		Behavior simpleBehavior = new BackAndForthBehavior();
		BehaviorInitializer simpleBehaviorInitializer = new BehaviorInitializer(
				"BackAndForthBehavior", "");
		simpleBehaviorInitializer.initialize(simpleBehavior, robot);

		behaviors.add(simpleBehavior);

		Behavior obstBehavior = new ObstAvoidanceBehavior();
		BehaviorInitializer obstBehaviorInitializer = new BehaviorInitializer("ObstacleBehavior",
				"obstacle");
		obstBehaviorInitializer.initialize(obstBehavior, robot);

		behaviors.add(obstBehavior);

		mService.addCC(robot, behaviors);

		Behavior behavior = mService.getBehaviorForUUID(simpleBehavior.getId());
		assertNotNull(behavior);
		assertEquals(simpleBehavior, behavior);

		List<String> mockedSpeechResults = new ArrayList<String>();
		mockedSpeechResults.add("bla bli blu");
		mockedSpeechResults.add("start notexistant behavior");
		mockedSpeechResults.add("start obstacle behavior");
		SpeechResultManager.getInstance().allocateNewResults(getContext(), mockedSpeechResults);

		CommandCenter cc = mService.getCCForAddress(robot.getAddress());
		assertNotNull(cc);

		double waitedSecs = 0;
		while (!obstBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertTrue(obstBehavior.isTurnedOn());

		assertEquals(cc.getRunningBehavior(), obstBehavior);

		mockedSpeechResults = new ArrayList<String>();
		mockedSpeechResults.add("bla bli blu");
		mockedSpeechResults.add("start notexistant behavior");
		mockedSpeechResults.add("stop all behavior");
		SpeechResultManager.getInstance().allocateNewResults(getContext(), mockedSpeechResults);
		waitedSecs = 0;
		while (!obstBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}

		assertFalse(obstBehavior.isTurnedOn());
		assertNull(cc.getRunningBehavior());

	}

	/**
	 * Makes sure that nothing happens when non registered behavior ids are sent
	 * to {@link BehaviorSwitcher}
	 */
	public void testStartStopNonRegisteredBehavior() {
		MockRobotFactory fac = new MockRobotFactory(mService);
		Robot robot = fac.createSimpleRobot("TESTBOT");
		List<Behavior> noBehaviors = new ArrayList<Behavior>();

		Behavior simpleBehavior = new BackAndForthBehavior();
		BehaviorInitializer simpleBehaviorInitializer = new BehaviorInitializer(
				"BackAndForthBehavior", "");
		simpleBehaviorInitializer.initialize(simpleBehavior, robot);

		mService.addCC(robot, noBehaviors);

		mSwitcher.startBehavior(simpleBehavior.getId());
		double waitedSecs = 0;
		while (!simpleBehavior.isTurnedOn() && waitedSecs < 20) {
			Helper.sleepMillis(100);
			waitedSecs = waitedSecs + 0.1;
		}
		assertFalse(simpleBehavior.isTurnedOn());

		mSwitcher.stopBehavior(simpleBehavior.getId());

	}

	@Override
	protected void tearDown() throws Exception {
		mSwitcher.destroy();
		mService.destroy();
		super.tearDown();
	}
}
