package org.firstinspires.ftc.team772.implementation.commands;

import com.arcrobotics.ftclib.command.Command;
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

    @Override
    public void initialize() {
        follower.holdPoint(holdPoint, heading);
    }

    @Override
    public void end(boolean interrupted) {
        follower.breakFollowing();
    }
}