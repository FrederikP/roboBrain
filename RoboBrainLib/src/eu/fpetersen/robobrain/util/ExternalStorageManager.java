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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import eu.fpetersen.robobrainlib.R;

/**
 * Helper class for accessing external storage files. Also creates directories
 * and empty files if needed.
 * 
 * @author Frederik Petersen
 * 
 */
public class ExternalStorageManager {

	private Context mContext;

	private RoboLog mLog;

	public ExternalStorageManager(Context context) {
		mContext = context;
		mLog = new RoboLog("ExternalStorageManager", context);
	}

	/**
	 * Creates new directory if it does not exist
	 * 
	 * @param dir
	 *            Directory to create if it does not already exist
	 */
	public void createDirIfNotExistant(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				mLog.alertError("Directory could not be created: " + dir.getAbsolutePath());
			}
		}
	}

	/**
	 * 
	 * @return File that represents robobrains root directory in sd card.
	 */
	public File getRoboBrainRoot() {
		File roboBrainRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + mContext.getString(R.string.sd_robobrain_root_dir));
		createDirIfNotExistant(roboBrainRoot);
		return roboBrainRoot;
	}

	/**
	 * 
	 * @return File that represents robobrains behaviormapping.xml file
	 */
	public InputStream getBehaviorMappingFile(String name) {
		File roboBrainRoot = getRoboBrainRoot();
		File behaviorMappingFile = new File(roboBrainRoot, name);
		if (!behaviorMappingFile.exists()) {
			try {
				behaviorMappingFile.createNewFile();
			} catch (IOException e) {
				mLog.alertError("Something went wrong when trying to create behaviormapping file. Check sd card and log.");
				e.printStackTrace();
			}
		}
		InputStream stream = null;
		try {
			stream = new FileInputStream(behaviorMappingFile);
		} catch (FileNotFoundException e) {
			mLog.alertError("behaviormapping file could bot be read");
			e.printStackTrace();
		}

		return stream;
	}

	/**
	 * 
	 * @return File that represents the directory which hold the robot
	 *         configuration *.xml files.
	 */
	public File getRobotsXmlDir() {
		File robotsXmlDir = new File(getRoboBrainRoot().getAbsolutePath() + File.separator
				+ mContext.getString(R.string.sd_robobrain_robots_xml_dir));
		createDirIfNotExistant(robotsXmlDir);
		return robotsXmlDir;
	}

	/**
	 * Should be checked before calling other methods.
	 * 
	 * @return True if SDcard is mounted, false if it's removed
	 */
	public boolean sdCardIsMountedAndWritable() {
		String state = Environment.getExternalStorageState();
		if (!state.matches(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return RoboBrains Music Directory
	 */
	public File getMusicDir() {
		File musicDir = new File(getRoboBrainRoot().getAbsolutePath() + File.separator
				+ mContext.getString(R.string.sd_robobrain_music_dir));
		createDirIfNotExistant(musicDir);
		return musicDir;
	}
}
