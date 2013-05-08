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
package eu.fpetersen.eu.robobrain.androidsensors;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import eu.fpetersen.robobrain.util.RoboLog;

/**
 * @author Frederik Petersen
 * 
 */
public class Compass implements SensorEventListener {

	private static int HISTORY_SIZE = 100;

	private Sensor mMagnetometer;
	private Sensor mAccelerometer;

	private float[] mGravity;
	private float[] mGeomagnetic;
	private Deque<Float> azimutHistory = new ArrayDeque<Float>(HISTORY_SIZE + 1);
	private Deque<Boolean> signHistory = new ArrayDeque<Boolean>(HISTORY_SIZE + 1);
	int plus = 0;
	int minus = 0;
	private SensorManager mSensorManager;
	private RoboLog mLog;

	public void initialize(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
		mLog = new RoboLog("Compass", context);
	}

	public float getDegreeToNorth() {
		synchronized (azimutHistory) {
			int addedValues = 0;
			Iterator<Float> azimuts = azimutHistory.iterator();
			float sum = 0;
			boolean sign = (plus >= minus);
			while (azimuts.hasNext()) {
				float value = azimuts.next();
				if ((sign && value >= 0) || (!sign && value < 0)) {
					sum = sum + value;
					addedValues = addedValues + 1;
				}

			}
			// between 0 and 180

			float deg = (sum / addedValues) * 360 / (2 * 3.14159f);
			mLog.log("Deg: " + deg, true);
			mLog.log("Added Values: " + addedValues, true);
			return (sum / addedValues) * 360 / (2 * 3.14159f);
		}
	}

	/**
	 * Copied from
	 * http://www.codingforandroid.com/2011/01/using-orientation-sensors
	 * -simple.html by Fernando Greenyway
	 */
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				if (orientation[0] >= 0) {
					plus = plus + 1;
					signHistory.addFirst(true);
				} else {
					signHistory.addFirst(false);
					minus = minus + 1;
				}
				while (signHistory.size() > HISTORY_SIZE) {
					boolean sign = signHistory.removeLast();
					if (sign) {
						plus = plus - 1;
					} else {
						minus = minus - 1;
					}
				}
				while (azimutHistory.size() > HISTORY_SIZE) {
					azimutHistory.removeLast();
				}
				azimutHistory.addFirst(orientation[0]); // orientation
														// contains:
				// azimut, pitch
				// and roll
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * 
	 */
	public void tearDown() {
		mSensorManager.unregisterListener(this);
	}

}
