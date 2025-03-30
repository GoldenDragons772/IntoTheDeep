package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.implementation.Constants;
import org.firstinspires.ftc.teamcode.implementation.RootSystem;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// must be java because easy opencv sim only compiles java
public class RedSampleDetection extends OpenCvPipeline {
    private RootSystem root;

    public RedSampleDetection(RootSystem root) {
        this.root = root;
        camera_matrix.put(0, 0, 1280, 0, 1280.0 / 2.0, 0, 720, 720.0 / 2.0, 0, 0, 1);
        distortion_coefficients.put(0, 0, -1.382, 2.25, 0, 0, -1.5);
    }

    Mat dst = new Mat();
    Mat cvt = new Mat();
    Mat camera_matrix = new Mat(3, 3, CvType.CV_64FC1);
    Mat distortion_coefficients = new Mat(1, 5, CvType.CV_64FC1);
    public double sampleRotation = 0.0;

    //    public double rotation;
    @Override
    public Mat processFrame(Mat mat) {
        Scalar RED_SAMPLE_LOW = new Scalar(Constants.MIN_RED_SAMPLE_HUE, 50, 50);
        Scalar RED_SAMPLE_HIGH = new Scalar(Constants.MAX_RED_SAMPLE_HUE, 255, 255);
/*
        // Rotation code for testing -- Hit CTRL + minus to fold it.
        Mat rotMat = Imgproc.getRotationMatrix2D(new Point(mat.width()/2, mat.height()/2), rotation, 1.0);
        Imgproc.warpAffine(mat, mat, rotMat, mat.size());
*/
        Mat newMat = mat.clone();
        Calib3d.undistort(mat, newMat, camera_matrix, distortion_coefficients);

        // Convert image to HSV for thresholding.
        Imgproc.cvtColor(newMat, cvt, Imgproc.COLOR_RGB2HSV);
        Core.inRange(cvt, RED_SAMPLE_LOW, RED_SAMPLE_HIGH, dst);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2 * 3 + 1, 2 * 3 + 1),
                new Point(3, 3));
        // Dilation slightly increases the selected area.
        Imgproc.dilate(dst, dst, kernel);
        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(dst, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > Constants.VISION_MIN_AREA) {
                filteredContours.add(contour);
            }
        }
        // Store the center coordinates and a reference to the rectangle.
        List<RotatedRect> boxCenters = new ArrayList<>();

        for (MatOfPoint i : filteredContours) {
            // Create the rectangles and plot them onto the rects mat.
            RotatedRect rrect = Imgproc.minAreaRect(new MatOfPoint2f(i.toArray()));
            boxCenters.add(rrect);
        }

        // Sort the box centers by the distance to the center of the image.
        boxCenters.sort((t0, t1) -> {
            Point center = new Point((double) dst.width() / 2, (double) dst.height() / 2);
            return (int) (distance(t1.center, center) - distance(t1.center, center));
        });
        // Do nothing if there's nothing on the screen
        if (boxCenters.isEmpty()) {
            sampleRotation = -70.0;
            return newMat;
            // The usage of two returns is not DeGennaro approved.
        }

        // Find the top line of the rectangle and get the angle from atan2 -- May not be the most accurate, but it works for my sample data.
        RotatedRect closest = boxCenters.get(0);

        Point[] points = new Point[4];
        closest.points(points);
        List<Point> lpoints = Arrays.stream(points)
                                    .sorted((i2, i1) -> (int) (i2.y - i1.y))
                                    .collect(Collectors.toList());
        Point highestPoint = lpoints.get(0);

        lpoints = Arrays.stream(points)
                        .sorted((i1, i2) -> (int) (distance(i2, highestPoint) - distance(i1, highestPoint)))
                        .collect(Collectors.toList());
        double theta = Math.atan2(highestPoint.y - lpoints.get(1).y, highestPoint.x - lpoints.get(1).x);
        root.getTelemetry().addData("Theta", theta);

        // Plot on mat.
        Imgproc.polylines(newMat, List.of(new MatOfPoint(points)), true, new Scalar(0, 255, 0), 3);
        Imgproc.line(newMat, new Point(closest.center.x - 250 * Math.cos(theta), closest.center.y - 250 * Math.sin(theta)), new Point(closest.center.x + 250 * Math.cos(theta), closest.center.y + 250 * Math.sin(theta)), new Scalar(255, 0, 0));
        Imgproc.putText(newMat, ((double) Math.round(theta * 1000)) / 1000 + "rad", closest.center, 1, 1, new Scalar(0, 0, 255));
        sampleRotation = theta;


        return newMat;

    }

    double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}
