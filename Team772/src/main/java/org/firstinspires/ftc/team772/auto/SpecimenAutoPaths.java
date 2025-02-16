package org.firstinspires.ftc.team772.auto;

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
                new BezierCurve(
                        new Point(7.100, 53.500, Point.CARTESIAN),
                        new Point(16.189, 73.278, Point.CARTESIAN),
                        new Point(40.000, 72.000, Point.CARTESIAN)
                )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
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
                            new Point(19.630, 23.135, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 3
            .addPath(
                    new BezierCurve(
                            new Point(19.630, 23.135, Point.CARTESIAN),
                            new Point(48.093, 35.334, Point.CARTESIAN),
                            new Point(62.115, 13.040, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 4
            .addPath(
                    new BezierLine(
                            new Point(57.488, 12.619, Point.CARTESIAN),
                            new Point(19.069, 13.461, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 5
            .addPath(
                    new BezierCurve(
                            new Point(19.069, 13.461, Point.CARTESIAN),
                            new Point(45.850, 23.416, Point.CARTESIAN),
                            new Point(58.329, 5.188, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            //line 6
            .addPath(
                    new BezierLine(
                            new Point(58.329, 5.188, Point.CARTESIAN),
                            new Point(19.209, 5.468, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 7
            .addPath(
                    new BezierCurve(
                            new Point(19.209, 5.468, Point.CARTESIAN),
                            new Point(20.331, 24.117, Point.CARTESIAN),
                            new Point(21.593, 25.379, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain pickSpecimenPreloadPath = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierLine(
                            new Point(21.593, 25.379, Point.CARTESIAN),
                            new Point(7.992, 25.379, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain goToChamberFromZone = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(8.000, 25.000, Point.CARTESIAN),
                            new Point(15.621, 69.302, Point.CARTESIAN),
                            new Point(40.000, 68.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

    public static PathChain goToZoneFromChamber = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(40.000, 68.000, Point.CARTESIAN),
                            new Point(15.621, 69.302, Point.CARTESIAN),
                            new Point(21.593, 25.379, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();

}
