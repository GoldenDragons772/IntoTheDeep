//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.teamcode.implementation.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.Point;

public class HoldPointCommand extends CommandBase {
    private final BezierPoint holdPoint;
    private final double heading;
    private final Follower follower;

    public HoldPointCommand(Follower follower, BezierPoint holdPoint, double heading) {
        this.follower = follower;
        this.holdPoint = holdPoint;
        this.heading = heading;
    }

    public HoldPointCommand(Follower follower, Point holdPoint, double heading) {
        this(follower, new BezierPoint(holdPoint), heading);
    }

    public HoldPointCommand(Follower follower, Pose holdPose) {
        this(follower, new BezierPoint(new Point(holdPose)), holdPose.getHeading());
    }

    public void initialize() {
        this.follower.breakFollowing();
        this.follower.holdPoint(this.holdPoint, this.heading);
    }

    public void end(boolean interrupted) {
        this.follower.breakFollowing();
    }

    @Override
    public boolean isFinished() {
        return follower.atPoint(holdPoint.getPoint(1), 0.5, 0.5);
    }
}
