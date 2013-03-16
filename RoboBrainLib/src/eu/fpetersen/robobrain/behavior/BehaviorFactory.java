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
package eu.fpetersen.robobrain.behavior;

import java.util.StringTokenizer;

import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.util.RoboBrainFactory;

/**
 * 
 * Singleton Factory that allows dynamic creation of Behaviors by their class
 * names.
 * 
 * @author Frederik Petersen
 * 
 */
public class BehaviorFactory extends RoboBrainFactory {

	private static BehaviorFactory sInstance;

	private BehaviorFactory(RobotService service) {
		super(service);
	}

	/**
	 * 
	 * @return Singleton instance of BehaviorFactory
	 */
	public static BehaviorFactory getInstance(RobotService service) {
		if (sInstance == null) {
			sInstance = new BehaviorFactory(service);
		} else if (sInstance.getService() != service) {
			sInstance.setService(service);
		}
		return sInstance;
	}

	/**
	 * Creates a Behavior instance for the specified Behavior name. The behavior
	 * is expected to be in the package "eu.fpetersen.robobrain.behavior" and
	 * the class should be called: NAME.java . In this case NAME would be passed
	 * to the method to instantiate the class.
	 * 
	 * @param initializer
	 *            Initializer containing information about the behavior
	 *            configured in behaviormapping.xml
	 * @param robot
	 *            The robot this behavior is supposed to be created for.
	 * @return Behavior instance or null if something went wrong. If something
	 *         is amiss, check log.
	 */
	public Behavior createBehavior(BehaviorInitializer initializer, Robot robot) {
		String name = initializer.getName();
		if (!name.contains(".")) {
			String behaviorPackageName = Behavior.class.getPackage().getName();
			name = behaviorPackageName + "." + name;
		}
		Behavior behavior = null;
		try {
			behavior = (Behavior) Class.forName(name).newInstance();
			String shortName = name;
			StringTokenizer nameTokenizer = new StringTokenizer(name, ".");
			while (nameTokenizer.hasMoreTokens()) {
				shortName = nameTokenizer.nextToken();
			}

			initializer.setName(shortName);
			initializer.initialize(behavior, robot);
		} catch (InstantiationException e) {
			mLog.alertWarning("Behavior class " + name + " could not be instantiated");
		} catch (IllegalAccessException e) {
			mLog.alertWarning("Behavior class " + name
					+ " could not be instantiated due to the constructor having restricted access");
		} catch (ClassNotFoundException e) {
			mLog.alertWarning("Behavior class " + name + " could not be found");
		}
		return behavior;
	}

}
