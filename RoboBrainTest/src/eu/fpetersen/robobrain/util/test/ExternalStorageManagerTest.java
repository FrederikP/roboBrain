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

import java.io.File;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.util.ExternalStorageManager;

/**
 * Tests the {@link ExternalStorageManager}
 * 
 * @author Frederik Petersen
 * 
 */
public class ExternalStorageManagerTest extends AndroidTestCase {
	private ExternalStorageManager externalStorageManager;

	@Override
	protected void setUp() throws Exception {
		externalStorageManager = new ExternalStorageManager(getContext());
		super.setUp();
	}

	/**
	 * Tests if robot xml dir exists (or can be created) and can be referenced
	 */
	public void testGetRobotXmlDir() {
		File file = externalStorageManager.getRobotsXmlDir();
		checkIfExists(file);
	}

	/**
	 * Tests if behaviormapping.xml exists (or can be created) and can be
	 * referenced
	 */
	public void testGetBehaviorMapping() {
		File file = externalStorageManager.getBehaviorMappingFile("behaviormapping.xml");
		checkIfExists(file);
	}

	/**
	 * Tests if robot root dir exists (or can be created) and can be referenced
	 */
	public void testRobotRootDirMapping() {
		File file = externalStorageManager.getRoboBrainRoot();
		checkIfExists(file);
	}

	public void checkIfExists(File file) {
		assertNotNull(file);
		assertTrue(file.exists());
	}

	public void testCreateDirNotPossible() {
		File file = new File("bla" + File.separator + "blu" + File.separator + "blo");
		assertNotNull(file);
		assertFalse(file.exists());
		externalStorageManager.createDirIfNotExistant(file);
		assertFalse(file.exists());
	}

	public void testGetBehaviorMappingNotPossible() {
		String name = "bla" + File.separator + "blu" + File.separator + "blo" + File.separator
				+ "mapping.xml";
		File file = new File(name);
		assertNotNull(file);
		assertFalse(file.exists());
		externalStorageManager.getBehaviorMappingFile(name);
		assertFalse(file.exists());

	}

	public void testGetNewBehaviorMapping() {
		String name = "mapping.xml";
		File file = externalStorageManager.getBehaviorMappingFile(name);
		assertTrue(file.exists());
		file.delete();

	}

	@Override
	protected void tearDown() throws Exception {
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
