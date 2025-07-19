package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

public class ReflectedBucketAutoPaths {
    static Pose reflect(Pose pose){
        return new Pose(pose.getX(), 148.0 - pose.getY(), pose.getHeading() + Math.PI);
    }

    public static final Pose startPose = reflect(BucketAutoPaths.startPose);
    public static final Pose scorePose = reflect(BucketAutoPaths.scorePose);
    public static final Pose sample1 = reflect(BucketAutoPaths.sample1);
    public static final Pose sample2 = reflect(BucketAutoPaths.sample2);
    public static final Pose sample3 = reflect(BucketAutoPaths.sample3);
    public static final Pose subPose = reflect(BucketAutoPaths.subPose);

    public static PathChain scorePreload() {
        return new PathBuilder()
            .addPath(
                new BezierLine(
                    new Point(startPose),
                    new Point(scorePose)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(270) + Math.PI, Math.toRadians(315) + Math.PI)
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

    public static PathChain sample3Align() {
        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(sample3),
                                new Point(sample3.getX(), sample3.getY() + 2)
                        )
                )
                .setConstantHeadingInterpolation(sample3.getHeading() + Math.toRadians(10))
                .build();
    }

    public static PathChain score3() {
        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(sample3),
                                new Point(scorePose)
                        )
                )
                .setLinearHeadingInterpolation(sample3.getHeading(), scorePose.getHeading())
                .build();
    }


    public static PathChain goToSub() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(scorePose),
                                new Point(scorePose.getX(), scorePose.getY() + 7),
                                new Point(scorePose.getX(), scorePose.getY() + 7),
                                new Point(subPose)
                        )
                )
                .setLinearHeadingInterpolation(scorePose.getHeading(), subPose.getHeading())
                .build();
    }

    public static PathChain scoreFromSub() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(subPose),
                                new Point(scorePose.getX(), scorePose.getY() + 8),
                                new Point(scorePose.getX(), scorePose.getY() + 8),
                                new Point(scorePose)
                        )
                )
                .setLinearHeadingInterpolation(subPose.getHeading(), scorePose.getHeading())
                .build();
    }

    public static PathChain finishAuto() {
        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(scorePose),
                                new Point(scorePose.getX() + 10, scorePose.getY() -10)
                        )
                )
                .setConstantHeadingInterpolation(scorePose.getHeading())
                .build();
    }


}
