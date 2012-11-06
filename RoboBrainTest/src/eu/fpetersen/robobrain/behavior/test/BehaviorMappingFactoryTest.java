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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.BehaviorMappingFactory;
import eu.fpetersen.robobrain.communication.RobotService;
import eu.fpetersen.robobrain.test.mock.MockRobotService;

/**
 * 
 * Tests the {@link BehaviorMappingFactory} class
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorMappingFactoryTest extends AndroidTestCase {

	public void testCreatingBehaviorMappingFromXML() {
		RobotService service = new MockRobotService(getContext());
		InputStream mappingXML = getContext().getResources().openRawResource(
				R.raw.behaviormapping);
		assertNotNull(mappingXML);
		BehaviorMappingFactory bmFac = BehaviorMappingFactory
				.getInstance(service);
		assertNotNull(bmFac);
		Map<String, List<String>> mapping = bmFac.createMappings(mappingXML);
		assertNotNull(mapping);
		assertEquals(1, mapping.keySet().size());
		String robotName = mapping.keySet().toArray(
				new String[mapping.keySet().size()])[0];
		List<String> behaviorNames = mapping.get(robotName);
		assertEquals(3, behaviorNames.size());
	}

}
