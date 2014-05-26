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
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

/**
 * @author Frederik Petersen
 * 
 */
public class OrbObjectDetector implements FollowObjectDetector {

	private FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);

	private DescriptorMatcher matcher = DescriptorMatcher
			.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

	private DescriptorExtractor descriptorExtractor = DescriptorExtractor
			.create(DescriptorExtractor.ORB);

	private Mat originalDescriptors;

	private MatOfKeyPoint originalKeypoints;

	private Mat originalImage;

	public OrbObjectDetector(Mat objectImage) {
		this.originalImage = objectImage;
		originalKeypoints = detectInImage(objectImage);
		originalDescriptors = extractDescriptors(originalKeypoints, objectImage);
	}

	private Mat extractDescriptors(MatOfKeyPoint keypoints, Mat image) {
		Mat descriptors = new Mat();
		descriptorExtractor.compute(image, keypoints, descriptors);
		return descriptors;
	}

	private MatOfKeyPoint detectInImage(Mat image) {
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		featureDetector.detect(image, keypoints);
		return keypoints;
	}

	public Point getObjectCentroid() {
		// TODO Auto-generated method stub
		return null;
	}

	public Double getObjectSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void process(Mat image) {
		Mat tempImage = new Mat();
		Imgproc.cvtColor(image, tempImage, Imgproc.COLOR_RGBA2RGB);
		MatOfKeyPoint keypoints = detectInImage(tempImage);
		Mat descriptors = extractDescriptors(keypoints, tempImage);
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descriptors, originalDescriptors, matches);

		KeyPoint[] keypointArray = keypoints.toArray();
		KeyPoint[] originalKeypointArray = originalKeypoints.toArray();

		float min = 40.0f;
		float max = 1000.0f;
		for (DMatch match : matches.toList()) {
			if (match.distance < min) {
				min = match.distance;
			} else if (match.distance > max) {
				max = match.distance;
			}
		}

		float threshold = 1.5f * min;
		List<KeyPoint> matchedKeyPoints = new ArrayList<KeyPoint>();
		List<Point> matchedPoints = new ArrayList<Point>();
		List<Point> matchedOriginalPoints = new ArrayList<Point>();
		for (DMatch match : matches.toList()) {
			if (match.distance < threshold) {
				KeyPoint matchedKeypoint = keypointArray[match.queryIdx];
				matchedKeyPoints.add(matchedKeypoint);
				matchedPoints.add(matchedKeypoint.pt);

				KeyPoint matchedOriginalKeypoint = originalKeypointArray[match.trainIdx];
				matchedOriginalPoints.add(matchedOriginalKeypoint.pt);
			}
		}

		if (matchedKeyPoints.size() > 10) {

			Mat H = Calib3d.findHomography(
					new MatOfPoint2f(matchedOriginalPoints.toArray(new Point[matchedOriginalPoints
							.size()])),
					new MatOfPoint2f(matchedPoints.toArray(new Point[matchedPoints.size()])),
					Calib3d.RANSAC, 10);

			List<Point> originalCorners = new ArrayList<Point>();
			originalCorners.add(new Point(0, 0));
			originalCorners.add(new Point(originalImage.cols(), 0));
			originalCorners.add(new Point(originalImage.cols(), originalImage.rows()));
			originalCorners.add(new Point(0, originalImage.rows()));

			List<Point> corners = new ArrayList<Point>();
			for (int i = 0; i < 4; i++) {
				corners.add(new Point(0, 0));
			}
			Mat objectCorners = Converters.vector_Point2f_to_Mat(corners);

			Core.perspectiveTransform(Converters.vector_Point2f_to_Mat(originalCorners),
					objectCorners, H);
			corners.clear();
			Converters.Mat_to_vector_Point2f(objectCorners, corners);

			Core.line(tempImage, corners.get(0), corners.get(1), new Scalar(0, 255, 0), 4);
			Core.line(tempImage, corners.get(1), corners.get(2), new Scalar(0, 255, 0), 4);
			Core.line(tempImage, corners.get(2), corners.get(3), new Scalar(0, 255, 0), 4);
			Core.line(tempImage, corners.get(3), corners.get(0), new Scalar(0, 255, 0), 4);
		}

		Features2d.drawKeypoints(tempImage,
				new MatOfKeyPoint(matchedKeyPoints.toArray(new KeyPoint[matchedKeyPoints.size()])),
				tempImage);
		Imgproc.cvtColor(tempImage, image, Imgproc.COLOR_RGB2RGBA);

	}
}
