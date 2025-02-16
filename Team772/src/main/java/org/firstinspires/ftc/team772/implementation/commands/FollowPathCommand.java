package org.firstinspires.ftc.team772.implementation.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;

public class FollowPathCommand extends CommandBase {

    private final Follower follower;
    private final PathChain path;
    private boolean holdEnd = true;
    private double maxPower = 0.1;
    private double completionThreshold = 0.99;
    private int timeout = 0;
    private long startTime;

    public FollowPathCommand(Follower follower, PathChain path) {
        this.follower = follower;
        this.path = path;
        this.timeout = 0;
    }

    public FollowPathCommand(Follower follower, PathChain path, int timeout) {
        this.follower = follower;
        this.path = path;
        this.timeout = timeout;
    }

    public FollowPathCommand(Follower follower, PathChain pathChain, int timeout, double maxPower) {
        this.follower = follower;
        this.path = pathChain;
        this.maxPower = maxPower;
        this.timeout = timeout;
    }

    public FollowPathCommand(Follower follower, PathChain pathChain, int timeout, boolean holdEnd) {
        this.follower = follower;
        this.path = pathChain;
        this.holdEnd = holdEnd;
        this.timeout = timeout;
    }

    public FollowPathCommand(Follower follower, PathChain pathChain, int timeout, boolean holdEnd, double maxPower) {
        this.follower = follower;
        this.path = pathChain;
        this.holdEnd = holdEnd;
        this.maxPower = maxPower;
        this.timeout = timeout;
    }

    public FollowPathCommand(Follower follower, Path path) {
        this(follower, new PathChain(path));
    }
    public FollowPathCommand(Follower follower, Path path, int timeout) {
        this(follower, new PathChain(path), timeout);
    }

    public FollowPathCommand(Follower follower, Path path, int timeout, double maxPower) {
        this(follower, new PathChain(path), timeout, maxPower);
    }

    /**
     * Decides whether or not to make the robot maintain its position once the path ends.
     *
     * @param holdEnd If the robot should maintain its ending position
     * @return This command for compatibility in command groups
     */
    public FollowPathCommand setHoldEnd(boolean holdEnd) {
        this.holdEnd = holdEnd;
        return this;
    }

    /**
     * Sets the follower's maximum power
     * @param power Between 0 and 1
     * @return This command for compatibility in command groups
     */
    public FollowPathCommand setMaxPower(double power) {
        this.maxPower = power;
        return this;
    }

    /**
     * Sets the T-value at which the follower will consider the path complete
     * @param t Between 0 and 1
     * @return This command for compatibility in command groups
     */
    public FollowPathCommand setCompletionThreshold(double t) {
        this.completionThreshold = t;
        return this;
    }

    @Override
    public void initialize() {
        follower.setMaxPower(this.maxPower);
        follower.followPath(path, holdEnd);
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isFinished() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime >= timeout && timeout > 0) {
            return true;
        }

        if ( follower.getCurrentPathNumber() == this.path.size() - 1 && Math.abs(follower.headingError) < 0.1 ) {
            return follower.getCurrentTValue() >= this.completionThreshold;
        }
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        //follower.setMaxPower(1);
    }
}