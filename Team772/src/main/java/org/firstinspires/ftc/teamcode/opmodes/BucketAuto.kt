package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.*
import com.pedropathing.commands.FollowPath
import com.pedropathing.commands.HoldPoint
import com.pedropathing.follower.Follower
import com.qualcomm.hardware.ams.AMSColorSensor
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.frozenmilk.sinister.loading.Preload
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.GrabSampleCommand
import org.firstinspires.ftc.teamcode.implementation.commands.ToggleIntakeCommand
import org.firstinspires.ftc.teamcode.implementation.commands.TransferSampleCommand
import kotlin.math.floor

@Autonomous(name = "Bucket Auto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {

        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = false)

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

                //Move the robot to the basket to score the preload.
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.scorePreload(), true, 0.9),
                    root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
                    root.outtake.moveArmToScore(),
                ),

                //Let go of the sample
                WaitCommand(250),
                root.outtake.toggleClaw(),

                // Pickup sample 1

                //Drive to the first sample while setting the outtake to transfer.
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.sample1(), true, 0.9),
                    root.outtake.moveArmToTransferPrep(),
                    WaitCommand(500).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                //Get in scanning position.
                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                root.intake.setWrist(IntakeSystem.WristPosition.HOME),
                WaitCommand(200),

                //Strike the intake down and close the claw
                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.setClaw(IntakeSystem.IntakePosition.TARGET),
                WaitCommand(300),

                //Transfer the sample and start moving the climb to score.
                transferSampleCommand,
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),

                WaitCommand(300),

                //Move the robot to score.
                FollowPath(root.follower, BucketAutoPaths.score1(), true, 0.9),
                WaitCommand(200),

                //Let go of the sample
                root.outtake.clawOpen(),
                WaitCommand(400),

                // pickup sample 2

                //Drive to the second sample while setting the outtake to transfer.
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.sample2(), true, 0.9),
                    root.outtake.moveArmToTransferPrep(),
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                //Get in scanning position.
                root.intake.moveToTarget(),
                root.intake.hoverIntake(),
                root.intake.setWrist(IntakeSystem.WristPosition.HOME),
                WaitCommand(200),

                //Strike the intake down and close the claw
                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.toggleClaw(),
                WaitCommand(500),

                //Transfer the sample and start moving the climb to score.
                transferSampleCommand,
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),

                //WaitCommand(500),

                //Move the robot to score.
                FollowPath(root.follower, BucketAutoPaths.score2(), true, 0.6),
                WaitCommand(200),

                //Let go of the sample
                root.outtake.clawOpen(),
                WaitCommand(600),

                // pickup sample 3

                //Drive to the third sample while setting the outtake to transfer.
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.sample3(), false, 0.9),
                    root.outtake.moveArmToTransferPrep(),
                    WaitCommand(1000).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

                //Get in scanning position.
                root.intake.setLinkage(IntakeSystem.LEFT_LINKAGE_TARGET - 0.02)
                    .andThen(root.intake.hoverIntake()),
//                root.intake.hoverIntake(),
                root.intake.setWrist(IntakeSystem.WristPosition.TARGET),
                WaitCommand(1200),

                //Strike the intake down and close the claw
                root.intake.strikeIntake(),
                FollowPath(root.follower, BucketAutoPaths.sample3Align(), 0.7),
                WaitCommand(300),
                root.intake.toggleClaw(),
                WaitCommand(500),

                //Transfer the sample and start moving the climb to score.
                transferSampleCommand,
                root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),

                //WaitCommand(500),

                //Move the robot to score.
                FollowPath(root.follower, BucketAutoPaths.score3(), true, 0.9),
                WaitCommand(200),

                //Let go of the sample.
                root.outtake.clawOpen(),
                WaitCommand(400),
//
//                //Pickup from sub first

                //Move to the sub while getting the outtake ready for transfer.
                ParallelCommandGroup(
                    FollowPath(root.follower, BucketAutoPaths.goToSub(), true, 0.9),
                    root.intake.moveToHome(),
                    WaitCommand(2000).andThen(root.outtake.toggleArmSpec()),
                    WaitCommand(1500).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
                    ),
                ),

//                //Move the intake into scanning position
//                root.intake.moveToTarget(),
//                root.intake.hoverIntake(),
//                GrabSampleCommand(root),
//
//                //Strike the intake and close the claw.
//                root.intake.strikeIntake(),
//                WaitCommand(300),
//                root.intake.toggleClaw(),

//                //TODO: Add command to scan submersible.
//                GrabSampleCommand(root),

                //Transfer the sample.
//                transferSampleCommand,
//
//                //Move back to the bucket while moving the climb up.
//                FollowPath(root.follower, BucketAutoPaths.scoreFromSub(), true, 0.9)
//                    .alongWith(
//                        WaitCommand(1500),
//                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
//                    ),
//
//                //Let go of the sample
//                root.outtake.clawOpen(),
//                WaitCommand(250),
//
//                //Pickup from sub second
//
//                //Move to the sub while getting the outtake ready for transfer.
//                ParallelCommandGroup(
//                    FollowPath(root.follower, BucketAutoPaths.goToSub(), true, 0.9),
//                    root.outtake.moveArmToTransferPrep(),
//                    WaitCommand(1500).andThen(
//                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
//                    ),
//                ),

                //Move the intake into scanning position
//                root.intake.moveToTarget(),
//                root.intake.hoverIntake(),
//                WaitCommand(500),
//
//                //Strike the intake and close the claw.
//                root.intake.strikeIntake(),
//                WaitCommand(300),
//                root.intake.toggleClaw(),

//                GrabSampleCommand(root),
//
//                //Transfer the Sample
//                transferSampleCommand,
//
//                //Go back to the sub while moving the climb system
//                FollowPath(root.follower, BucketAutoPaths.scoreFromSub(), true, 0.9)
//                    .alongWith(
//                        WaitCommand(1500),
//                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
//                    ),
//
//                //Let go of the sample.
//                root.outtake.clawOpen(),
//                WaitCommand(250),
//
//
//                //Drive the robot away from the bucket so that the outtake does not get
//                //stuck in the bucket after auto ends.
//                ParallelCommandGroup(
//                    FollowPath(root.follower, BucketAutoPaths.finishAuto(), true, 0.9),
//                    WaitCommand(1000).andThen(
//                        root.outtake.moveArmToHome(),
//                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME)
//                    ),
//                ),
            )
        )
    }
}