package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.commands.FollowPath
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.implementation.*

@Autonomous(name = "Bucket Auto")
class BucketAuto : LinearOpMode() {
    override fun runOpMode() {

        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = false)

        val transferSampleCommand = root.intake::transferSample


        root.follower.setStartingPose(BucketAutoPaths.startPose)

        waitForStart()
        runBlocking {
            root.intake.moveToHome()

            //The actual auto code
            launch {
                while (!isStopRequested) {
                    root.update()
                    if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
                }
            }
            root.outtake.setClaw(true)
            root.outtake.setPivot(OuttakeState.PRELOAD)

            root.intake.setPivot(IntakeState.HOME)
            root.intake.setStrike(IntakeState.HOME)

            root.intake.toggleHover()
            root.intake.moveToHome()


            // preload

            //Move the robot to the basket to score the preload.
            root.follower.followPath(BucketAutoPaths.scorePreload(),  0.9,true)
            root.climb::climbState.set(ClimbState.HIGH_BASKET)
            root.outtake.moveArmToScore()

            //Let go of the sample
            delay(250L)
            root.outtake.toggleClaw()

            // Pickup sample 1

            //Drive to the first sample while setting the outtake to transfer.

            FollowPath(root.follower, BucketAutoPaths.sample1(), true, 0.9)
            root.outtake.moveArmToTransferPrep()
            delay(500)
            root.climb::climbState.set(ClimbState.HOME)

            //Get in scanning position.
            root.intake.moveToTarget()
            root.intake.hoverIntake()
            root.intake.setWrist(WristState.HOME)
            delay(200)

            //Strike the intake down and close the claw
            root.intake.strikeIntake()
            delay(300)
            root.intake.setClaw(IntakeState.TARGET)
            delay(300)

            //Transfer the sample and start moving the climb to score.
            transferSampleCommand()
            root.climb::climbState.set(ClimbState.HIGH_BASKET)

            delay(300)

            //Move the robot to score.
            root.follower.followPath(BucketAutoPaths.score1(), 0.9, true)
            delay(200)

            //Let go of the sample
            root.outtake.setClaw(false)
            delay(400)

            // pickup sample 2

            //Drive to the second sample while setting the outtake to transfer.
            FollowPath(root.follower, BucketAutoPaths.sample2(), true, 0.9)
            root.outtake.moveArmToTransferPrep()
            delay(800)
            root.climb::climbState.set(ClimbState.HOME)

            //Get in scanning position.
            root.intake.moveToTarget()
            root.intake.hoverIntake()
            root.intake.setWrist(WristState.HOME)
            delay(200)

            //Strike the intake down and close the claw
            root.intake.strikeIntake()
            delay(300)
            root.intake.toggleClaw()
            delay(500)

            //Transfer the sample and start moving the climb to score.
            transferSampleCommand()
            root.climb::climbState.set(ClimbState.HIGH_BASKET)

            //WaitCommand(500),

            //Move the robot to score.
            FollowPath(root.follower, BucketAutoPaths.score2(), true, 0.6)
            delay(200)

            //Let go of the sample
            root.outtake.setClaw(false)
            delay(600)
            // pickup sample 3

            //Drive to the third sample while setting the outtake to transfer.
            root.follower.followPath(BucketAutoPaths.sample3(), 0.9, false)
            root.outtake.moveArmToTransferPrep()
            delay(1000)
            root.climb::climbState.set(ClimbState.HOME)

            //Get in scanning position.
            root.intake.setLinkage(IntakeSystem.LEFT_LINKAGE_TARGET - 0.02)
            root.intake.hoverIntake()
//                root.intake.hoverIntake()
            root.intake.setWrist(WristState.TARGET)
            delay(600)

            //Strike the intake down and close the claw
            root.intake.strikeIntake()
            FollowPath(root.follower, BucketAutoPaths.sample3Align(), 0.7)
            delay(300)
            root.intake.toggleClaw()
            delay(500)

            //Transfer the sample and start moving the climb to score.
            transferSampleCommand()
            root.climb::climbState.set(ClimbState.HIGH_BASKET)

            //WaitCommand(500),

            //Move the robot to score.
            root.follower.followPath(BucketAutoPaths.score3(), 0.9, true)
            delay(200)

            //Let go of the sample.
            root.outtake.setClaw(false)
            delay(400)
//                //Pickup from sub first

            //Move to the sub while getting the outtake ready for transfer.
            FollowPath(root.follower, BucketAutoPaths.goToSub(), true, 0.9)
            root.intake.moveToHome()
            delay(2000)
            root.outtake.toggleArmSpec()
            delay(1500)
            root.climb::climbState.set(ClimbState.HOME)

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
        }
    }
}