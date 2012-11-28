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
package eu.fpetersen.robobrain.communication.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.test.ServiceTestCase;
import at.abraxas.amarino.AmarinoIntent;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.services.RobotService;
import eu.fpetersen.robobrain.test.util.Helper;
import eu.fpetersen.robobrain.util.ExternalStorageManager;

/**
 * Tests the RobotService, isolated from activities, etc.
 * 
 * @author Frederik Petersen
 * 
 */
public class RobotServiceTest extends ServiceTestCase<RobotService> {

	private boolean changedSd = false;

	/**
	 * @param serviceClass
	 *            RobotService Class - The class we wish to test
	 */
	public RobotServiceTest() {
		super(RobotService.class);
	}

	/**
	 * Test starting the Robot Service per Intent
	 */
	public void testStartingRobotService() {
		RobotService service = startService();

		assertNotNull(service);

		// Check if onCreate() was called
		assertNotNull(service.getDistributingSpeechReceiver());

		double countSecs = 0;
		while (!service.isRunning() && countSecs < 20) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}

		assertTrue(service.isRunning());

		shutdownService();

		countSecs = 0;
		while (service.isRunning() && countSecs < 5) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}
		assertFalse(service.isRunning());

	}

	private RobotService startService() {
		startService(new Intent(getContext(), RobotService.class));

		double countSecs = 0;
		RobotService service = getService();
		while (service == null && countSecs < 5) {
			Helper.sleepMillis(000);
			countSecs = countSecs + 0.1;
			service = getService();
		}
		return service;
	}

	public void testIfOnBindReturnsNull() {
		RobotService service = startService();
		assertNull(service.onBind(new Intent()));
		stopService(service);
	}

	public void testStoppingService() {
		RobotService service = startService();
		service.stopService(new Intent(getContext(), RobotService.class));
		stopService(service);
	}

	private void stopService(RobotService service) {
		double countSecs = 0;
		while (service.isRunning() && countSecs < 10) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}
		assertFalse(service.isRunning());
	}

	/**
	 * Go through the different szenarios of starting and stopping with amarino
	 * connection intents and example behavior xml
	 * 
	 * @throws IOException
	 */
	public void testRobotServiceConnectionManagement() throws IOException {

		changedSd = true;

		copyTestFilesToSdCard();

		RobotService service = startService();

		assertNotNull(service);

		// Check if onCreate() was called
		assertNotNull(service.getDistributingSpeechReceiver());

		Helper.sleepMillis(1500);

		assertEquals(1, service.getAllCCs().size());

		Intent connectedIntent = new Intent(AmarinoIntent.ACTION_CONNECTED);
		connectedIntent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, "TESTADDRESS");
		getContext().sendBroadcast(connectedIntent);

		double countSecs = 0;
		while (!service.isRunning() && countSecs < 20) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}

		assertTrue(service.isRunning());

		Helper.sleepMillis(500);

		Intent disconnectedIntent = new Intent(AmarinoIntent.ACTION_DISCONNECTED);
		disconnectedIntent.putExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS, "TESTADDRESS");
		getContext().sendBroadcast(disconnectedIntent);

		shutdownService();

		countSecs = 0;
		while (service.isRunning() && countSecs < 5) {
			Helper.sleepMillis(100);
			countSecs = countSecs + 0.1;
		}
		assertFalse(service.isRunning());

	}

	/**
	 * Restore the changes to the sd card. Roll back backups and delete
	 * testfiles
	 * 
	 * @throws IOException
	 */
	private void restoreSdCard() throws IOException {
		ExternalStorageManager eManager = new ExternalStorageManager(getContext());
		File roboBrainRoot = eManager.getRoboBrainRoot();
		File behaviorMapping = new File(roboBrainRoot, "behaviormapping.xml");
		behaviorMapping.delete();
		File behaviorMappingBackup = new File(roboBrainRoot, "behaviormapping.xmlbackup");
		if (behaviorMappingBackup.exists()) {
			restoreBackup(behaviorMappingBackup);
		}

		File robotDir = eManager.getRobotsXmlDir();
		File robotFile = new File(robotDir, "testbot.xml");
		robotFile.delete();
		File robotFileBackup = new File(robotDir, "testbot.xmlbackup");
		if (robotFileBackup.exists()) {
			restoreBackup(robotFileBackup);
		}
	}

	/**
	 * @param behaviorMappingBackup
	 * @throws IOException
	 */
	private void restoreBackup(File backup) throws IOException {
		File dir = backup.getParentFile();
		File origFile = new File(dir, backup.getName().replace("backup", ""));
		origFile.createNewFile();
		copyFile(backup, origFile);
		backup.delete();
	}

	/**
	 * Backup existing behaviormapping.xml Copy behaviormapping and and testbot
	 * to sdcard.
	 * 
	 * @throws IOException
	 */
	private void copyTestFilesToSdCard() throws IOException {
		ExternalStorageManager eManager = new ExternalStorageManager(getContext());
		File roboBrainRoot = eManager.getRoboBrainRoot();
		File behaviorMapping = new File(roboBrainRoot, "behaviormapping.xml");
		if (behaviorMapping.exists()) {
			createBackUp(behaviorMapping);
		}
		behaviorMapping.createNewFile();
		InputStream testFileStream = getContext().getResources().openRawResource(
				R.raw.behaviormapping);
		writeStreamToFile(testFileStream, behaviorMapping);

		File robotDir = eManager.getRobotsXmlDir();
		File robotFile = new File(robotDir, "testbot.xml");
		if (robotFile.exists()) {
			createBackUp(robotFile);
		}
		robotFile.createNewFile();
		testFileStream = getContext().getResources().openRawResource(R.raw.testbot);
		writeStreamToFile(testFileStream, robotFile);
	}

	/**
	 * Writes stream to file.
	 * 
	 * @param in
	 * @param to
	 * @throws IOException
	 */
	private void writeStreamToFile(InputStream in, File to) throws IOException {

		// create FileOutputStream object for destination file
		FileOutputStream fout = new FileOutputStream(to);

		byte[] b = new byte[1024];
		int noOfBytes = 0;

		// read bytes from source file and write to destination file
		while ((noOfBytes = in.read(b)) != -1) {
			fout.write(b, 0, noOfBytes);
		}

		// close the streams
		in.close();
		fout.close();
	}

	/**
	 * Creates backup of file
	 * 
	 * @param original
	 * @throws IOException
	 */
	private void createBackUp(File original) throws IOException {
		File dir = original.getParentFile();
		File backupFile = new File(dir, original.getName() + "backup");
		backupFile.createNewFile();
		copyFile(original, backupFile);
	}

	/**
	 * Copy file
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	private void copyFile(File from, File to) throws IOException {
		// create FileInputStream object for source file
		InputStream fin = new FileInputStream(from);

		// create FileOutputStream object for destination file
		FileOutputStream fout = new FileOutputStream(to);

		byte[] b = new byte[1024];
		int noOfBytes = 0;

		// read bytes from source file and write to destination file
		while ((noOfBytes = fin.read(b)) != -1) {
			fout.write(b, 0, noOfBytes);
		}

		// close the streams
		fin.close();
		fout.close();
	}

	@Override
	protected void tearDown() throws Exception {

		if (changedSd) {
			restoreSdCard();
			changedSd = false;
		}
		// ///CLOVER:FLUSH
		super.tearDown();
	}

}
