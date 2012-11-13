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
package eu.fpetersen.robobrain.communication;

import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.behavior.Behavior;

/**
 * 
 * Receives behavior trigger intents and starts/stops the behavior. This enables
 * the RobotService to receive behavior triggers from other components i.e. the
 * UI.
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorReceiver extends BroadcastReceiver {

	private RobotService mService;

	public BehaviorReceiver(RobotService service) {
		this.mService = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().matches(RoboBrainIntent.ACTION_BEHAVIORTRIGGER)) {
			boolean startIt = intent.getBooleanExtra(RoboBrainIntent.EXTRA_BEHAVIORSTATE, false);
			UUID uuid = (UUID) intent.getSerializableExtra(RoboBrainIntent.EXTRA_BEHAVIORUUID);
			final Behavior b = mService.getBehaviorForUUID(uuid);
			if (startIt) {
				Runnable behaviorStarter = new Runnable() {
					public void run() {
						b.startBehavior();
					}
				};
				Thread thread = new Thread(behaviorStarter);
				thread.start();
			} else {
				Runnable behaviorStopper = new Runnable() {
					public void run() {
						b.stopBehavior();
					}
				};
				Thread thread = new Thread(behaviorStopper);
				thread.start();
			}

		}
	}

}
