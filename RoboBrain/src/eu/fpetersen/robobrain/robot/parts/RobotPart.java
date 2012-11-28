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
package eu.fpetersen.robobrain.robot.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.fpetersen.robobrain.communication.RobotReceiver;
import eu.fpetersen.robobrain.robot.Robot;
import eu.fpetersen.robobrain.util.RoboLog;
import eu.fpetersen.robobrain.util.exceptions.RequiredFlagsNotSetException;

/**
 * @author Frederik Petersen
 * 
 * 
 *         When implementing a RobotPart class, please make sure to supply a
 *         default constructor (i.e. don't specify any constructor). For now
 *         there is no way to supply more parameters on initialization. This
 *         will change soon. Initialization values will then be entered through
 *         the .xml config file of the robot.
 * 
 */
public abstract class RobotPart {
	private Robot mRobot;
	private Map<String, Character> mFlags;
	private String mId;

	private RoboLog mLog;

	/**
	 * This needs to be called after instantiated the RobotPart and it exists
	 * because the default constructor is needed to dynamically decide which
	 * subclass is to be created. Also runs the flag-id requirement check
	 * 
	 * @param robot
	 *            Robot the Part belongs to.
	 * @return true if flag requirements are met, false if not
	 */
	public boolean initialize(String id, Robot robot, Map<String, Character> flags) {
		mLog = new RoboLog("RobotPart", robot.getRobotService());
		this.mId = id;
		this.mRobot = robot;
		this.mFlags = flags;
		try {
			checkForRequiredFlags();
		} catch (RequiredFlagsNotSetException e) {
			for (String flagId : e.getMissingFlagIds()) {
				mLog.alertError("Required flag with the id \"" + flagId + "\" missing for robot \""
						+ robot.getName() + "\" and it's part \"" + mId + "\"");
			}
			return false;
		}

		return true;
	}

	/**
	 * Checks if all required Flags are set in xml
	 * 
	 * @throws RequiredFlagsNotSetException
	 *             if a required flag is not set
	 */
	private void checkForRequiredFlags() throws RequiredFlagsNotSetException {
		List<String> requiredFlagIds = getRequiredFlagIds();
		if (requiredFlagIds == null) {
			return;
		}
		List<String> missingFlagIds = new ArrayList<String>();
		for (String reqFlag : requiredFlagIds) {
			if (mFlags == null || !mFlags.containsKey(reqFlag)) {
				missingFlagIds.add(reqFlag);
			}
		}

		if (missingFlagIds.size() > 0) {
			throw new RequiredFlagsNotSetException(missingFlagIds);
		}

	}

	/**
	 * @return a List of Ids of required Flags that need to be set in robots xml
	 */
	protected abstract List<String> getRequiredFlagIds();

	protected RobotPart() {
	}

	public Robot getRobot() {
		return mRobot;
	}

	/**
	 * Returns the flag dientified by the id. If flag does not exist, returns
	 * the following char: ' ' And displays error. Flags are defined in robots
	 * xml like this:
	 * 
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="utf-8"?>
	 * 	<robot name="TESTBOT" address="TESTADDRESS">
	 * 		<parts>
	 * 			<part type="Motor" id="main_motor">
	 * 		    	<flags>
	 * 		    		<flag id="advance" flag="A"/>
	 * 		    		<flag id="backoff" flag="B"/>
	 * 		    		<flag id="left" flag="L"/>
	 * 		    		<flag id="right" flag="R"/>
	 * 		    		<flag id="stop" flag="S"/>
	 * 		    	</flags>
	 * 			</part>
	 * 			<part type="ProximitySensor" id="front_proxsensor" />
	 * 			<part type="ProximitySensor" id="back_proxsensor" />
	 * 			<part type="Servo" id="head_servo">
	 * 		    	<flags>
	 * 		    		<flag id="toAngle" flag="C"/>
	 * 		    	</flags>
	 * 			</part>
	 * 			<part type="RgbLed" id="headcolor_rgbled">
	 * 		    	<flags>
	 * 		    		<flag id="toColor" flag="D"/>
	 * 		    	</flags>
	 * 			</part>	
	 * 		</parts>
	 * 	</robot>
	 * }
	 * </pre>
	 * 
	 * @param id
	 *            Identifies the flag to be returned
	 * @return The flag if it exists or ' ' if not
	 */
	public char getFlag(String id) {
		if (!mFlags.containsKey(id)) {
			mLog.alertError("Flag with id \"" + id + "\" was not defined in xml");
			return ' ';
		}
		return mFlags.get(id);
	}

	/**
	 * This will be called in {@link RobotReceiver} and gives the user a generic
	 * way to set values of a robot part
	 * 
	 * @param data
	 */
	public abstract void onReceive(String data);

}
