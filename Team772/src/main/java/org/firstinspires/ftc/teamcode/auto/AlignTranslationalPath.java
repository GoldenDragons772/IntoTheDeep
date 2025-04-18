package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.follower.Follower;
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

    public static PathChain alignLatitudinal(Follower follower, double diff) {

        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(follower.getPose()),
                                new Point(follower.getPose().getX(), diff)
                        )
                )
                .setConstantHeadingInterpolation(follower.getPose().getHeading())
                .setZeroPowerAccelerationMultiplier(2.8)
                .build();
    }


}
