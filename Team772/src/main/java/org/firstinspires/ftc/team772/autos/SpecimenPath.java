package org.firstinspires.ftc.team772.autos;

import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.Point;

public class SpecimenPath {

    public SpecimenPath() {
        PathBuilder builder = new PathBuilder();

        builder
                .addPath(
                        // Line 1
                        new BezierCurve(
                                new Point(7.852, 55.945, Point.CARTESIAN),
                                new Point(28.463, 55.945, Point.CARTESIAN),
                                new Point(44.588, 72.070, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(1))
                .addPath(
                        // Line 2
                        new BezierLine(
                                new Point(44.588, 72.070, Point.CARTESIAN),
                                new Point(15.984, 121.566, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(
                        Math.toRadians(1),
                        Math.toRadians(1)
                );
    }
}
