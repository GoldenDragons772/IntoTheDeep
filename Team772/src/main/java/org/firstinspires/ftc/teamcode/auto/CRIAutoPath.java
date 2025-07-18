package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.*;

import static org.firstinspires.ftc.teamcode.auto.SpecimenAutoPaths.*;

public class CRIAutoPath {
    private static double SAMPLE_OFFSET = 10.0 - 0.5;
    private static double SAMPLE_DISTANCE = 47.4;
    private static double SAMPLE_X = 26.0 - 3.5;
    private static double FIELD_SIZE = 148;
    // Subtract from field size to mirror coordinates
    // Distance from left size to front of robot + distane from front of robot to center
    public static Pose start = new Pose(FIELD_SIZE - (47 + 7), 5.5, 0);
    public static Pose hold = new Pose((FIELD_SIZE - 32), 40, 0);
    public static Pose destHold = new Pose((FIELD_SIZE - 32), 38, 0);
    public static Pose dest = new Pose(FIELD_SIZE - 28, 7, 0);
    public static Pose sample1 = new Pose(FIELD_SIZE - SAMPLE_X, SAMPLE_DISTANCE, 0);
    public static Pose sample2 = new Pose(FIELD_SIZE - SAMPLE_X, SAMPLE_DISTANCE + SAMPLE_OFFSET, 0);
    public static Pose sample3 = new Pose(FIELD_SIZE - SAMPLE_X, SAMPLE_DISTANCE + SAMPLE_OFFSET * 2, 0);

    public static PathChain moveToAdjacent() {
        PathBuilder builder = new PathBuilder();
        builder
                .addPath(new BezierLine(start, hold))
                .setTangentHeadingInterpolation();
        return builder.build();
    }

    public static PathChain spec2() {
        return new PathBuilder()
                .addPath(new BezierLine(reflect(new Point(grab2Pose)), reflect(new Point(specPose))))
                .setConstantHeadingInterpolation(specPose.getHeading() + 90)
                .setZeroPowerAccelerationMultiplier(5)
                .setPathEndTimeoutConstraint(100)
                .build();
    }

    public static PathChain grab2() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(
                                reflect(new Point(specPose)),
                                reflect(new Point(specPose.getX() - 30, specPose.getY(), Point.CARTESIAN)),
                                reflect(new Point(grab2Pose.getX() + 35, grab2Pose.getY(), Point.CARTESIAN)),
                                reflect(new Point(grab2Pose))
                        )
                )
                .setConstantHeadingInterpolation(grab2Pose.getHeading() + 90)
                .setZeroPowerAccelerationMultiplier(2)
                .setPathEndTimeoutConstraint(100)
                .build();
    }

    public static Point reflect(Point original) {
        return new Point(original.getY(), FIELD_SIZE - original.getX());
    }

    public static PathChain lineToPlayer() {
        return new PathBuilder()
                .addPath(
                        new BezierCurve(reflect(new Point(grab2Pose)),
                                reflect(new Point(grab2Pose.getX() - 12, grab2Pose.getY(), Point.CARTESIAN))
                        )
                )
                .setConstantHeadingInterpolation(grab2Pose.getHeading() + 90)
                .setZeroPowerAccelerationMultiplier(2.5)
                .setPathEndTimeoutConstraint(75)
                .build();
    }
}

