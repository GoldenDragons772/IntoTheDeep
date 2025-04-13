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
import org.firstinspires.ftc.teamcode.implementation.commands.TransferSampleCommand

@Autonomous(name = "Bucket Auto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {

        val root = RootSystem(hardwareMap, telemetry, false)

        val transferSampleCommand = TransferSampleCommand(root.intake, root.outtake, root.climb);

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

                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                WaitCommand(500),

                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.toggleClaw(),

                transferSampleCommand,
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),

                WaitCommand(500),

                FollowPath(root.follower, BucketAutoPaths.score1(), true, 0.9),

                root.outtake.clawOpen(),
                WaitCommand(250),

                // pickup sample 2
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.sample2(), true, 0.9),
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                WaitCommand(500),

                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.toggleClaw(),


                transferSampleCommand,
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),

                WaitCommand(500),

                FollowPath(root.follower, BucketAutoPaths.score2(), true, 0.9),

                root.outtake.clawOpen(),
                WaitCommand(250),

                // pickup sample 3
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.sample3(), true, 0.9),
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                WaitCommand(500),

                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.toggleClaw(),

                transferSampleCommand,
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),

                WaitCommand(500),

                FollowPath(root.follower, BucketAutoPaths.score3(), true, 0.9),

                root.outtake.clawOpen(),
                WaitCommand(250),

                //Pickup from sub first

                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.goToSub(), true, 0.9),
                    WaitCommand(500).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                WaitCommand(500),

                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.toggleClaw(),

                transferSampleCommand,

                FollowPath(root.follower, BucketAutoPaths.scoreFromSub(), true, 0.9)
                    .alongWith(
                        WaitCommand(1500),
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
                        ),

                root.outtake.clawOpen(),
                WaitCommand(250),

                //Pickup from sub second

                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.goToSub(), true, 0.9),
                    WaitCommand(500).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                WaitCommand(500),

                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.toggleClaw(),

                transferSampleCommand,

                FollowPath(root.follower, BucketAutoPaths.scoreFromSub(), true, 0.9)
                    .alongWith(
                        WaitCommand(1500),
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
                    ),

                root.outtake.clawOpen(),
                WaitCommand(250),




            )
        )
    }

}