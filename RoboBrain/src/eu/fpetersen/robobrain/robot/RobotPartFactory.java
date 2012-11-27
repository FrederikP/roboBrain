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
package eu.fpetersen.robobrain.robot;

import eu.fpetersen.robobrain.robot.parts.RobotPart;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.util.RoboBrainFactory;

/**
 * Singleton factory which creates RobotParts. Dynamically chooses subclass
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotPartFactory extends RoboBrainFactory {

	private static RobotPartFactory sInstance;

	private RobotPartFactory(RobotService service) {
		super(service);

	}

	public static RobotPartFactory getInstance(RobotService service) {
		if (sInstance == null) {
			sInstance = new RobotPartFactory(service);
		} else if (sInstance.getService() != service) {
			sInstance.setService(service);
		}
		return sInstance;
	}

	/**
	 * Creates a RobotPart instance for the specified RobotPart type. The
	 * RobotPart is expected to be in the package "eu.fpetersen.robobrain.robot"
	 * and the class should be called: TYPE.java . In this case TYPE would be
	 * passed to the method to instantiate the class.
	 * 
	 * @param type
	 *            Name of the RobotPart implementation.
	 * @param initializer
	 *            In charge of correctly initializing the RobotPart
	 * @return RobotPart instance or null if something went wrong. If something
	 *         is amiss, check log.
	 */
	public RobotPart createRobotPart(String type, RobotPartInitializer initializer) {
		String partClassName;
		if (!type.contains(".")) {
			String robotPartPackageName = RobotPart.class.getPackage().getName();
			partClassName = robotPartPackageName + "." + type;
		} else {
			partClassName = type;
		}
		RobotPart part = null;
		try {
			part = (RobotPart) Class.forName(partClassName).newInstance();
			initializer.initialize(part);
		} catch (InstantiationException e) {
			mLog.alertWarning("RobotPart class " + type + " could not be instantiated");
		} catch (IllegalAccessException e) {
			mLog.alertWarning("RobotPart class " + type
					+ " could not be instantiated due to the constructor having restricted access");
		} catch (ClassNotFoundException e) {
			mLog.alertWarning("RobotPart class " + type + " could not be found");
		}
		return part;
	}

}
