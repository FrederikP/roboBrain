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
package eu.fpetersen.robobrain.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import eu.fpetersen.robobrain.R;

/**
 * Helper class for accessing external storage files. Also creates directories
 * and empty files if needed.
 * 
 * @author Frederik Petersen
 * 
 */
public class ExternalStorageManager {

	/**
	 * Creates new directory if it does not exist
	 * 
	 * @param dir
	 *            Directory to create if it does not already exist
	 */
	private static void createDirIfNotExistant(Context context, File dir) {
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				RoboLog.alertError(context,
						"Directory could not be created: " + dir.getAbsolutePath());
			}
		}
	}

	/**
	 * 
	 * @return File that represents robobrains root directory in sd card.
	 */
	public static File getRoboBrainRoot(Context context) {
		File roboBrainRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getString(R.string.sd_robobrain_root_dir));
		createDirIfNotExistant(context, roboBrainRoot);
		return roboBrainRoot;
	}

	/**
	 * 
	 * @return File that represents robobrains behaviormapping.xml file
	 */
	public static File getBehaviorMappingFile(Context context) {
		File roboBrainRoot = getRoboBrainRoot(context);
		File behaviorMappingFile = new File(roboBrainRoot, "behaviormapping.xml");
		if (!behaviorMappingFile.exists()) {
			try {
				behaviorMappingFile.createNewFile();
			} catch (IOException e) {
				RoboLog.alertError(context,
						"Something went wrong when trying to create behaviormapping file. Check sd card and log.");
				e.printStackTrace();
			}
		}
		return behaviorMappingFile;
	}

	/**
	 * 
	 * @return File that represents the directory which hold the robot
	 *         configuration *.xml files.
	 */
	public static File getRobotsXmlDir(Context context) {
		File robotsXmlDir = new File(getRoboBrainRoot(context).getAbsolutePath() + File.separator
				+ context.getString(R.string.sd_robobrain_robots_xml_dir));
		createDirIfNotExistant(context, robotsXmlDir);
		return robotsXmlDir;
	}
}
