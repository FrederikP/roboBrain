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

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.robot.parts.Motor.MotorState;

/**
 * Tests enum {@link MotorState}
 * 
 * @author Frederik Petersen
 * 
 */
public class MotorStateTest extends AndroidTestCase {

	/**
	 * 
	 */
	public void testValuesMethod() {
		MotorState[] values = MotorState.values();
		assertNotNull(values);
		assertTrue(values.length > 0);
	}

	public void testValueOf() {
		MotorState state = MotorState.STOPPED;
		assertEquals(state, MotorState.valueOf(state.name()));
	}

}
