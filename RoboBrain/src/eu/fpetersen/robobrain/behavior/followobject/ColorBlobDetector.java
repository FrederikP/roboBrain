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
package eu.fpetersen.robobrain.behavior.followobject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 * @author Frederik Petersen
 * 
 */
public class ColorBlobDetector implements FollowObjectDetector {
	private Scalar CONTOUR_COLOR;
	// Lower and Upper bounds for range checking in HSV color space
	private Scalar mLowerBound = new Scalar(0);
	private Scalar mUpperBound = new Scalar(0);
	// Minimum contour area in percent for contours filtering
	private double mMinContourArea = 0.1;
	// Color radius for range checking in HSV color space
	private Scalar mColorRadius = new Scalar(25, 50, 50, 0);
	private Mat mSpectrum = new Mat();
	private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

	// Cache
	Mat mPyrDownMat = new Mat();
	Mat mHsvMat = new Mat();
	Mat mMask = new Mat();
	Mat mDilatedMask = new Mat();
	Mat mHierarchy = new Mat();
	private Point centroidOfMaxArea;
	private double maxArea;

	public ColorBlobDetector(Scalar hsvColor) {
		CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
		setHsvColor(hsvColor);
	}

	public void setColorRadius(Scalar radius) {
		mColorRadius = radius;
	}

	public void setHsvColor(Scalar hsvColor) {
		double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]
				- mColorRadius.val[0] : 0;
		double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255) ? hsvColor.val[0]
				+ mColorRadius.val[0] : 255;

		mLowerBound.val[0] = minH;
		mUpperBound.val[0] = maxH;

		mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
		mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

		mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
		mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

		mLowerBound.val[3] = 0;
		mUpperBound.val[3] = 255;

		Mat spectrumHsv = new Mat(1, (int) (maxH - minH), CvType.CV_8UC3);

		for (int j = 0; j < maxH - minH; j++) {
			byte[] tmp = { (byte) (minH + j), (byte) 255, (byte) 255 };
			spectrumHsv.put(0, j, tmp);
		}

		Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
	}

	public Mat getSpectrum() {
		return mSpectrum;
	}

	public void setMinContourArea(double area) {
		mMinContourArea = area;
	}

	public void process(Mat rgbaImage) {
		Imgproc.pyrDown(rgbaImage, mPyrDownMat);
		Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

		Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

		Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
		Imgproc.dilate(mMask, mDilatedMask, new Mat());

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL,
				Imgproc.CHAIN_APPROX_SIMPLE);

		// Find max contour area
		maxArea = 0;
		Iterator<MatOfPoint> each = contours.iterator();
		Mat biggestContour = null;
		while (each.hasNext()) {
			MatOfPoint wrapper = each.next();
			double area = Imgproc.contourArea(wrapper);
			if (area > maxArea) {
				maxArea = area;
				biggestContour = wrapper.clone();
			}
		}
		if (biggestContour != null) {
			Core.multiply(biggestContour, new Scalar(4, 4), biggestContour);
			Moments mo = Imgproc.moments(biggestContour);
			centroidOfMaxArea = new Point(mo.get_m10() / mo.get_m00(), mo.get_m01() / mo.get_m00());
		} else {
			centroidOfMaxArea = null;
		}

		// Filter contours by area and resize to fit the original image size
		mContours.clear();
		each = contours.iterator();
		while (each.hasNext()) {
			MatOfPoint contour = each.next();
			if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
				Core.multiply(contour, new Scalar(4, 4), contour);
				mContours.add(contour);
			}
		}

		Imgproc.drawContours(rgbaImage, mContours, -1, CONTOUR_COLOR);
	}

	public Point getObjectCentroid() {
		return centroidOfMaxArea;
	}

	public Double getObjectSize() {
		return maxArea;
	}
}
