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

import java.io.InputStream;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.robot.RobotFactory;
import eu.fpetersen.robobrain.robot.RobotPartFactory;
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

	/**
	 * Creates really simple robot, no parts, no file
	 */
	public void testSimpleRobotCreation() {
		String robotName = "TestBot";
		Robot robot = RobotFactory.getInstance(new MockRobotService(getContext()))
				.createSimpleRobot(robotName);
		assertNotNull(robot);
		assertEquals(robot.getName(), robotName);
	}

	/**
	 * Creates robot from raw resource xml file used in integration tests
	 */
	public void testXmlRobotCreation() {
		InputStream robotXml = getContext().getResources().openRawResource(R.raw.testbot);
		Robot robot = RobotFactory.getInstance(new MockRobotService(getContext()))
				.createRobotFromXml(robotXml);
		assertNotNull(robot);
		assertEquals("TESTBOT", robot.getName());
		assertNotNull(robot.getMainMotor());
		assertNotNull(robot.getBackSensor());
		assertNotNull(robot.getFrontSensor());
		assertNotNull(robot.getHeadColorLed());
		assertNotNull(robot.getHeadServo());
	}

}
