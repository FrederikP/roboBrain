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
package eu.fpetersen.robobrain.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to share RobotService with activities
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotServiceContainer {

	private static Map<UUID, RobotService> sRobotServices = new HashMap<UUID, RobotService>();

	public static UUID addRobotService(RobotService service) {
		UUID id = UUID.randomUUID();
		sRobotServices.put(id, service);
		return id;
	}

	public static RobotService getRobotService(UUID id) {
		return sRobotServices.get(id);
	}

	public static void removeRobotService(UUID id) {
		sRobotServices.remove(id);
	}

	private RobotServiceContainer() {
	}

}
