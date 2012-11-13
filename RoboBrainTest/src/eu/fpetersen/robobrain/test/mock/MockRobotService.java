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
package eu.fpetersen.robobrain.test.mock;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.behavior.BackAndForthBehavior;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.CommandCenter;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;

/**
 * Simple mock for the {@link RobotService} class, so that tests for activities
 * don't have to depend on the Service to work.
 * 
 * @author Frederik Petersen
 * 
 */
public class MockRobotService extends RobotService {

	private Context testContext;

	public MockRobotService(Context context) {
		testContext = context;
	}

	@Override
	public void sendBroadcast(Intent intent) {
		testContext.sendBroadcast(intent);
	}

	@Override
	public void sendBroadcast(Intent intent, String receiverPermission) {
		testContext.sendBroadcast(intent, receiverPermission);
	}

	/**
	 * To make the status be set from testing classes.
	 * 
	 * @param running
	 *            True if service is supposed to be running, false if otherwise
	 */
	public void setRunning(boolean running) {
		this.mRunning = running;
	}

	@Override
	public Collection<CommandCenter> getAllCCs() {
		Collection<CommandCenter> ccs = new ArrayList<CommandCenter>();
		Robot robot = RobotFactory.getInstance(MockRobotService.this).createSimpleRobot("TestBot");
		Behavior b1 = new BackAndForthBehavior();
		b1.initialize(robot, "BackAndForth", "TestSpeechName");
		ArrayList<Behavior> behaviors = new ArrayList<Behavior>();
		behaviors.add(b1);
		ccs.add(new CommandCenter(robot, behaviors));
		return ccs;
	}
}
