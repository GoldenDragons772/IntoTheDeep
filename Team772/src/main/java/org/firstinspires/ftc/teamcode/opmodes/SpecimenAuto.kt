package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.firstinspires.ftc.teamcode.auto.SpecimenAutoPaths
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
            root.outtake.setStrike(OuttakeState.HOME),
            root.outtake.setClaw(ClawState.CLOSED)
            specimenCommand() // score position

            root.follower.followPath(SpecimenAutoPaths.preload(), 0.9, true)
            delay(250)
            root.outtake.toggleClaw()
            delay(150)
            root.outtake.setPivot(OuttakeState.SAFE)

            //Knock the Specimens
            delay(800)

            root.climb.set(ClimbState.HOME),
            root.outtake.moveArmToHome()
            root.intake.moveToHome()
            root.follower.followPath(SpecimenAutoPaths.knockSpecsIntoZone(), 1.0, true)

            root.follower.followPath(SpecimenAutoPaths.grab1(), 0.9, true)
            val j = launch {
                withTimeout(1500) {
                    root.follower.followPath(SpecimenAutoPaths.lineGrab1(), 0.6, false)
                }
            }
            launch {
                while (true){
                    delay(5)
                    if (!root.follower.isBusy || root.outtake.getClawButtonState()) {
                        root.follower.breakFollowing()
                    }
                }
            }
            ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500)
            //WaitUntilCommand { root.outtake.getClawButtonState() },
            //Spec 2
            root.outtake.toggleClaw(),
            //WaitCommand(200),
            specimenCommand,
            FollowPath(root.follower, SpecimenAutoPaths.spec1(), true, 0.9),
            WaitCommand(150),
            root.outtake.toggleClaw(),
            root.outtake.setPivot(OuttakeSystem.OuttakeState.SAFE),
            ParallelCommandGroup(
                FollowPath(root.follower, SpecimenAutoPaths.grab2(), true, 0.9),
                WaitCommand(600).andThen(
                    root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                    root.outtake.moveArmToHome(),
                    root.intake.moveToHome(),
                )
            ),
            FollowPath(
                root.follower,
                SpecimenAutoPaths.lineGrab2(),
                false,
                0.6
            ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500),
//                // spec3
            root.outtake.toggleClaw(),
            //WaitCommand(200),
            specimenCommand,
            FollowPath(root.follower, SpecimenAutoPaths.spec2(), true, 0.9),
            WaitCommand(150),
            root.outtake.toggleClaw(),
            root.outtake.setPivot(OuttakeSystem.OuttakeState.SAFE),
            ParallelCommandGroup(
                FollowPath(root.follower, SpecimenAutoPaths.grab3(), true, 0.9),
                WaitCommand(600).andThen(
                    root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                    root.outtake.moveArmToHome(),
                    root.intake.moveToHome(),
                )
            ),
            FollowPath(
                root.follower,
                SpecimenAutoPaths.lineGrab3(),
                false,
                0.6
            ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500),
//                // spec4
            root.outtake.toggleClaw(),
            //WaitCommand(200),
            specimenCommand,
            FollowPath(root.follower, SpecimenAutoPaths.spec3(), true, 0.9),
            WaitCommand(150),
            root.outtake.toggleClaw(),
            root.outtake.setPivot(OuttakeSystem.OuttakeState.SAFE),

            ParallelCommandGroup(
                FollowPath(root.follower, SpecimenAutoPaths.grab4(), true, 0.8),
                WaitCommand(600).andThen(
                    root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                    root.outtake.moveArmToHome(),
                    root.intake.moveToHome(),
                )
            ),
            FollowPath(
                root.follower,
                SpecimenAutoPaths.lineGrab4(),
                false,
                0.6
            ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500),
//                // spec5
            root.outtake.toggleClaw(),
            //WaitCommand(200),
            specimenCommand,
            FollowPath(root.follower, SpecimenAutoPaths.spec4(), true, 0.9),

            WaitCommand(150),
            root.outtake.toggleClaw(),
            root.outtake.setPivot(OuttakeSystem.OuttakeState.SAFE),
//                FollowPath(root.follower, SpecimenAutoPaths.park(), true, 1.0),

            ParallelCommandGroup(
                FollowPath(root.follower, SpecimenAutoPaths.park(), true, 1.0),
                WaitCommand(600).andThen(
                    root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                    root.outtake.moveArmToHome(),
                    root.intake.moveToHome(),
                )
            ),
        }
    }

}