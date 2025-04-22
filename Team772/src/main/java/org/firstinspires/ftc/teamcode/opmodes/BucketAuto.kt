package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.helpers.Util.blockPath
import org.firstinspires.ftc.teamcode.implementation.*

@Autonomous(name = "Bucket Auto")
class BucketAuto : LinearOpMode() {
    override fun runOpMode() {

        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = false)

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
            root.outtake.setClaw(ClawState.CLOSED)
            root.outtake.setPivot(OuttakeState.PRELOAD)

            root.intake.setPivot(IntakeState.HOME)
            root.intake.setStrike(IntakeState.HOME)

            root.intake.toggleHover()
            root.intake.moveToHome()


            // preload

            //Move the robot to the basket to score the preload.
            root.follower.blockPath(BucketAutoPaths.scorePreload(), 0.9, true).join()
            root.climb.set(ClimbState.HIGH_BASKET)
            root.outtake.moveArmToScore()

            //Let go of the sample
            delay(250L)
            root.outtake.toggleClaw()

            // Pickup sample 1

            //Drive to the first sample while setting the outtake to transfer.

            root.follower.blockPath(BucketAutoPaths.sample1(), 0.9, true)
            root.outtake.moveArmToTransferPrep()
            moveToScanningPosition(root)
            root.intake.setClaw(ClawState.CLOSED)
            delay(300)

            //Transfer the sample and start moving the climb to score.
            root.intake.transferSample()
            root.climb.set(ClimbState.HIGH_BASKET)

            delay(300)

            //Move the robot to score.
            root.follower.followPath(BucketAutoPaths.score1(), 0.9, true)
            delay(200)

            //Let go of the sample
            root.outtake.setClaw(ClawState.OPEN)
            delay(400)

            // pickup sample 2

            //Drive to the second sample while setting the outtake to transfer.
            root.follower.blockPath(BucketAutoPaths.sample2(), 0.9, true)
            root.outtake.moveArmToTransferPrep()
            delay(800)
            moveToScanningPosition(root)
            root.intake.toggleClaw()
            delay(500)

            //Transfer the sample and start moving the climb to score.
            root.intake.transferSample()
            root.climb::climbState.set(ClimbState.HIGH_BASKET)

            //Move the robot to score.
            root.follower.blockPath(BucketAutoPaths.score2(), 0.6, true).join()
            delay(200)

            //Let go of the sample
            root.outtake.setClaw(ClawState.OPEN)
            delay(600)
            // pickup sample 3

            //Drive to the third sample while setting the outtake to transfer.
            root.follower.followPath(BucketAutoPaths.sample3(), 0.9, false)
            root.outtake.moveArmToTransferPrep()
            delay(1000)
            root.climb.set(ClimbState.HOME)

            //Get in scanning position.
            root.intake.linkage.set(IntakeSystem.LEFT_LINKAGE_TARGET - 0.02)
            root.intake.hoverIntake()
//                root.intake.hoverIntake()
            root.intake.wrist.set(WristState.TARGET)
            delay(600)

            //Strike the intake down and close the claw
            root.intake.strikeIntake()
            root.follower.followPath(BucketAutoPaths.sample3Align(), 0.7, true)
            delay(300)
            root.intake.toggleClaw()
            delay(500)

            //Transfer the sample and start moving the climb to score.
            root.intake.transferSample()
            root.climb.set(ClimbState.HIGH_BASKET)

            //Move the robot to score.
            root.follower.followPath(BucketAutoPaths.score3(), 0.9, true)
            delay(200)

            //Let go of the sample.
            root.outtake.setClaw(ClawState.OPEN)
            delay(400)
//                //Pickup from sub first

            //Move to the sub while getting the outtake ready for transfer.
            root.follower.blockPath(BucketAutoPaths.goToSub(), 0.9, true).join()
            root.intake.moveToHome()
            delay(2000)
            root.outtake.toggleArmSpec()
            delay(1500)
            root.climb.set(ClimbState.HOME)
        }
    }
    suspend fun moveToScanningPosition(root:RootSystem) {
        delay(500)
        root.climb.set(ClimbState.HOME)

        //Get in scanning position.
        root.intake.moveToTarget()
        root.intake.hoverIntake()
        root.intake.wrist.set(WristState.HOME)
        delay(200)

        //Strike the intake down and close the claw
        root.intake.strikeIntake()
        delay(300)
    }
}