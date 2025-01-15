package org.firstinspires.ftc.team772.autos;

import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

import org.firstinspires.ftc.team772.implementation.Constants;

public class SpecimenPath {

    public static PathChain startSpecimenPath = new PathBuilder()
        .addPath(
        // Line 1
                new BezierCurve(
                        new Point(7.852, 55.945, Point.CARTESIAN),
                        new Point(28.463, 55.945, Point.CARTESIAN),
                        new Point(Constants.BLUE_CHAMBER_XDISTANCE, 71.000, Point.CARTESIAN)
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
                            new Point(55.000, 25.562, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 2
                    new BezierLine(
                            new Point(55.000, 25.562, Point.CARTESIAN),
                            new Point(23.006, 22.154, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 3
                    new BezierLine(
                            new Point(23.006, 22.154, Point.CARTESIAN),
                            new Point(55.000, 25.278, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 4
                    new BezierCurve(
                            new Point(55.000, 25.278, Point.CARTESIAN),
                            new Point(69.302, 12.497, Point.CARTESIAN),
                            new Point(23.006, 15.905, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 5
                    new BezierLine(
                            new Point(23.006, 15.905, Point.CARTESIAN),
                            new Point(55.000, 9.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 6
                    new BezierLine(
                            new Point(55.000, 9.000, Point.CARTESIAN),
                            new Point(22.438, 9.000, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 7
                    new BezierCurve(
                            new Point(22.438, 9.000, Point.CARTESIAN),
                            new Point(40.615, 32.947, Point.CARTESIAN),
                            new Point(18.746, 25.278, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(230))
            .build();

    public static PathChain knock2SpecsIntoZone = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierCurve(
                            new Point(33.000, 71.000, Point.CARTESIAN),
                            new Point(11.645, 37.207, Point.CARTESIAN),
                            new Point(55.000, 25.562, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 2
                    new BezierLine(
                            new Point(55.000, 25.562, Point.CARTESIAN),
                            new Point(23.006, 22.154, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 3
                    new BezierLine(
                            new Point(23.006, 22.154, Point.CARTESIAN),
                            new Point(55.000, 25.278, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 4
                    new BezierCurve(
                            new Point(55.000, 25.278, Point.CARTESIAN),
                            new Point(69.302, 12.497, Point.CARTESIAN),
                            new Point(23.006, 15.905, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(180))
            .addPath(
                    // Line 5
                    new BezierCurve(
                            new Point(23.006, 15.905, Point.CARTESIAN),
                            new Point(40.615, 32.947, Point.CARTESIAN),
                            new Point(18.746, 25.278, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(230))
            .build();

    public static PathChain goToChamberFromZone = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierCurve(
                            new Point(18.462, 44.024, Point.CARTESIAN),
                            new Point(13.349, 65.893, Point.CARTESIAN),
                            new Point(28.000, 68.845, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(230), Math.toRadians(180))
            .build();
    public static PathChain goToZoneFromChamber = new PathBuilder()
            .addPath(
                    // Line 1
                    new BezierCurve(
                            new Point(28.000, 69.000, Point.CARTESIAN),
                            new Point(13.349, 65.893, Point.CARTESIAN),
                            new Point(18.462, 44.024, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(230))
            .build();

}
