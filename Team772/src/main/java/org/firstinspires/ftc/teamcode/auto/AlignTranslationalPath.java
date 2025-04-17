package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

public class AlignTranslationalPath extends PathChain {
    Point currentPosition, targetPosition;

    AlignTranslationalPath(Point currentPosition, Point targetPosition) {
        this.currentPosition = currentPosition;
        this.targetPosition = targetPosition;
    }

    public PathChain alignTranslational() {

        return new PathBuilder()
                .addPath(
                        new BezierLine(
                                this.currentPosition,
                                this.targetPosition
                        )
                )
                .build();
    }


}
