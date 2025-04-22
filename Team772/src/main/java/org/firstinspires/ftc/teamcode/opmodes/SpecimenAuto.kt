package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.*
import org.firstinspires.ftc.teamcode.auto.SpecimenAutoPaths
import org.firstinspires.ftc.teamcode.helpers.Util.blockPath
import org.firstinspires.ftc.teamcode.helpers.Util.interruptOn
import org.firstinspires.ftc.teamcode.helpers.Util.timeout
import org.firstinspires.ftc.teamcode.implementation.*

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : LinearOpMode() {


    override fun runOpMode() {

        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = true)
        root.follower.setStartingPose(Pose(8.50, 66.500, Math.toRadians(180.0)))
        val specimenCommand = suspend {
            root.climb.climbState = ClimbState.HIGH_CHAMBER
            root.outtake.moveArmToScoreSpec()
        }

//        // reset encoders only once during auto.
//        root.climb.resetEncoders()
        //The actual auto code
        root.outtake.setClaw(ClawState.OPEN)
        root.outtake.setPivot(OuttakeState.PRELOAD)

        waitForStart()
        runBlocking {
            launch {
                while (opModeIsActive()) {
                    root.update()
                    if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
                }
            }
            //Preload
            root.outtake.setStrike(OuttakeState.HOME)
            root.outtake.setClaw(ClawState.CLOSED)
            specimenCommand() // score position

            root.follower.blockPath(SpecimenAutoPaths.preload(), 0.9, true)
            delay(250)
            root.outtake.toggleClaw()
            delay(150)
            root.outtake.setPivot(OuttakeState.SAFE)

            //Knock the Specimens
            delay(800)

            root.climb.set(ClimbState.HOME)
            root.outtake.moveArmToHome()
            root.intake.moveToHome()
            root.follower.blockPath(SpecimenAutoPaths.knockSpecsIntoZone(), 1.0, true).join()

            root.follower.blockPath(SpecimenAutoPaths.grab1(), 0.9, true).join()
            withTimeout(1500) {
                return@withTimeout root.follower.blockPath(SpecimenAutoPaths.lineGrab1(), 0.6, false)
            }.interruptOn { root.outtake.getClawButtonState() }
            //Spec 2
            root.outtake.toggleClaw()
            //WaitCommand(200),
            specimenCommand()
            root.follower.blockPath(SpecimenAutoPaths.spec1(), 0.9, true).join()
            delay(150)
            root.outtake.toggleClaw()
            root.outtake.setPivot(OuttakeState.SAFE)
            mutableListOf(
                blockPath(root.follower, SpecimenAutoPaths.grab2(), 0.9, true),
                launch {
                    delay(600L)
                    root.climb.set(ClimbState.HOME)
                    root.outtake.moveArmToHome()
                    root.intake.moveToHome()
                }
            ).joinAll()
            root.follower.blockPath(
                SpecimenAutoPaths.lineGrab2(),
                0.6,
                false
            ).timeout(1500L)
                .interruptOn { root.outtake.getClawButtonState() }.join()
//                // spec3
            root.outtake.toggleClaw()
            //WaitCommand(200),
            specimenCommand()
            root.follower.blockPath(SpecimenAutoPaths.spec2(), 0.9, true).join()
            delay(150)
            root.outtake.toggleClaw()
            root.outtake.setPivot(OuttakeState.SAFE)
            mutableListOf(
                root.follower.blockPath(SpecimenAutoPaths.grab3(), 0.9, true),
                launch {
                    delay(600)
                    root.climb.set(ClimbState.HOME)
                    root.outtake.moveArmToHome()
                    root.intake.moveToHome()
                }
            ).joinAll()
            root.follower.blockPath(
                SpecimenAutoPaths.lineGrab3(),
                0.6,
                false,
            ).timeout(1500).interruptOn { root.outtake.getClawButtonState() }.join()
//                // spec4
            root.outtake.toggleClaw()
            specimenCommand()

            root.follower.blockPath(SpecimenAutoPaths.spec3(), 0.9, true).join()
            delay(150)
            root.outtake.toggleClaw()
            root.outtake.setPivot(OuttakeState.SAFE)

            mutableListOf(
                root.follower.blockPath(SpecimenAutoPaths.grab4(), 0.8, true),
                launch {
                    delay(600)
                    root.climb.set(ClimbState.HOME)
                    root.outtake.moveArmToHome()
                    root.intake.moveToHome()
                }
            ).joinAll()
            root.follower.blockPath(
                SpecimenAutoPaths.lineGrab4(),
                0.6,
                false,
            ).interruptOn { root.outtake.getClawButtonState() }.timeout(1500).join()
            // spec5
            root.outtake.toggleClaw()
            specimenCommand()
            root.follower.blockPath(SpecimenAutoPaths.spec4(), 0.9, true).join()
            delay(150)
            root.outtake.toggleClaw()
            root.outtake.setPivot(OuttakeState.SAFE)

            mutableListOf(
                root.follower.blockPath(SpecimenAutoPaths.park(), 1.0, true),
                launch {
                    delay(600)
                    root.climb.set(ClimbState.HOME)
                    root.outtake.moveArmToHome()
                    root.intake.moveToHome()
                }
            )
        }
    }

}