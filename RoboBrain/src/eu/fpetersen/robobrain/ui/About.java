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
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.util.VersionHelper;

/**
 * Shows general information about the app and allows switching to other
 * activities per menu.
 * 
 * @author Frederik Petersen
 * 
 */
public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Set website link clickable
		TextView websiteView = (TextView) findViewById(R.id.websiteText);
		websiteView.setText(Html
				.fromHtml("<a href=\"http://robobrain.fpetersen.eu/\">roboBrain.fpetersen.eu</a>"));
		websiteView.setMovementMethod(LinkMovementMethod.getInstance());

		// Set version
		String version = VersionHelper.getVersion(getPackageManager(), About.this);
		TextView versionView = (TextView) findViewById(R.id.versionText);
		versionView.setText(version);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_about, menu);

		MenuHelper.addConsoleMenuClickListener(menu, About.this);
		MenuHelper.addStarterMenuClickListener(menu, About.this);
		return super.onCreateOptionsMenu(menu);
	}

}
