package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

/**
 * Easier to write in Java because of the
 * <a href="https://pedro-path-generator.vercel.app/">WATT's UP generator.</a>
 */
public class SpecimenAutoPaths {

    private static final Pose startPose = new Pose(7.4, 66.500, Math.toRadians(180)); //Original 53.5 55.5?
    private static final Pose preloadPose = new Pose(40.0, 74.000, Math.toRadians(180));
    private static final Pose grab1Pose = new Pose(7.4, 25.000, Math.toRadians(180)); //This pose looks really promising.
    public static final Pose grab2Pose = new Pose(13.0, 25.000, Math.toRadians(180));
    private static final Pose grab3Pose = new Pose(9.5, 25.000, Math.toRadians(180));
    private static final Pose grab4Pose = new Pose(9.5, 25.000, Math.toRadians(180));
    public static final Pose specPose = new Pose(43.000, 76.000, Math.toRadians(180));
    private static final Pose parkPose = new Pose(8.000, 10.000, Math.toRadians(180));

    public static PathChain preload() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(startPose),
                                new Point(preloadPose.getX() - 15, preloadPose.getY(), Point.CARTESIAN),
                                new Point(preloadPose)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .setZeroPowerAccelerationMultiplier(4)
                .build();
    }


    public static PathChain knockSpecsIntoZone() {
        return new PathBuilder()
                // line 1
                .addPath(
                        new BezierCurve(
                                new Point(preloadPose),
                                new Point(15.000, 36.000, Point.CARTESIAN),
                                new Point(15.000, 36.250, Point.CARTESIAN),
                                new Point(60.000, 45.000, Point.CARTESIAN),
                                new Point(62.000, 24.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))

                //Line 2
                .addPath(
                        new BezierLine(
                                new Point(62.000, 24.000, Point.CARTESIAN),
                                new Point(28.000, 24.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))


                //Line 3
                .addPath(
                        new BezierCurve(
                                new Point(28.000, 24.000, Point.CARTESIAN),
                                new Point(52.000, 24.000, Point.CARTESIAN),
                                new Point(62.000, 18.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))

                //Line 4
                .addPath(
                        new BezierLine(
                                new Point(62.000, 16.000, Point.CARTESIAN),
                                new Point(28.000, 16.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))

                //Line 5
                .addPath(
                        new BezierCurve(
                                new Point(28.000, 16.000, Point.CARTESIAN),
                                new Point(56.000, 10.000, Point.CARTESIAN),
                                new Point(62.000, 8.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))

                //Line 6
                .addPath(
                        new BezierLine(
                                new Point(62.000, 8.000, Point.CARTESIAN),
                                new Point(26.000, 8.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))

                //Line 7
//                .addPath(
//                        new BezierCurve(
//                                new Point(26.000, 8.000, Point.CARTESIAN),
//                                new Point(36.000, 20.000, Point.CARTESIAN),
//                                new Point(grab1Pose)
//                        )
//                )
//                .setConstantHeadingInterpolation(Math.toRadians(180))
                .setZeroPowerAccelerationMultiplier(7)
                .setPathEndTimeoutConstraint(50)
                .build();
    }

    public static PathChain grab1() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(26.000, 8.000, Point.CARTESIAN),
                                new Point(grab1Pose.getX() + 30, grab1Pose.getY(), Point.CARTESIAN),
                                new Point(grab1Pose)
                        )
                )
                .setConstantHeadingInterpolation(grab1Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2)
                .setPathEndTimeoutConstraint(75)
                .build();
    }

    public static PathChain lineGrab1() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(grab1Pose),
                                new Point(grab1Pose.getX() - 12, grab1Pose.getY(), Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(grab1Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2.5)
                .setPathEndTimeoutConstraint(75)
                .build();
    }

    public static PathChain spec1() {
        return new PathBuilder()
                .addPath(
                        // Line 1
                        new BezierLine(
                                new Point(grab1Pose),
                                //new Point(specPose.getX() - 15, specPose.getY(), Point.CARTESIAN),
                                new Point(specPose)
                        )
                )
                .setConstantHeadingInterpolation(specPose.getHeading())
                .setZeroPowerAccelerationMultiplier(5)
                .build();
    }


    public static PathChain grab2() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(specPose),
                                new Point(specPose.getX() - 30, specPose.getY(), Point.CARTESIAN),
                                new Point(grab2Pose.getX() + 35, grab2Pose.getY(), Point.CARTESIAN),
                                new Point(grab2Pose)
                        )
                )
                .setConstantHeadingInterpolation(grab2Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2)
                .setPathEndTimeoutConstraint(100)
                .build();
    }

    public static PathChain lineGrab2() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(grab2Pose),
                                new Point(grab2Pose.getX() - 12, grab2Pose.getY(), Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(grab2Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2.5)
                .setPathEndTimeoutConstraint(75)
                .build();
    }


    public static PathChain spec2() {
        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(grab2Pose),
                                //new Point(specPose.getX() - 15, specPose.getY(), Point.CARTESIAN),
                                new Point(specPose)
                        )
                )
                .setConstantHeadingInterpolation(specPose.getHeading())
                .setZeroPowerAccelerationMultiplier(5)
                .setPathEndTimeoutConstraint(100)
                .build();
    }


    public static PathChain grab3() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(specPose),
                                new Point(specPose.getX() - 30, specPose.getY(), Point.CARTESIAN),
                                new Point(grab3Pose.getX() + 35, grab3Pose.getY(), Point.CARTESIAN),
                                new Point(grab3Pose)
                        )
                )
                .setConstantHeadingInterpolation(grab3Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2)
                .setPathEndTimeoutConstraint(100)
                .build();
    }

    public static PathChain lineGrab3() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(grab3Pose),
                                new Point(grab3Pose.getX() - 12, grab3Pose.getY(), Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(grab4Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2.5)
                .setPathEndTimeoutConstraint(75)
                .build();
    }


    public static PathChain spec3() {
        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(grab3Pose),
                                //new Point(specPose.getX() - 15, specPose.getY(), Point.CARTESIAN),
                                new Point(specPose)
                        )
                )
                .setConstantHeadingInterpolation(specPose.getHeading())
                .setZeroPowerAccelerationMultiplier(5)
                .build();
    }


    public static PathChain grab4() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(specPose),
                                new Point(specPose.getX() - 30, specPose.getY(), Point.CARTESIAN),
                                new Point(grab4Pose.getX() + 35, grab4Pose.getY(), Point.CARTESIAN),
                                new Point(grab4Pose)
                        )
                )
                .setConstantHeadingInterpolation(grab4Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2)
                .setPathEndTimeoutConstraint(100)
                .build();
    }

    public static PathChain lineGrab4() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(grab4Pose),
                                new Point(grab4Pose.getX() - 12, grab4Pose.getY(), Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(grab4Pose.getHeading())
                .setZeroPowerAccelerationMultiplier(2.5)
                .setPathEndTimeoutConstraint(75)
                .build();
    }


    public static PathChain spec4() {
        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(grab4Pose),
                                //new Point(specPose.getX() - 15, specPose.getY(), Point.CARTESIAN),
                                new Point(specPose)
                        )
                )
                .setConstantHeadingInterpolation(specPose.getHeading())
                .setZeroPowerAccelerationMultiplier(5)
                .build();
    }


    public static PathChain park() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(specPose),
                                new Point(parkPose)
                        )
                )
                .setConstantHeadingInterpolation(parkPose.getHeading())
                .setZeroPowerAccelerationMultiplier(7)
                .build();
    }
}
