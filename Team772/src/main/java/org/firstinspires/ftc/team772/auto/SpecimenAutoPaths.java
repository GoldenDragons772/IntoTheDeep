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
                            new Point(1.136, 34.083, Point.CARTESIAN),
                            new Point(61.349, 23.006, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            // line 2
            .addPath(
                    new BezierLine(
                            new Point(61.349, 23.006, Point.CARTESIAN),
                            new Point(15.337, 23.006, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    new BezierCurve(
                            new Point(15.337, 23.006, Point.CARTESIAN),
                            new Point(40.047, 31.811, Point.CARTESIAN),
                            new Point(60.497, 13.065, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    new BezierLine(
                            new Point(60.497, 13.065, Point.CARTESIAN),
                            new Point(15.053, 12.781, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    new BezierCurve(
                            new Point(15.053, 12.781, Point.CARTESIAN),
                            new Point(41.467, 20.450, Point.CARTESIAN),
                            new Point(60.497, 5.112, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .build();
}
