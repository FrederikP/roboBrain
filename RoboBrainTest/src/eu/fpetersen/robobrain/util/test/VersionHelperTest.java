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
package eu.fpetersen.robobrain.util.test;

import android.content.pm.PackageInfo;
import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.test.mock.MockRoboBrainPackageManager;
import eu.fpetersen.robobrain.util.VersionHelper;

/**
 * Tests the util class {@link VersionHelper}.
 * 
 * @author Frederik Petersen
 * 
 */
public class VersionHelperTest extends AndroidTestCase {

	public void testGetVersionPositive() {
		String versionName = "1.2";
		MockRoboBrainPackageManager packageManager = new MockRoboBrainPackageManager();
		PackageInfo roboBrainInfo = new PackageInfo();
		roboBrainInfo.packageName = "eu.fpetersen.robobrain";
		roboBrainInfo.versionCode = 3;
		roboBrainInfo.versionName = versionName;
		packageManager.addPackageInfo(roboBrainInfo);

		String version = VersionHelper.getVersion(packageManager, getContext());
		assertNotNull(version);
		assertEquals(versionName, version);
	}

	public void testGetVersionNegative() {
		String versionName = "1.2";
		MockRoboBrainPackageManager packageManager = new MockRoboBrainPackageManager();
		PackageInfo roboBrainInfo = new PackageInfo();
		roboBrainInfo.packageName = "eu.fpetersen.robobrainzzz";
		roboBrainInfo.versionCode = 3;
		roboBrainInfo.versionName = versionName;
		packageManager.addPackageInfo(roboBrainInfo);

		String version = VersionHelper.getVersion(packageManager, getContext());
		assertNotNull(version);
		assertEquals("", version);
	}

}
