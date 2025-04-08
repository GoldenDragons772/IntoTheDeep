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

    private static final Pose startPose = new Pose(8.50, 53.500, Math.toRadians(180));
    private static final Pose preloadPose = new Pose(42.0, 72.000, Math.toRadians(180));
    private static final Pose grab1Pose = new Pose(8.50,20.000, Math.toRadians(180));
    private static final Pose grab2Pose = new Pose(0.000,20.000, Math.toRadians(180));
    private static final Pose grab3Pose = new Pose(8.000,20.000, Math.toRadians(180));
    private static final Pose grab4Pose = new Pose(8.000,20.000, Math.toRadians(180));
    private static final Pose spec1Pose = new Pose(45.000, 66.000, Math.toRadians(180));
    private static final Pose spec2Pose = new Pose(45.000, 66.000, Math.toRadians(180));
    private static final Pose spec3Pose = new Pose(45.000, 66.000, Math.toRadians(180));
    private static final Pose spec4Pose = new Pose(45.000, 66.000, Math.toRadians(180));
    private static final Pose parkPose = new Pose(8.000,10.000, Math.toRadians(180));

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
            .setZeroPowerAccelerationMultiplier(0.3)
                .setPathEndVelocityConstraint(2.5)
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
                                new Point(60.000, 18.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))


                //Line 4
                .addPath(
                        new BezierLine(
                                new Point(60.000, 18.000, Point.CARTESIAN),
                                new Point(28.000, 18.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))


                //Line 5
                .addPath(
                        new BezierCurve(
                                new Point(28.000, 18.000, Point.CARTESIAN),
                                new Point(56.000, 10.000, Point.CARTESIAN),
                                new Point(60.000, 8.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))


                //Line 6
                .addPath(
                        new BezierLine(
                                new Point(60.000, 8.000, Point.CARTESIAN),
                                new Point(26.000, 8.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))

                //Line 7
                .addPath(
                        new BezierCurve(
                                new Point(26.000, 8.000, Point.CARTESIAN),
                                new Point(36.000, 20.000, Point.CARTESIAN),
                                new Point(grab1Pose)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .setZeroPowerAccelerationMultiplier(0.3)
                .setPathEndVelocityConstraint(2.0)
                .build();
    }

    public static PathChain spec1() {
        return new PathBuilder()
            .addPath(
                // Line 1
                new BezierCurve(
                    new Point(grab1Pose),
                    new Point(spec1Pose.getX() - 15, spec1Pose.getY(), Point.CARTESIAN),
                    new Point(spec1Pose)
                )
            )
            .setConstantHeadingInterpolation(spec1Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(6.5)
            .build();
    }


    public static PathChain grab2() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(spec1Pose),
                    new Point(grab2Pose)
                )
            )
            .setConstantHeadingInterpolation(grab2Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(1.5)
            .build();
    }


    public static PathChain spec2() {
        return new PathBuilder()
            .addPath(
                new BezierCurve(
                    new Point(grab2Pose),
                    new Point(spec2Pose.getX() - 15, spec2Pose.getY(), Point.CARTESIAN),
                    new Point(spec2Pose)
                )
            )
            .setConstantHeadingInterpolation(spec2Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(7)
            .setPathEndTimeoutConstraint(50)
            .build();
    }


    public static PathChain grab3() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(spec2Pose),
                    new Point(grab3Pose)
                )
            )
            .setConstantHeadingInterpolation(grab3Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();
    }


    public static PathChain spec3() {
        return new PathBuilder()
            .addPath(
                new BezierCurve(
                    new Point(grab3Pose),
                    new Point(spec3Pose.getX() - 15, spec3Pose.getY(), Point.CARTESIAN),
                    new Point(spec3Pose)
                )
            )
            .setConstantHeadingInterpolation(spec3Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(7)
            .setPathEndTimeoutConstraint(50)
            .build();
    }


    public static PathChain grab4() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(spec3Pose),
                    new Point(grab4Pose)
                )
            )
            .setConstantHeadingInterpolation(grab4Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();
    }


    public static PathChain spec4() {
        return new PathBuilder()
            .addPath(
                new BezierCurve(
                    new Point(grab4Pose),
                    new Point(spec4Pose.getX() - 15, spec4Pose.getY(), Point.CARTESIAN),
                    new Point(spec4Pose)
                )
            )
            .setConstantHeadingInterpolation(spec4Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(7)
            .build();
    }


    public static PathChain park() {
        return new PathBuilder()
            .addPath(
                new BezierCurve(
                    new Point(spec4Pose),
                    new Point(parkPose)
                )
            )
            .setConstantHeadingInterpolation(parkPose.getHeading())
            .setZeroPowerAccelerationMultiplier(7)
            .build();
    }
}
