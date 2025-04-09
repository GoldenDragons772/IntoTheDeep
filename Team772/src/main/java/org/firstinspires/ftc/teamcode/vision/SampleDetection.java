package org.firstinspires.ftc.teamcode.vision;

//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

//import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
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
    public static Scalar YELLOW_SAMPLE_LOW = new Scalar(15, 90, 90);
    public static Scalar YELLOW_SAMPLE_HIGH = new Scalar(25, 255, 255);
    public static Scalar BLUE_SAMPLE_LOW = new Scalar(110, 90, 15);
    public static Scalar BLUE_SAMPLE_HIGH = new Scalar(125, 255, 255);
    public static double VISION_MIN_AREA = 20000;
    public static boolean useDst = false;
    public static Scalar kvs = new Scalar(-1.382, 2.25, -1.5);
    public Scalar SAMPLE_LOW;
    public Scalar SAMPLE_HIGH;
    public Point centroid;
    private final boolean isRed;

    public SampleDetection(Telemetry tel) {
        this(tel, true);
    }

    public SampleDetection(Telemetry tel, boolean isRed) {
        this.telemetry = tel;
        this.isRed = isRed;
    }

    Mat distance = new Mat(), dst = new Mat(), cvt = new Mat(), yellow = new Mat();
    Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3 + 1, 3 + 1),
            new Point(3, 3));
    Mat undistortedMat = new Mat();
    Mat camera_matrix = new Mat(3, 3, CvType.CV_64FC1);
    Mat distortion_coefficients = new Mat(1, 5, CvType.CV_64FC1);
    Mat specimenMask = new Mat(), markers = new Mat(), unknown = new Mat(), zeros = Mat.zeros(WIDTH, HEIGHT, CvType.CV_8UC1);
    public double sampleRotation = 0.0;
    public static int HEIGHT = 480, WIDTH = 640;

    //    public double rotation;
    @Override
    public Mat processFrame(Mat mat) {
        if (isRed) {
            SAMPLE_LOW = RED_SAMPLE_LOW;
            SAMPLE_HIGH = RED_SAMPLE_HIGH;
        } else {
            SAMPLE_LOW = BLUE_SAMPLE_LOW;
            SAMPLE_HIGH = BLUE_SAMPLE_HIGH;
        }
        camera_matrix.put(0, 0, WIDTH, 0, WIDTH / 2.0, 0, HEIGHT, HEIGHT / 2.0, 0, 0, 1);
        distortion_coefficients.put(0, 0, kvs.val[0], kvs.val[1], 0, 0, kvs.val[2]);
/*
        // Rotation code for testing -- Hit CTRL + minus to fold it.
        Mat rotMat = Imgproc.getRotationMatrix2D(new Point(mat.width()/2, mat.height()/2), rotation, 1.0);
        Imgproc.warpAffine(mat, mat, rotMat, mat.size());
*/
        mat.copyTo(undistortedMat);
//        Calib3d.undistort(mat, undistortedMat, camera_matrix, distortion_coefficients);

        // Convert image to HSV for thresholding.
        Imgproc.cvtColor(undistortedMat, cvt, Imgproc.COLOR_RGB2HSV);
        Core.inRange(cvt, SAMPLE_LOW, SAMPLE_HIGH, dst);
        undistortedMat.copyTo(yellow);
        Core.inRange(cvt, YELLOW_SAMPLE_LOW, YELLOW_SAMPLE_HIGH, yellow);

        // Store the center coordinates and a reference to the rectangle.
        List<MatOfPoint> filteredContours = new ArrayList<>();
        filteredContours.addAll(findSamples(dst));
        filteredContours.addAll(findSamples(yellow));
        List<RotatedRect> boxCenters = new ArrayList<>();

        for (MatOfPoint i : filteredContours) {
            // Create the rectangles and plot them onto the rects mat.
            RotatedRect rrect = Imgproc.minAreaRect(new MatOfPoint2f(i.toArray()));
            boxCenters.add(rrect);
        }
        Point center = new Point((double) dst.width() / 2, (double) dst.height() / 2);

        // Sort the box centers by the distance to the center of the image.
        boxCenters.sort((t0, t1) -> (int) (distance(t0.center, center) - distance(t1.center, center)));
        for (RotatedRect box : boxCenters) {
            Imgproc.putText(undistortedMat, String.format("%.2f", distance(box.center, center)), new Point(box.center.x, box.center.y + 10), 1, 1, new Scalar(0, 255, 0));
        }
        Imgproc.circle(undistortedMat, center, 5, new Scalar(0, 255, 255));
        // Do nothing if there's nothing on the screen
        if (boxCenters.isEmpty()) {
            sampleRotation = -70.0;
            centroid = null;
            return undistortedMat;
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

        // Plot on mat.
        Imgproc.polylines(undistortedMat, List.of(new MatOfPoint(points)), true, new Scalar(0, 255, 0), 3);
        Imgproc.line(undistortedMat, new Point(closest.center.x - 250 * Math.cos(theta), closest.center.y - 250 * Math.sin(theta)), new Point(closest.center.x + 250 * Math.cos(theta), closest.center.y + 250 * Math.sin(theta)), new Scalar(255, 0, 255));
        Imgproc.putText(undistortedMat, ((double) Math.round(theta * 1000)) / 1000 + "rad", closest.center, 1, 1, new Scalar(0, 0, 255));
        sampleRotation = theta;
        centroid = closest.center;

        if (!useDst) {
            return undistortedMat;
        } else {
            return dst;
        }


    }

    double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    List<MatOfPoint> findSamples(Mat thresholdedMatrix) {
        long jim = System.currentTimeMillis();
        long currentTime = 0;

        Imgproc.dilate(thresholdedMatrix, thresholdedMatrix, kernel, new Point(-1, -1), 1);
        Imgproc.distanceTransform(thresholdedMatrix, distance, Imgproc.DIST_L2, 5);
        Core.normalize(distance, distance, 0, 255, Core.NORM_MINMAX);
        Imgproc.threshold(this.distance, distance, 200, 255, Imgproc.THRESH_BINARY);
        distance.convertTo(distance, CvType.CV_8U);
//        telemetry.addData("time 1", System.currentTimeMillis() - jim);

        // Unknown region
        Core.subtract(thresholdedMatrix, distance, unknown);
// Create markers for watershed
        int nLabels = Imgproc.connectedComponents(distance, markers);
//        telemetry.addData("labels", nLabels);
//        telemetry.update();
        Core.add(markers, Scalar.all(1), markers);


        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                if (unknown.get(i, j)[0] == 255) {
                    markers.put(i, j, 0);
                }
            }
        }
        Imgproc.cvtColor(undistortedMat, undistortedMat, Imgproc.COLOR_RGB2BGR);
        Imgproc.watershed(undistortedMat, markers);
        // Visualization - draw boundaries on original image

        // Find contours
        List<MatOfPoint> filteredContours = new ArrayList<>();
        telemetry.addData("time 2", currentTime);
        telemetry.addData("segment total", nLabels);
        long totalTime = System.currentTimeMillis();
        List<Long> times = new ArrayList<>();
        for (int b = 2; b < nLabels + 1; b++) {

            specimenMask = Mat.zeros(thresholdedMatrix.size(), CvType.CV_8UC1);
            currentTime = System.currentTimeMillis() - jim;
            Mat wshed = Mat.ones(markers.size(), CvType.CV_8UC3);
            Core.compare(markers, new Scalar(b), specimenMask, Core.CMP_EQ);
            specimenMask.convertTo(specimenMask, CvType.CV_8U, 1.0/255.0);

            thresholdedMatrix.copyTo(wshed, specimenMask);

            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(wshed, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            for (MatOfPoint contour : contours) {
                if (Imgproc.contourArea(contour) > VISION_MIN_AREA) {
                    filteredContours.add(contour);
                }
            }
            currentTime = (System.currentTimeMillis() - jim) - currentTime;
            times.add(currentTime);
        }
        telemetry.addData("time sum", System.currentTimeMillis() - totalTime);
        for (int i = 0; i< times.size(); i++) {
            telemetry.addData("time" + i, currentTime);
        }
        telemetry.update();
        return filteredContours;
    }
}
