package eu.fpetersen.robobrain.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import eu.fpetersen.robobrain.ui.R;

public class ExternalStorageManager {

	private static void createDirIfNotExistant(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				// TODO Exception handling
				// throw new
				// RoboBrainDirectoryNotCreatedException("No directory ");
			}
		}
	}

	public static File getRoboBrainRoot() {
		File roboBrainRoot = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ R.string.sd_robobrain_root_dir);
		createDirIfNotExistant(roboBrainRoot);
		return roboBrainRoot;
	}

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

	public static File getRobotsXmlDir() {
		File robotsXmlDir = new File(getRoboBrainRoot().getAbsolutePath()
				+ File.separator + R.string.sd_robobrain_robots_xml_dir);
		createDirIfNotExistant(robotsXmlDir);
		return robotsXmlDir;
	}
}
