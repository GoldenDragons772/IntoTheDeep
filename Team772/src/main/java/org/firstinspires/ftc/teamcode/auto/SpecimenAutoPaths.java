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

    private static final Pose grab1Pose = new Pose(6.5,18.000, Math.toRadians(180));
    private static final Pose grab2Pose = new Pose(8.0,16.000, Math.toRadians(180));
    private static final Pose grab3Pose = new Pose(8.0,14.000, Math.toRadians(180));
    private static final Pose grab4Pose = new Pose(8.0,12.000, Math.toRadians(180));

    private static final Pose spec1Pose = new Pose(44.000, 66.000, Math.toRadians(180));
    private static final Pose spec2Pose = new Pose(44.000, 64.000, Math.toRadians(180));
    private static final Pose spec3Pose = new Pose(44.000, 62.000, Math.toRadians(180));
    private static final Pose spec4Pose = new Pose(44.000, 60.000, Math.toRadians(180));

    private static final Pose parkPose = new Pose(8.500,10.000, Math.toRadians(180));


    public static PathChain preload() {
        return new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(startPose),
                            //new Point(preloadPose.getX() - 15, preloadPose.getY(), Point.CARTESIAN),
                            new Point(preloadPose)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(10)
            .build();
    }


    public static PathChain knockSpecsIntoZone() {
        return new PathBuilder()
            // line 1
            .addPath(
                    new BezierCurve(
                            new Point(40.000, 72.000, Point.CARTESIAN),
                            new Point(2.384, 31.548, Point.CARTESIAN),
                            new Point(57.768, 38.559, Point.CARTESIAN),
                            new Point(61.00, 22.00, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 2
            .addPath(
                    new BezierLine(
                            new Point(61.00, 22.00, Point.CARTESIAN),
                            new Point(24.00, 22.00, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 3
            .addPath(
                    new BezierCurve(
                            new Point(24.00, 22.00, Point.CARTESIAN),
                            new Point(48.093, 35.334, Point.CARTESIAN),
                            new Point(61.00, 12.00, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 4
            .addPath(
                    new BezierLine(
                            new Point(61.00, 12.00, Point.CARTESIAN),
                            new Point(24.00, 13.00, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 5
            .addPath(
                    new BezierCurve(
                            new Point(24.00, 13.00, Point.CARTESIAN),
                            new Point(45.850, 23.416, Point.CARTESIAN),
                            new Point(61.00, 5.00, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
        //line 6
            .addPath(
                    new BezierLine(
                            new Point(61.00, 5.000, Point.CARTESIAN),
                            new Point(24.00, 5.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
        // line 7
            .addPath(
                    new BezierCurve(
                            new Point(24.00, 5.000, Point.CARTESIAN),
                            new Point(25, 20.000, Point.CARTESIAN),
                            new Point(8.500, 20.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(0.7)
            .setPathEndTimeoutConstraint(1.2)
            .setPathEndTValueConstraint(0.9)
            .build();
    }


    public static PathChain spec1() {
        return new PathBuilder()

            .addPath(
                    // Line 1
                    new BezierLine(
                            new Point(grab1Pose),
                            //new Point(spec1Pose.getX() - 15, spec1Pose.getY(), Point.CARTESIAN),
                            new Point(spec1Pose)
                    )
            )
            .setConstantHeadingInterpolation(spec1Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(5)
            .setPathEndTimeoutConstraint(50)
            .build();
    }


    public static PathChain grab2() {
        return new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(spec1Pose),
                            new Point(grab2Pose.getX() + 15, grab2Pose.getY(), Point.CARTESIAN),
                            new Point(grab2Pose)
                    )
            )
            .setConstantHeadingInterpolation(grab2Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(0.7)
            .build();
    }


    public static PathChain spec2() {
        return new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(grab2Pose),
                           // new Point(spec2Pose.getX() - 15, spec2Pose.getY(), Point.CARTESIAN),
                            new Point(spec2Pose)
                    )
            )
            .setConstantHeadingInterpolation(spec2Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(5)
            .setPathEndTimeoutConstraint(50)
            .build();
    }


    public static PathChain grab3() {
        return new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(spec2Pose),
                            new Point(grab3Pose.getX() + 15, grab3Pose.getY(), Point.CARTESIAN),
                            new Point(grab3Pose)
                    )
            )
            .setConstantHeadingInterpolation(grab3Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(0.7)
            .build();
    }


    public static PathChain spec3() {
        return new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(grab3Pose),
                           // new Point(spec3Pose.getX() - 15, spec3Pose.getY(), Point.CARTESIAN),
                            new Point(spec3Pose)
                    )
            )
            .setConstantHeadingInterpolation(spec3Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(5)
            .setPathEndTimeoutConstraint(50)
            .build();
    }


    public static PathChain grab4() {
        return new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(spec3Pose),
                            new Point(grab4Pose.getX() + 15, grab4Pose.getY(), Point.CARTESIAN),
                            new Point(grab4Pose)
                    )
            )
            .setConstantHeadingInterpolation(grab4Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(0.7)
            .build();
    }


    public static PathChain spec4() {
        return new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(grab4Pose),
                            //new Point(spec4Pose.getX() - 15, spec4Pose.getY(), Point.CARTESIAN),
                            new Point(spec4Pose)
                    )
            )
            .setConstantHeadingInterpolation(spec4Pose.getHeading())
            .setZeroPowerAccelerationMultiplier(5)
            .setPathEndTimeoutConstraint(50)
            .build();
    }


    public static PathChain park() {
        return new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(spec4Pose),
                            new Point(parkPose)
                    )
            )
            .setConstantHeadingInterpolation(parkPose.getHeading())
            .setZeroPowerAccelerationMultiplier(7.0)
            .build();
    }


//    public static PathChain goToChamberFromZone = new PathBuilder()
//            .addPath(
//                    new BezierCurve(
//                            new Point(8.000, 20.000, Point.CARTESIAN),
//                            new Point(15.621, 68.000, Point.CARTESIAN),
//                            new Point(45.000, 68.000, Point.CARTESIAN) //Add More X-val?
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .setZeroPowerAccelerationMultiplier(2.5)
//            .build();
//
//    public static PathChain goToZoneFromChamber = new PathBuilder()
//            .addPath(
//                    new BezierCurve(
//                            new Point(45.000, 68.000, Point.CARTESIAN),
//                            new Point(15.621, 69.302, Point.CARTESIAN),
//                            new Point(21.593, 20.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .setZeroPowerAccelerationMultiplier(2.5)
//            .build();
//
//    public static PathChain pickSpecimenPreloadPath2 = new PathBuilder()
//            .addPath(
//                    // Line 123.574
//                    new BezierLine(
//                            new Point(21.593, 20.000, Point.CARTESIAN),
//                            new Point(6.8, 20.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .setZeroPowerAccelerationMultiplier(3)
//            .setPathEndTimeoutConstraint(50)
//            .build();
//
//    public static PathChain goToChamberFromZone2 = new PathBuilder()
//            .addPath(
//                    new BezierCurve(
//                            new Point(8.000, 20.000, Point.CARTESIAN),
//                            new Point(30.000, 66.000, Point.CARTESIAN),
//                            new Point(45.000, 66.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .setZeroPowerAccelerationMultiplier(2.5)
//            .build();
//
//    public static PathChain goToChamberFromZone3 = new PathBuilder()
//            .addPath(
//                    new BezierCurve(
//                            new Point(8.000, 20.000, Point.CARTESIAN),
//                            new Point(30.000, 64.000, Point.CARTESIAN),
//                            new Point(45.000, 64.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .setZeroPowerAccelerationMultiplier(2.5)
//            .build();
//
//    public static PathChain pickSpecimenPreloadPath3 = new PathBuilder()
//            .addPath(
//                    // Line 1
//                    new BezierLine(
//                            new Point(21.593, 20.000, Point.CARTESIAN),
//                            new Point(6.850, 20.000, Point.CARTESIAN)
//                    )
//
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .setZeroPowerAccelerationMultiplier(3)
//            .setPathEndTimeoutConstraint(50)
//            .build();
//
//    public static PathChain pickSpecimenPreloadPath4 = new PathBuilder()
//            .addPath(
//                    // Line 1
//                    new BezierLine(
//                            new Point(21.593, 20.000, Point.CARTESIAN),
////                            new Point(8, 20.000, Point.CARTESIAN)
//                            new Point(10, 20.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .build();
//
//    public static PathChain goToChamberFromZone4 = new PathBuilder()
//            .addPath(
//                    new BezierCurve(
//                            new Point(8.000, 20.000, Point.CARTESIAN),
//                            new Point(30.000, 72.000, Point.CARTESIAN),
//                            new Point(45.000, 72.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .build();
//
//    public static PathChain spaceSpecsPath = new PathBuilder()
//            .addPath(
//                    new BezierLine(
//                            new Point(38.000, 68.000, Point.CARTESIAN),
//                            new Point(45.000, 74.000, Point.CARTESIAN)
//                    )
//            )
//            .setConstantHeadingInterpolation(Math.toRadians(180))
//            .build();


}
