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
package eu.fpetersen.robobrain.ui.test;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.ui.About;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * tests the {@link About} activity
 * 
 * @author Frederik Petersen
 * 
 */
public class AboutTest extends ActivityInstrumentationTestCase2<About> {

	About mAboutActivity;

	public AboutTest() {
		super(About.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mAboutActivity = getActivity();
	}

	/**
	 * Test if version number is correctly displayed
	 */
	public void testVersionCode() {
		// Set version
		PackageInfo pInfo = null;
		String version = "";
		try {
			pInfo = mAboutActivity.getPackageManager().getPackageInfo(
					mAboutActivity.getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			RoboLog.alertError(mAboutActivity, "Version could not be set");
		}

		assertFalse(version.matches(""));

		TextView versionView = (TextView) mAboutActivity.findViewById(R.id.versionText);
		assertNotNull(versionView);
		assertEquals(version, versionView.getText());
	}

}
