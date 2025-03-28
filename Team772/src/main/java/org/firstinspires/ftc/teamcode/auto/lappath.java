package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class lappath {
    public static PathBuilder builder = new PathBuilder();

    public static PathChain line1 = builder
            .addPath(
                    new BezierCurve(
                            new Point(8.000, 56.000, Point.CARTESIAN),
                            new Point(12.406, 119.852, Point.CARTESIAN),
                            new Point(73.329, 116.972, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(0))
            .build();

    public static PathChain line2 = builder
            .addPath(
                    new BezierCurve(
                            new Point(73.329, 116.972, Point.CARTESIAN),
                            new Point(142.671, 118.080, Point.CARTESIAN),
                            new Point(134.695, 30.351, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(0))
            .build();

    public static PathChain line3 = builder
            .addPath(
                    new BezierCurve(
                            new Point(134.695, 30.351, Point.CARTESIAN),
                            new Point(29.022, 0.000, Point.CARTESIAN),
                            new Point(7.975, 49.625, Point.CARTESIAN)
                    )
            )
            .setConstantHeadingInterpolation(Math.toRadians(0))
            .build();
}
