package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.*
import com.pedropathing.commands.FollowPath
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.frozenmilk.sinister.loading.Preload
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem

@Autonomous(name = "Bucket Auto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {

        val root = RootSystem(hardwareMap, telemetry, false)
        root.follower.setStartingPose(BucketAutoPaths.startPose)

        root.intake.moveToHome()

        //The actual auto code
        schedule(
            RunCommand({
                root.update()
                if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
            }),
            SequentialCommandGroup(
                root.outtake.clawClose(),
                root.outtake.setPivot(OuttakeSystem.OuttakePosition.PRELOAD),

                root.intake.setPivot(IntakeSystem.IntakePosition.HOME),
                root.intake.setStrike(IntakeSystem.IntakePosition.HOME),

                root.intake.toggleHover(),
                root.intake.moveToHome(),
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
                ),

                // pickup samples
                root.intake.setStrike(IntakeSystem.IntakePosition.TARGET),
                root.intake.setPivot(IntakeSystem.IntakePosition.TARGET),
                root.intake.setLinkage(IntakeSystem.LinkagePosition.FULL),
//                //Field sample 1
//                FollowPath(root.follower, BucketAutoPaths.score1(), true, 0.2),


            )
        )
    }

}