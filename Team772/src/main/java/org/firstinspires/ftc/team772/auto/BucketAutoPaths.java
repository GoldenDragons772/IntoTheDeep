package org.firstinspires.ftc.team772.auto;

import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class BucketAutoPaths {

    public static PathChain scorePreloadPath = new PathBuilder()
            .addPath(
                    new BezierCurve(
                            new Point(5.889, 103.759, Point.CARTESIAN),
                            new Point(20.892, 114.134, Point.CARTESIAN),
                            new Point(15.003, 129.418, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(320))
            .build();

    public static PathChain moveToFirstSample = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(15.003, 129.418, Point.CARTESIAN),
                            new Point(29.725, 126.473, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(320), Math.toRadians(340))
            .build();

    public static PathChain scoreFirstSample = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(29.725, 126.473, Point.CARTESIAN),
                            new Point(16.405, 133.204, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(340), Math.toRadians(330))
            .build();

    public static PathChain moveToSecondSample = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(16.405, 133.204, Point.CARTESIAN),
                            new Point(29.445, 132.362, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(330), Math.toRadians(360))
            .build();

    public static PathChain scoreSecondSample = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(29.445, 132.362, Point.CARTESIAN),
                            new Point(16.189, 133.491, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(360), Math.toRadians(340))
            .build();

    public static PathChain moveToThirdSample = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(16.189, 133.491, Point.CARTESIAN),
                            new Point(30.107, 139.456, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(340), Math.toRadians(360))
            .build();

    public static PathChain scoreThirdSample = new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(30.107, 139.456, Point.CARTESIAN),
                            new Point(15.905, 133.775, Point.CARTESIAN)
                    )
            )
            .setLinearHeadingInterpolation(Math.toRadians(360), Math.toRadians(340))
            .build();
}
