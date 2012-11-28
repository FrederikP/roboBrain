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

import java.util.List;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.communication.RoboBrainIntent;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.services.RobotServiceContainer;
import eu.fpetersen.robobrain.speech.DistributingSpeechReceiver;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * Simple mock for the {@link RobotService} class, so that tests for activities
 * don't have to depend on the Service to work.
 * 
 * @author Frederik Petersen
 * 
 */
public class MockRobotService extends RobotService {

	private Context mTestContext;

	private DistributingSpeechReceiver mDistSpeechRec;

	private UUID id;

	private boolean mIsDestroyed = false;

	public MockRobotService(Context context) {
		mLog = new RoboLog("MockRobotService", MockRobotService.this);
		mTestContext = context;
		mDistSpeechRec = new DistributingSpeechReceiver();
		registerReceiver(mDistSpeechRec, new IntentFilter(RoboBrainIntent.ACTION_SPEECH));
		id = RobotServiceContainer.getInstance().addRobotService(MockRobotService.this);
	}

	@Override
	public void sendBroadcast(Intent intent) {
		mTestContext.sendBroadcast(intent);
	}

	@Override
	public void sendBroadcast(Intent intent, String receiverPermission) {
		mTestContext.sendBroadcast(intent, receiverPermission);
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

	public void addCC(Robot robot, List<Behavior> behaviors) {
		createCommandCenter(robot, behaviors);
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		return mTestContext.registerReceiver(receiver, filter);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		mTestContext.unregisterReceiver(receiver);
	}

	@Override
	public DistributingSpeechReceiver getDistributingSpeechReceiver() {
		return mDistSpeechRec;
	}

	@Override
	public Resources getResources() {
		return mTestContext.getResources();
	}

	/**
	 * Call this to broadcast intent receivable by STarter activity to update
	 * it's RobotService reference and Update UI
	 */
	public void broadcastUIUpdateIntent(boolean setToNull) {
		Intent intent = new Intent(RoboBrainIntent.ACTION_STARTERUIUPDATE);
		if (!setToNull) {
			intent.putExtra(RoboBrainIntent.EXTRA_SERVICEID, id);
		} else {
			RobotServiceContainer.getInstance().removeRobotService(id);
		}
		sendBroadcast(intent);
	}

	public void destroy() {
		if (!mIsDestroyed) {
			mIsDestroyed = true;
			unregisterReceiver(mDistSpeechRec);
		}
	}
}
