package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class AlignTranslationalPath extends PathChain {
//    Point currentPosition, targetPosition;

//    AlignTranslationalPath(Point currentPosition, Point targetPosition) {
//        this.currentPosition = currentPosition;
//        this.targetPosition = targetPosition;
//    }

    public static PathChain alignLatitudinal(Pose currentPose, double diff) {

        return new PathBuilder()
            .addPath(
                    new BezierLine(
                            new Point(currentPose),
                            new Point(currentPose.getX(), diff)
                    )
            )
            .setConstantHeadingInterpolation(currentPose.getHeading())
            .setZeroPowerAccelerationMultiplier(2.8)
            .build();
    }
}
