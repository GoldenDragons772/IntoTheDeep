package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.*
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem

@Autonomous(name = "Bucket Auto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {
        val root = RootSystem(hardwareMap, telemetry)
        root.follower.setStartingPose(BucketAutoPaths.startPose)
//        follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            RunCommand({
                if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
                root.update()
            }),
            root.outtake.clawClose(),
            root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
            root.intake.moveToHome(),
            WaitUntilCommand(this::opModeIsActive),
            SequentialCommandGroup(
                // preload
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.scorePreload(), true, 0.9),
                    root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
                    
                    root.outtake.moveArmToScore(),
                ),
                WaitCommand(250),
                root.outtake.toggleClaw(),

                // pickup sample 1
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.sample1(), true, 0.9),
                    WaitCommand(500).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                    root.intake.moveToTarget()
                )
//                root.outtake.moveArmToHome(),
//                root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
//                //Field sample 1
//                FollowPath(root.follower, BucketAutoPaths.moveToFirstSample, true, 0.2),
//                root.intake.moveToTarget()

            )
        )
    }

}