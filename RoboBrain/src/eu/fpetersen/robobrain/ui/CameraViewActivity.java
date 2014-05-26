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
package eu.fpetersen.robobrain.ui;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import eu.fpetersen.robobrain.R;
import eu.fpetersen.robobrain.behavior.followobject.ColorBlobDetector;
import eu.fpetersen.robobrain.behavior.followobject.FollowObjectDetector;
import eu.fpetersen.robobrain.behavior.followobject.FollowObjectIntent;

/**
 * @author Frederik Petersen
 * 
 */
public class CameraViewActivity extends Activity implements CvCameraViewListener2 {
	private static final String TAG = "CameraView::Activity";

	private CameraBridgeViewBase mOpenCvCameraView;

	private Mat mRgba;
	private FollowObjectDetector mDetector;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	private long timeOfLastShout = 0;

	private boolean goingForward = false;

	private long timeOfLastShout2 = 0;

	public CameraViewActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_camera_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);

		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
		Intent quit = new Intent(FollowObjectIntent.ACTION_QUIT);
		sendBroadcast(quit);
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mDetector = new ColorBlobDetector(new Scalar(3.109375, 241, 186.640625));
		// ExternalStorageManager manager = new ExternalStorageManager(this);
		// File root = manager.getRoboBrainRoot();
		// File images = new File(root, "images");
		// File objectPicture = new File(images, "card2.jpg");
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// Bitmap bitmap =
		// BitmapFactory.decodeFile(objectPicture.getAbsolutePath(), options);
		// Mat objectImage = new Mat();
		// Utils.bitmapToMat(bitmap, objectImage);
		// mDetector = new OrbObjectDetector(objectImage);
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mDetector.process(mRgba);

		Point centroid = mDetector.getObjectCentroid();
		boolean rightLeft = false;
		if (centroid != null && System.currentTimeMillis() - timeOfLastShout > 500) {
			rightLeft = handleCentroid(centroid);
			timeOfLastShout = System.currentTimeMillis();
		}

		Double area = mDetector.getObjectSize();
		if (!rightLeft && area > 0 && System.currentTimeMillis() - timeOfLastShout2 > 500) {
			handleArea(area);
			timeOfLastShout2 = System.currentTimeMillis();
		}

		return mRgba;
	}

	/**
	 * @param area
	 */
	private void handleArea(Double area) {
		Intent movement = new Intent();
		int limit = 3000;
		if (!goingForward && area <= limit && area > 0) {
			movement.setAction(FollowObjectIntent.ACTION_FORWARD);
			goingForward = true;
			sendBroadcast(movement);
		} else if (goingForward && area > limit) {
			movement.setAction(FollowObjectIntent.ACTION_STOP);
			goingForward = false;
			sendBroadcast(movement);
		}
	}

	/**
	 * @param centroid
	 */
	private boolean handleCentroid(Point centroid) {
		Intent movement = new Intent();
		int divider = 3;
		if (centroid.y < mRgba.height() / (double) divider) {
			movement.setAction(FollowObjectIntent.ACTION_RIGHT);
			sendBroadcast(movement);
			return true;
		} else if (centroid.y > (divider - 1) * mRgba.height() / (double) divider) {
			movement.setAction(FollowObjectIntent.ACTION_LEFT);
			sendBroadcast(movement);
			return true;
		}
		return false;
	}

}
