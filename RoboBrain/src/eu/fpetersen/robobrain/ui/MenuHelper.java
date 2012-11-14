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
package eu.fpetersen.robobrain.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import eu.fpetersen.robobrain.R;

/**
 * Bundles some menu specific methods. Specificly adding listeners to menu
 * items.
 * 
 * @author Frederik Petersen
 * 
 */
public class MenuHelper {

	/**
	 * Add a Listener that allows switching to console activity
	 * 
	 * @param menu
	 *            to add Listener to
	 * @param activity
	 *            calling this Helper method
	 */
	public static void addConsoleMenuClickListener(Menu menu, final Activity activity) {
		MenuItem console = menu.findItem(R.id.console_menu_item);
		final Intent cIntent = new Intent(activity, Console.class);
		cIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		console.setIntent(cIntent);
		console.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				activity.startActivity(cIntent);
				return true;
			}
		});
	}

	/**
	 * Add a Listener that allows switching to starter activity
	 * 
	 * @param menu
	 *            to add Listener to
	 * @param activity
	 *            calling this Helper method
	 */
	public static void addStarterMenuClickListener(Menu menu, final Activity activity) {
		MenuItem starter = menu.findItem(R.id.starter_menu_item);
		final Intent cIntent = new Intent(activity, Starter.class);
		cIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		starter.setIntent(cIntent);
		starter.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				activity.startActivity(cIntent);
				return true;
			}
		});
	}

	/**
	 * Add a Listener that allows switching to about activity
	 * 
	 * @param menu
	 *            to add Listener to
	 * @param activity
	 *            calling this Helper method
	 */
	public static void addAboutMenuClickListener(Menu menu, final Activity activity) {
		MenuItem starter = menu.findItem(R.id.about_menu_item);
		final Intent cIntent = new Intent(activity, About.class);
		cIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		starter.setIntent(cIntent);
		starter.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				activity.startActivity(cIntent);
				return true;
			}
		});
	}

}
