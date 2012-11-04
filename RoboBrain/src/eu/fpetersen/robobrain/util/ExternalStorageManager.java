package eu.fpetersen.robobrain.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.communication.RobotService;

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
	private static void createDirIfNotExistant(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				RoboLog.log("Directory could not be created: "
						+ dir.getAbsolutePath());
			}
		}
	}

	/**
	 * 
	 * @return File that represents robobrains root directory in sd card.
	 */
	public static File getRoboBrainRoot() {
		File roboBrainRoot = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ RobotService.getInstance().getString(
						R.string.sd_robobrain_root_dir));
		createDirIfNotExistant(roboBrainRoot);
		return roboBrainRoot;
	}

	/**
	 * 
	 * @return File that represents robobrains behaviormapping.xml file
	 */
	public static File getBehaviorMappingFile() {
		File roboBrainRoot = getRoboBrainRoot();
		File behaviorMappingFile = new File(roboBrainRoot,
				"behaviormapping.xml");
		if (!behaviorMappingFile.exists()) {
			try {
				behaviorMappingFile.createNewFile();
			} catch (IOException e) {
				// TODO Exception handling
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
	public static File getRobotsXmlDir() {
		File robotsXmlDir = new File(getRoboBrainRoot().getAbsolutePath()
				+ File.separator
				+ RobotService.getInstance().getString(
						R.string.sd_robobrain_robots_xml_dir));
		createDirIfNotExistant(robotsXmlDir);
		return robotsXmlDir;
	}
}
