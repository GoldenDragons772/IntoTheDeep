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
    public static Scalar BLUE_SAMPLE_LOW = new Scalar(107, 90, 15);
    public static Scalar BLUE_SAMPLE_HIGH = new Scalar(125, 255, 255);
    public static Scalar RECTANGLE_BOUNDS = new Scalar(80, 80, 460, 320);
    // 640

    public static double VISION_MIN_AREA = 2000;
    public static boolean useDst = false;
    public static boolean DEBUG = true;
    public static Scalar kvs = new Scalar(-1.382, 2.25, -1.5);
    public Scalar SAMPLE_LOW;
    public Scalar SAMPLE_HIGH;
    public Point centroid;
    private final boolean isRed;

    public SampleDetection(Telemetry tel) {
        this(tel, false);
    }

    public SampleDetection(Telemetry tel, boolean isRed) {
        this.telemetry = tel;
        this.isRed = isRed;
    }

    Mat garbage = new Mat(), dst = new Mat(), cvt = new Mat(), yellow = new Mat();
    Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2 * 3 + 1, 2 * 3 + 1),
            new Point(3, 3));
    Mat camera_matrix = new Mat(3, 3, CvType.CV_64FC1);
    Mat distortion_coefficients = new Mat(1, 5, CvType.CV_64FC1);
    public double sampleRotation = 0.0;
    public static int HEIGHT = 480, WIDTH = 640;

    //    public double rotation;
    @Override
    public Mat processFrame(Mat mat) {
        useDst = DEBUG && useDst;
        if (isRed) {
            SAMPLE_LOW = RED_SAMPLE_LOW;
            SAMPLE_HIGH = RED_SAMPLE_HIGH;
        } else {
            SAMPLE_LOW = BLUE_SAMPLE_LOW;
            SAMPLE_HIGH = BLUE_SAMPLE_HIGH;
        }


        camera_matrix.put(0, 0, WIDTH, 0, WIDTH / 2.0, 0, HEIGHT, HEIGHT / 2.0, 0, 0, 1);
        distortion_coefficients.put(0, 0, kvs.val[0], kvs.val[1], 0, 0, kvs.val[2]);
        Rect rectangle = new Rect(RECTANGLE_BOUNDS.val);
        if (!DEBUG) {
            Calib3d.undistort(mat, garbage, camera_matrix, distortion_coefficients);
            garbage.copyTo(mat);
            mat = mat.submat(rectangle);
        }
        else {
            Imgproc.rectangle(mat, rectangle, new Scalar(255,0,0));
        }
//        mat = mat.submat(new Rect());


        // Convert image to HSV for thresholding.
        Imgproc.cvtColor(mat, cvt, Imgproc.COLOR_RGB2HSV);
        Core.inRange(cvt, SAMPLE_LOW, SAMPLE_HIGH, dst);
//        mat.copyTo(yellow);
        Core.inRange(cvt, YELLOW_SAMPLE_LOW, YELLOW_SAMPLE_HIGH, yellow);

        List<MatOfPoint> filteredContours = findSamples(dst);
        filteredContours.addAll(findSamples(yellow));

        if (useDst) {
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2RGB);
            Imgproc.cvtColor(yellow, yellow, Imgproc.COLOR_GRAY2RGB);
            Core.compare(yellow, new Scalar(255, 255, 255), garbage, Core.CMP_EQ);
            yellow.setTo(new Scalar(255, 255, 0), yellow);
            Core.compare(dst, new Scalar(255, 255, 255), garbage, Core.CMP_EQ);
            dst.setTo((isRed) ? new Scalar(255, 0, 0) : new Scalar(0, 0, 255), garbage);
            Core.add(yellow, dst, dst);

//            Core.add(dst,yellow, dst);
            dst.copyTo(mat);
        }
        if (DEBUG) {
            Imgproc.drawContours(mat, filteredContours, -1, new Scalar(255, 0, 0), 5);
        }

        List<RotatedRect> boxCenters = new ArrayList<>();

        for (MatOfPoint i : filteredContours) {
            // Create the rectangles and plot them onto the rects mat.
            RotatedRect rrect = Imgproc.minAreaRect(new MatOfPoint2f(i.toArray()));
            boxCenters.add(rrect);
        }
        Point center = new Point((double) dst.width() / 2, (double) dst.height() / 2);

        // Sort the box centers by the distance to the center of the image.
        boxCenters.sort((t0, t1) -> (int) (distance(t0.center, center) - distance(t1.center, center)));
        if (DEBUG) {
            for (RotatedRect box : boxCenters) {
                Imgproc.putText(mat, String.format("%.2f", distance(box.center, center)), new Point(box.center.x, box.center.y + 10), 1, 1, new Scalar(0, 255, 0));
            }

            Imgproc.circle(mat, center, 5, new Scalar(0, 255, 255), 5);
        }
        // Do nothing if there's nothing on the screen
        if (boxCenters.isEmpty()) {
            sampleRotation = -70.0; // Generic value we can check for
            centroid = null;
//            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);
            return mat;
            // The usage of two returns is not DeGennaro approved.
        }

        // Find the top line of the rectangle and get the angle from atan2 -- May not be the most accurate, but it works for my sample data.
        RotatedRect closest = boxCenters.get(0);

        Point[] points = new Point[4];
        if (DEBUG) {
            for (RotatedRect box :
                    boxCenters) {
                box.points(points);
                Imgproc.polylines(mat, List.of(new MatOfPoint(points)), true, new Scalar(255, 0, 255), 2);
            }
        }
        closest.points(points);

        List<Point> lpoints = Arrays.stream(points).collect(Collectors.toList());
        Point highestPoint = lpoints.stream().max((i2, i1) -> (int) (i2.y - i1.y)).get();
        lpoints.remove(highestPoint);

        Point secondHighest = lpoints.stream()
                                     .max((i1, i2) -> (int) (distance(i2, highestPoint) - distance(i1, highestPoint)))
                                     .get();

        Point min = (secondHighest.x < highestPoint.x) ? highestPoint : secondHighest;
        Point max = (min == highestPoint) ? secondHighest : highestPoint;
        double theta = Math.atan2(max.y - min.y, max.x - min.x) + Math.PI / 2;

        // Plot on mat.
        if (DEBUG) {
            Imgproc.polylines(mat, List.of(new MatOfPoint(points)), true, new Scalar(0, 255, 0), 3);
            Imgproc.putText(mat, ((double) Math.round(theta * 1000)) / 1000 + "rad", closest.center, 1, 1, new Scalar(0, 0, 255));
        }
        Imgproc.line(mat, new Point(closest.center.x - 250 * Math.cos(theta), closest.center.y - 250 * Math.sin(theta)), new Point(closest.center.x + 250 * Math.cos(theta), closest.center.y + 250 * Math.sin(theta)), new Scalar(255, 0, 255), 3);
        sampleRotation = theta;
        centroid = closest.center;

        return mat;


    }

    double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    List<MatOfPoint> findSamples(Mat thresholdedMatrix) {
        Imgproc.medianBlur(thresholdedMatrix, thresholdedMatrix, 3);
        Imgproc.erode(thresholdedMatrix, thresholdedMatrix, kernel, new Point(-1, -1), 9);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdedMatrix, contours, garbage, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > VISION_MIN_AREA) {
                filteredContours.add(contour);
            }
        }
        return filteredContours;
    }
}
