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
                        new Point(34.493, 72.070, Point.CARTESIAN)
                )
        )
        .setConstantHeadingInterpolation(Math.toRadians(180))
        .build();
}
