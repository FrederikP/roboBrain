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
package eu.fpetersen.robobrain.behavior.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.Behavior;
import eu.fpetersen.robobrain.behavior.BehaviorInitializer;
import eu.fpetersen.robobrain.behavior.DanceBehavior;
import eu.fpetersen.robobrain.color.RgbColor;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.util.ExternalStorageManager;

/**
 * Tests {@link DanceBehavior}. Since there are a lot of random events, the
 * behavior is not tested very thoroughly.
 * 
 * @author Frederik Petersen
 * 
 */
public class DanceBehaviorTest extends AndroidTestCase {

	private MockRobotService mService;

	@Override
	protected void setUp() throws Exception {
		System.setProperty(getContext().getString(R.string.envvar_testing), "true");
		mService = new MockRobotService(getContext());
		super.setUp();
	}

	/**
	 * Tries to test and cover as much of the {@link DanceBehavior} as possible
	 * 
	 * @throws IOException
	 */
	public void testRunningDanceBehavior() throws IOException {
		ExternalStorageManager exManager = new ExternalStorageManager(getContext());
		File musicDir = exManager.getMusicDir();
		File musicFile = new File(musicDir, "empty.mp3");
		musicFile.createNewFile();
		putExampleMp3ToFile(musicFile);

		MockRobotFactory fact = new MockRobotFactory(mService);
		Robot robot = fact.createSimpleRobot("TestBot");
		final Behavior danceB = new DanceBehavior();
		BehaviorInitializer initializer = new BehaviorInitializer("DanceBehavior", "Dance");
		initializer.initialize(danceB, robot);
		assertNotNull(danceB.getId());

		// Turn on
		Runnable behaviorTask = new Runnable() {

			public void run() {
				danceB.startBehavior();
			}
		};
		Thread behaviorThread = new Thread(behaviorTask);
		behaviorThread.start();

		Helper.sleepMillis(200);
		assertTrue(danceB.isTurnedOn());

		// Set some values, just to make sure there is gonna be no error
		// Also wait for a while, just to see what happens
		Helper.sleepMillis(3000);
		robot.getFrontSensor().setValue(10);
		Helper.sleepMillis(100);
		robot.getFrontSensor().setValue(10);
		robot.getBackSensor().setValue(0);
		Helper.sleepMillis(100);

		RgbColor color = robot.getHeadColorLed().getColor();
		assertNotNull(color);

		// Turn off
		danceB.stopBehavior();
		Helper.sleepMillis(200);
		assertFalse(danceB.isTurnedOn());

		musicFile.delete();

	}

	/**
	 * Puts an empty mp3 file to the desired location.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void putExampleMp3ToFile(File file) throws IOException {
		// create FileInputStream object for source file
		InputStream fin = getContext().getResources().openRawResource(R.raw.empty_sound);

		// create FileOutputStream object for destination file
		FileOutputStream fout = new FileOutputStream(file);

		byte[] b = new byte[1024];
		int noOfBytes = 0;

		// read bytes from source file and write to destination file
		while ((noOfBytes = fin.read(b)) != -1) {
			fout.write(b, 0, noOfBytes);
		}

		// close the streams
		fin.close();
		fout.close();
	}

	@Override
	protected void tearDown() throws Exception {
		if (mService != null) {
			mService.destroy();
		}
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
