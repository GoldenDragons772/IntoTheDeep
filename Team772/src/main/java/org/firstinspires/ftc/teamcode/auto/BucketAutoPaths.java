package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class BucketAutoPaths {

    public static final Pose startPose = new Pose(8, 104, Math.toRadians(270));
    public static final Pose scorePose = new Pose(18, 125, Math.toRadians(315));
    public static final Pose sample1 = new Pose(20, 120, Math.toRadians(0));
    public static final Pose sample2 = new Pose(20, 130, Math.toRadians(0));
    public static final Pose sample3 = new Pose(20, 135, Math.toRadians(0));

    public static PathChain scorePreload() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(startPose),
                    new Point(scorePose)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(315))
            .setZeroPowerAccelerationMultiplier(2.5)
            .build();
    }

    public static PathChain sample1() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(scorePose),
                    new Point(sample1)
                )
            )
            .setLinearHeadingInterpolation(scorePose.getHeading(), sample1.getHeading())
            .build();
    }
//
    public static PathChain score1() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                        new Point(sample1),
                        new Point(scorePose)
                )
            )
            .setLinearHeadingInterpolation(sample1.getHeading(), scorePose.getHeading())
            .build();
    }

    public static PathChain sample2() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(scorePose),
                    new Point(sample2)
                )
            )
            .setLinearHeadingInterpolation(scorePose.getHeading(), sample2.getHeading())
            .build();
    }

    public static PathChain score2() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(sample2),
                    new Point(scorePose)
                )
            )
            .setLinearHeadingInterpolation(sample2.getHeading(), scorePose.getHeading())
            .build();
    }

    public static PathChain sample3() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(scorePose),
                    new Point(sample3)
                )
            )
            .setLinearHeadingInterpolation(scorePose.getHeading(), sample3.getHeading())
            .build();
    }
}
