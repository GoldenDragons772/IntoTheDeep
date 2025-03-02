package org.firstinspires.ftc.team772.Vision;

import org.opencv.core.*;
import java.util.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class RedSampleDetection extends OpenCvPipeline {

    public Scalar lowerRGBA = new Scalar(139.0, 0.0, 0.0, 0.0);
    public Scalar upperRGBA = new Scalar(220.0, 116.0, 176.0, 255.0);
    private Mat rgbaBinaryMat = new Mat();

    private ArrayList<MatOfPoint> contours = new ArrayList<>();
    private Mat hierarchy = new Mat();

    private MatOfPoint2f contours2f = new MatOfPoint2f();
    private ArrayList<RotatedRect> contoursRotRects = new ArrayList<>();

    public Scalar lineColor = new Scalar(0.0, 0.0, 0.0, 0.0);
    public int lineThickness = 20;

    private Mat inputRotRects = new Mat();

    public Scalar lineColor1 = new Scalar(0.0, 0.0, 0.0, 0.0);
    public int lineThickness1 = 10;

    private ArrayList<MatOfPoint> crosshair = new ArrayList<>();
    private Mat crosshairImage = new Mat();
    public int crosshairSize = 9;

    public double vectorX = 0.0;
    public double vectorY = 0.0;

    @Override
    public Mat processFrame(Mat input) {
        Core.inRange(input, lowerRGBA, upperRGBA, rgbaBinaryMat);

        contours.clear();
        hierarchy.release();
        Imgproc.findContours(rgbaBinaryMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        contoursRotRects.clear();
        for(MatOfPoint points : contours) {
            contours2f.release();
            points.convertTo(contours2f, CvType.CV_32F);

            contoursRotRects.add(Imgproc.minAreaRect(contours2f));
        }

        input.copyTo(inputRotRects);
        for(RotatedRect rect : contoursRotRects) {
            if(rect != null) {
                Point[] rectPoints = new Point[4];
                rect.points(rectPoints);
                MatOfPoint matOfPoint = new MatOfPoint(rectPoints);

                Imgproc.polylines(inputRotRects, Collections.singletonList(matOfPoint), true, lineColor, lineThickness);
            }
        }

        input.copyTo(crosshairImage);

        Point crosshairPoint = new Point((((double) (input.cols())) / 2) + vectorX, (((double) (input.rows())) / 2) + vectorY);
        int scaleFactor = (input.rows() + input.cols()) / 2;

        int adjustedCrosshairSize = (crosshairSize * scaleFactor) / 100;

        Imgproc.line(crosshairImage, new Point(crosshairPoint.x - adjustedCrosshairSize, crosshairPoint.y), new Point(crosshairPoint.x + adjustedCrosshairSize, crosshairPoint.y), lineColor1, lineThickness1);
        Imgproc.line(crosshairImage, new Point(crosshairPoint.x, crosshairPoint.y - adjustedCrosshairSize), new Point(crosshairPoint.x, crosshairPoint.y + adjustedCrosshairSize), lineColor1, lineThickness1);

        crosshair.clear();

        for(MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);

            if(boundingRect.contains(crosshairPoint)) {
                crosshair.add(contour);
            }
        }

        return inputRotRects;
    }
}


