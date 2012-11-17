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
import eu.fpetersen.robobrain.util.AppRequirementsChecker;
import eu.fpetersen.robobrain.util.exceptions.AppRequirementNotMetException;

/**
 * @author Frederik Petersen
 * 
 */
public class AppRequirementsCheckerTest extends AndroidTestCase {

	/**
	 * Emulate old Amarino Version
	 */
	public void testAmarinoOldWarning() {
		MockRoboBrainPackageManager manager = new MockRoboBrainPackageManager();
		PackageInfo amarinoOld = new PackageInfo();
		amarinoOld.packageName = "at.abraxas.amarino";
		amarinoOld.versionCode = 10;
		amarinoOld.versionName = "30";

		manager.addPackageInfo(amarinoOld);

		boolean warned = false;

		try {
			AppRequirementsChecker.checkForAmarino(manager);
		} catch (AppRequirementNotMetException e) {
			assertTrue(e.getMessage().contains("only tested with"));
			warned = true;
			e.showAlert(getContext());
		}

		assertTrue(warned);
	}

	/**
	 * Emulate no Amarino
	 */
	public void testNoAmarinoError() {
		MockRoboBrainPackageManager manager = new MockRoboBrainPackageManager();
		PackageInfo amarinoOld = new PackageInfo();
		amarinoOld.packageName = "hullahu.hulla.hu";

		manager.addPackageInfo(amarinoOld);

		boolean errord = false;

		try {
			AppRequirementsChecker.checkForAmarino(manager);
		} catch (AppRequirementNotMetException e) {
			assertTrue(e.getMessage().contains("not installed"));
			errord = true;
			e.showAlert(getContext());
		}

		assertTrue(errord);
	}
}