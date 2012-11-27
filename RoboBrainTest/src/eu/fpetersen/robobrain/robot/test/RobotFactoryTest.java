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
package eu.fpetersen.robobrain.robot.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import android.os.Environment;
import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotFactory;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * Tests {@link RobotFactory}. Also tests {@link RobotPartFactory} as
 * RobotFactory depends on it. Don't see the need to mock the RobotPartFactory
 * here.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotFactoryTest extends AndroidTestCase {

	private MockRobotService mService;

	/**
	 * Creates really simple robot, no parts, no file This tests a class in the
	 * Test Project. Just to make sure, nothing goes wrong there
	 */
	public void testSimpleRobotCreation() {
		String robotName = "TestBot";
		MockRobotFactory factory = new MockRobotFactory(mService);
		Robot robot = factory.createSimpleRobot("TestBot");
		assertNotNull(robot);
		assertEquals(robot.getName(), robotName);
	}

	/**
	 * Creates robot from raw resource xml file used in integration tests
	 */
	public void testXmlRobotCreation() {
		InputStream robotXml = getContext().getResources().openRawResource(R.raw.testbot);
		mService = new MockRobotService(getContext());
		Robot robot = RobotFactory.getInstance(mService).createRobotFromXml(robotXml);
		assertNotNull(robot);
		assertEquals("TESTBOT", robot.getName());
		assertNotNull(robot.getMainMotor());
		assertNotNull(robot.getBackSensor());
		assertNotNull(robot.getFrontSensor());
		assertNotNull(robot.getHeadColorLed());
		assertNotNull(robot.getHeadServo());
	}

	/**
	 * Creates robot from raw resource xml file used in integration tests
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	public void testXmlRobotCreationFromFolder() {
		InputStream testBot1 = null;
		FileOutputStream outStream = null;
		File bot1 = null;
		File testDir = null;
		try {
			testDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator + mContext.getString(R.string.sd_robobrain_root_dir)
					+ File.separator + "testbots");
			testDir.mkdir();
			assertTrue(testDir.exists());
			assertTrue(testDir.isDirectory());
			bot1 = new File(testDir.getAbsolutePath(), "testbot1.xml");
			bot1.createNewFile();
			testBot1 = mContext.getResources().openRawResource(R.raw.testbot);
			outStream = new FileOutputStream(bot1);
			int c;
			while ((c = testBot1.read()) != -1) {
				outStream.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(1 == 0);
		} finally {

			try {
				if (testBot1 != null) {
					testBot1.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				assertTrue(1 == 0);
			}

		}

		mService = new MockRobotService(getContext());
		Map<String, Robot> robots = RobotFactory.getInstance(mService).createRobots(testDir);
		assertNotNull(robots);
		Robot robot = robots.get("TESTBOT");
		assertEquals("TESTBOT", robot.getName());
		assertNotNull(robot.getMainMotor());
		assertNotNull(robot.getBackSensor());
		assertNotNull(robot.getFrontSensor());
		assertNotNull(robot.getHeadColorLed());
		assertNotNull(robot.getHeadServo());

		bot1.delete();
		testDir.delete();

	}

	/**
	 * Tests how the factory method reacts when handing in a NullPointer
	 */
	public void testNullPointerRobotCreation() {
		InputStream robotXml = null;
		mService = new MockRobotService(getContext());
		Robot robot = RobotFactory.getInstance(mService).createRobotFromXml(robotXml);
		assertNull(robot);
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
