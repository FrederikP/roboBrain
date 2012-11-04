package eu.fpetersen.robobrain.util.test;

import java.io.File;

import android.test.AndroidTestCase;
import eu.fpetersen.robobrain.util.ExternalStorageManager;

public class ExternalStorageManagerTest extends AndroidTestCase {

	/**
	 * Tests if robot xml dir exists (or can be created) and can be referenced
	 */
	public void testGetRobotXmlDir() {
		File file = ExternalStorageManager.getRobotsXmlDir(getContext());
		checkIfExists(file);
	}

	/**
	 * Tests if behaviormapping.xml exists (or can be created) and can be
	 * referenced
	 */
	public void testGetBehaviorMapping() {
		File file = ExternalStorageManager.getBehaviorMappingFile(getContext());
		checkIfExists(file);
	}

	/**
	 * Tests if robot root dir exists (or can be created) and can be referenced
	 */
	public void testRobotRootDirMapping() {
		File file = ExternalStorageManager.getRoboBrainRoot(getContext());
		checkIfExists(file);
	}

	public static void checkIfExists(File file) {
		assertNotNull(file);
		assertTrue(file.exists());
	}

}
