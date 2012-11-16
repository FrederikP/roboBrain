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

import java.util.Map;

import eu.fpetersen.robobrain.util.RoboLog;

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

	/**
	 * This needs to be called after instantiated the RobotPart and it exists
	 * because the default constructor is needed to dynamically decide which
	 * subclass is to be created
	 * 
	 * @param robot
	 *            Robot the Part belongs to.
	 */
	protected void initialize(Robot robot, Map<String, Character> flags) {
		this.mRobot = robot;
		this.mFlags = flags;
	}

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
			RoboLog.alertError(mRobot.getRobotService(), "Flag with id \"" + id
					+ "\" was not defined in xml");
			return ' ';
		}
		return mFlags.get(id);
	}

}
