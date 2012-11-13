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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.fpetersen.robobrain.ui.Console;

/**
 * Receives intents to display messages in the console activity of the app.
 * 
 * @author Frederik Petersen
 * 
 */
public class ConsoleReceiver extends BroadcastReceiver {

	/**
	 * Console Activity this Receiver receives intents for
	 */
	private Console mConsole;

	public ConsoleReceiver(Console console) {
		this.mConsole = console;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(RoboBrainIntent.ACTION_OUTPUT)) {
			mConsole.appendText(intent.getStringExtra(RoboBrainIntent.EXTRA_OUTPUT));
		}

	}

}
