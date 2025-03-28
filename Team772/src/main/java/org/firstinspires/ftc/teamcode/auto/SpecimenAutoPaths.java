package org.firstinspires.ftc.teamcode.auto;

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

    public static PathChain scoreFirstSpecimenPath = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierCurve(
                            new Point(8.500, 53.500, Point.CARTESIAN),
                            new Point(16.189, 73.278, Point.CARTESIAN),
                            new Point(40.000, 72.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();

    public static PathChain knockSpecsIntoZone = new PathBuilder()
            // line 1
            .addPath(
                    new BezierCurve(
                            new Point(40.000, 72.000, Point.CARTESIAN),
                            new Point(2.384, 31.548, Point.CARTESIAN),
                            new Point(57.768, 38.559, Point.CARTESIAN),
                            new Point(61.133, 22.855, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 2
            .addPath(
                    new BezierLine(
                            new Point(61.133, 22.855, Point.CARTESIAN),
                            new Point(24.142, 23.135, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 3
            .addPath(
                    new BezierCurve(
                            new Point(19.630, 23.135, Point.CARTESIAN),
                            new Point(48.093, 35.334, Point.CARTESIAN),
                            new Point(57.488, 12.619, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 4
            .addPath(
                    new BezierLine(
                            new Point(57.488, 12.619, Point.CARTESIAN),
                            new Point(24.142, 13.633, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 5
            .addPath(
                    new BezierCurve(
                            new Point(24.142, 13.633, Point.CARTESIAN),
                            new Point(45.850, 23.416, Point.CARTESIAN),
                            new Point(58.329, 5.6, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            //line 6
            .addPath(
                    new BezierLine(
                            new Point(58.610, 5.600, Point.CARTESIAN),
                            new Point(24.117, 5.600, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 7
            .addPath(
                    new BezierLine(
                            new Point(24.117, 5.6, Point.CARTESIAN),
                            new Point(21.593, 20.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain pickSpecimenPreloadPath = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierLine(
                            new Point(21.593, 20.000, Point.CARTESIAN),
                            new Point(6.80, 20.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(2.5)
            .setPathEndTimeoutConstraint(50)
            .build();

    public static PathChain pickSpecimenPreloadPath2 = new PathBuilder()
            .addPath(
                    // Line 123.574
                    new BezierLine(
                            new Point(21.593, 20.000, Point.CARTESIAN),
                            new Point(6.8, 20.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(3)
            .setPathEndTimeoutConstraint(50)
            .build();

    public static PathChain pickSpecimenPreloadPath3 = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierLine(
                            new Point(21.593, 20.000, Point.CARTESIAN),
                            new Point(6.850, 20.000, Point.CARTESIAN)
                    )

            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(3)
            .setPathEndTimeoutConstraint(50)
            .build();

    public static PathChain pickSpecimenPreloadPath4 = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierLine(
                            new Point(21.593, 20.000, Point.CARTESIAN),
//                            new Point(8, 20.000, Point.CARTESIAN)
                            new Point(10, 20.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain goToChamberFromZone = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(8.000, 20.000, Point.CARTESIAN),
                            new Point(15.621, 69.302, Point.CARTESIAN),
                            new Point(40.000, 66.000, Point.CARTESIAN) //Add More X-val?
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();


    public static PathChain goToChamberFromZone2 = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(8.000, 20.000, Point.CARTESIAN),
                            new Point(30.000, 68.000, Point.CARTESIAN),
                            new Point(40.000, 68.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();

    public static PathChain goToChamberFromZone3 = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(8.000, 20.000, Point.CARTESIAN),
                            new Point(30.000, 70.000, Point.CARTESIAN),
                            new Point(40.000, 70.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();

    public static PathChain goToChamberFromZone4 = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(8.000, 20.000, Point.CARTESIAN),
                            new Point(30.000, 72.000, Point.CARTESIAN),
                            new Point(40.000, 72.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain spaceSpecsPath = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(38.000, 68.000, Point.CARTESIAN),
                            new Point(40.000, 74.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain goToZoneFromChamber = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(40.000, 68.000, Point.CARTESIAN),
                            new Point(15.621, 69.302, Point.CARTESIAN),
                            new Point(21.593, 20.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

}
