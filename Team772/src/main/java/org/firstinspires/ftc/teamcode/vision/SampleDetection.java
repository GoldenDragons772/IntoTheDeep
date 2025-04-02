package org.firstinspires.ftc.teamcode.vision;

//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// must be java because easy opencv sim only compiles java
public class SampleDetection extends OpenCvPipeline {
    private Telemetry telemetry;
    public static Scalar RED_SAMPLE_LOW = new Scalar(170, 90, 90);
    public static Scalar RED_SAMPLE_HIGH = new Scalar(180, 255, 255);
    public static Scalar YELLOW_SAMPLE_LOW = new Scalar(230, 90, 90);
    public static Scalar YELLOW_SAMPLE_HIGH = new Scalar(240, 255, 255);
    public static Scalar BLUE_SAMPLE_LOW = new Scalar(30, 90, 90);
    public static Scalar BLUE_SAMPLE_HIGH = new Scalar(40, 255, 255);
    public static double VISION_MIN_AREA = 30000;
    public static Scalar kvs = new Scalar(-1.382, 2.25, -1.5);
    public Scalar SAMPLE_LOW;
    public Scalar SAMPLE_HIGH;
    public Point centroid;

    public SampleDetection(Telemetry tel, boolean isRed) {
        this.telemetry = tel;
        if (isRed) {
            SAMPLE_LOW = RED_SAMPLE_LOW;
            SAMPLE_HIGH = RED_SAMPLE_HIGH;
        }
        else {
            SAMPLE_LOW = BLUE_SAMPLE_LOW;
            SAMPLE_HIGH = BLUE_SAMPLE_HIGH;
        }
    }

    Mat dst = new Mat();
    Mat cvt = new Mat();
    Mat camera_matrix = new Mat(3, 3, CvType.CV_64FC1);
    public double sampleRotation = 0.0;
    public static int HEIGHT = 480, WIDTH = 640;

    //    public double rotation;
    @Override
    public Mat processFrame(Mat mat) {
        camera_matrix.put(0, 0, WIDTH, 0, WIDTH / 2.0, 0, HEIGHT, HEIGHT / 2.0, 0, 0, 1);
        Mat distortion_coefficients = new Mat(1, 5, CvType.CV_64FC1);
        distortion_coefficients.put(0, 0, kvs.val[0], kvs.val[1], 0, 0, kvs.val[2]);
/*
        // Rotation code for testing -- Hit CTRL + minus to fold it.
        Mat rotMat = Imgproc.getRotationMatrix2D(new Point(mat.width()/2, mat.height()/2), rotation, 1.0);
        Imgproc.warpAffine(mat, mat, rotMat, mat.size());
*/
        Mat newMat = mat.clone();
        Calib3d.undistort(mat, newMat, camera_matrix, distortion_coefficients);

        // Convert image to HSV for thresholding.
        Imgproc.cvtColor(newMat, cvt, Imgproc.COLOR_RGB2HSV);
        Core.inRange(cvt, SAMPLE_LOW, SAMPLE_HIGH, dst);
        Mat yellowMat = newMat.clone();
        Core.inRange(cvt, YELLOW_SAMPLE_LOW, YELLOW_SAMPLE_HIGH, yellowMat);
        Core.add(dst, yellowMat, dst);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2 * 3 + 1, 2 * 3 + 1),
                new Point(3, 3));
        // Dilation slightly increases the selected area.
        Imgproc.dilate(dst, dst, kernel);
        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(dst, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > VISION_MIN_AREA) {
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
            return (int) (distance(t0.center, center) - distance(t1.center, center));
        });
        // Do nothing if there's nothing on the screen
        if (boxCenters.isEmpty()) {
            sampleRotation = -70.0;
            centroid = null;
            return newMat;
            // The usage of two returns is not DeGennaro approved.
        }

        // Find the top line of the rectangle and get the angle from atan2 -- May not be the most accurate, but it works for my sample data.
        RotatedRect closest = boxCenters.get(0);

        Point[] points = new Point[4];
        closest.points(points);
        // Not the greatest to do because it doesn't need to be sorted -- max would be better
        List<Point> lpoints = Arrays.stream(points)
                                    .sorted((i2, i1) -> (int) (i2.y - i1.y))
                                    .collect(Collectors.toList());
        Point highestPoint = lpoints.get(0);

        lpoints = Arrays.stream(points)
                        .sorted((i1, i2) -> (int) (distance(i2, highestPoint) - distance(i1, highestPoint)))
                        .collect(Collectors.toList());
        Point min = (lpoints.get(1).x < highestPoint.x) ? highestPoint : lpoints.get(1);
        Point max = (min == highestPoint) ? lpoints.get(1) : highestPoint;
        double theta = Math.atan2(max.y - min.y, max.x - min.x);
        telemetry.addData("Theta", theta);
        telemetry.update();

        // Plot on mat.
//        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2RGB);
        Imgproc.polylines(newMat, List.of(new MatOfPoint(points)), true, new Scalar(0, 255, 0), 3);
        Imgproc.line(newMat, new Point(closest.center.x - 250 * Math.cos(theta), closest.center.y - 250 * Math.sin(theta)), new Point(closest.center.x + 250 * Math.cos(theta), closest.center.y + 250 * Math.sin(theta)), new Scalar(255, 0, 0));
        Imgproc.putText(newMat, ((double) Math.round(theta * 1000)) / 1000 + "rad", closest.center, 1, 1, new Scalar(0, 0, 255));
        sampleRotation = theta;
        centroid = closest.center;

        return newMat;

    }

    double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}
