package org.firstinspires.ftc.team772.autos;

import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class SpecimenPath {

    public static PathChain startSpecimenPath = new PathBuilder()
        .addPath(
        // Line 1
                new BezierCurve(
                        new Point(7.852, 55.945, Point.CARTESIAN),
                        new Point(28.463, 55.945, Point.CARTESIAN),
                        new Point(32.000, 71.000, Point.CARTESIAN)
                )
        )
        .setConstantHeadingInterpolation(Math.toRadians(180))
        .build();

    public static PathChain knockSpecsIntoZone = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierCurve(
                            new Point(30.000, 71.000, Point.CARTESIAN),
                            new Point(11.645, 37.207, Point.CARTESIAN),
                            new Point(61.633, 25.562, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 2
                    new BezierLine(
                            new Point(61.633, 25.562, Point.CARTESIAN),
                            new Point(23.006, 22.154, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 3
                    new BezierLine(
                            new Point(23.006, 22.154, Point.CARTESIAN),
                            new Point(61.917, 25.278, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 4
                    new BezierCurve(
                            new Point(61.917, 25.278, Point.CARTESIAN),
                            new Point(69.302, 12.497, Point.CARTESIAN),
                            new Point(23.006, 15.905, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 5
                    new BezierLine(
                            new Point(23.006, 15.905, Point.CARTESIAN),
                            new Point(61.917, 12.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 6
                    new BezierLine(
                            new Point(61.917, 12.000, Point.CARTESIAN),
                            new Point(15.000, 12.497, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 7
                    new BezierCurve(
                            new Point(15.000, 12.497, Point.CARTESIAN),
                            new Point(32.947, 39.763, Point.CARTESIAN),
                            new Point(15.337, 39.763, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(230))
            .build();
}
