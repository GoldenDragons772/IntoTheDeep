package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.*
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem

@Autonomous(name = "Bucket Auto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {
        val root = RootSystem(hardwareMap, telemetry)
        root.follower.setStartingPose(Pose(5.9, 103.8, Math.toRadians(270.0)))
//        follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
                root.update()
            }),
            SequentialCommandGroup(
                // preload
                FollowPath(root.follower, BucketAutoPaths.scorePreloadPath, true, 0.2),
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
                root.outtake.moveArmToScore(),
                WaitCommand(1000),
                root.outtake.toggleClaw(),
                root.outtake.moveArmToHome(),
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                //Field sample 1
                FollowPath(root.follower, BucketAutoPaths.moveToFirstSample, true, 0.2),
                root.intake.moveToTarget()






                //

            )
        )
    }

}