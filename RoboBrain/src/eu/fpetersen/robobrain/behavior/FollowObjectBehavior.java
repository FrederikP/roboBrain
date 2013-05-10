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
package eu.fpetersen.robobrain.behavior;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import eu.fpetersen.robobrain.behavior.followobject.FollowObjectIntent;
import eu.fpetersen.robobrain.behavior.followobject.FollowObjectReceiver;
import eu.fpetersen.robobrain.requirements.Requirements;
import eu.fpetersen.robobrain.robot.parts.Motor;
import eu.fpetersen.robobrain.ui.CameraViewActivity;

/**
 * @author Frederik Petersen
 * 
 */
public class FollowObjectBehavior extends Behavior {

	FollowObjectReceiver receiver;

	@Override
	protected void behaviorLoop() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStop() {
		Context context = getRobot().getRobotService();
		context.unregisterReceiver(receiver);

	}

	@Override
	protected void onStart() {
		Context context = getRobot().getRobotService();
		Intent startIntent = new Intent(context, CameraViewActivity.class);
		startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startIntent);

		receiver = new FollowObjectReceiver(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FollowObjectIntent.ACTION_BACKWARD);
		intentFilter.addAction(FollowObjectIntent.ACTION_FORWARD);
		intentFilter.addAction(FollowObjectIntent.ACTION_LEFT);
		intentFilter.addAction(FollowObjectIntent.ACTION_RIGHT);
		intentFilter.addAction(FollowObjectIntent.ACTION_QUIT);
		context.registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void fillRequirements(Requirements requirements) {
		requirements.addPart("main_motor", Motor.class.getName());
	}

}
