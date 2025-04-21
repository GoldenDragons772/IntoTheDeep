package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.ParallelCommandGroup
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.auto.SpecimenAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : CommandOpMode() {


    override fun initialize() {

        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = true)
        root.follower.setStartingPose(Pose(8.50, 66.500, Math.toRadians(180.0)))
        val specimenCommand = {
            root.climb.climbState = ClimbSystem.ClimbState.HIGH_CHAMBER
            root.outtake.moveArmToScoreSpec()
        }

//        // reset encoders only once during auto.
//        root.climb.resetEncoders()
        //The actual auto code
        schedule(
            root.outtake.clawClose(),
            root.outtake.setPivot(OuttakeSystem.OuttakeState.PRELOAD),

            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                root.update()
                if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
            }),
            SequentialCommandGroup(
                //Preload
                root.outtake.setStrike(OuttakeSystem.OuttakeState.HOME),
                root.outtake.clawClose(),
                specimenCommand, // score position
                FollowPath(root.follower, SpecimenAutoPaths.preload(), true, 0.9)
                    .andThen(
                        WaitCommand(250),
                        root.outtake.toggleClaw(),
                        WaitCommand(150),
                        root.outtake.setPivot(OuttakeSystem.OuttakeState.SAFE)
                    ),

                ParallelCommandGroup(
                    //Knock the Specimens
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        root.outtake.moveArmToHome(),
                        root.intake.moveToHome(),
                    ),
                    FollowPath(root.follower, SpecimenAutoPaths.knockSpecsIntoZone(), true, 1.0).andThen(
                        FollowPath(root.follower, SpecimenAutoPaths.grab1(), true, 0.9),
                        FollowPath(
                            root.follower,
                            SpecimenAutoPaths.lineGrab1(),
                            false,
                            0.6
                        ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500)
                    ),
                ), //WaitUntilCommand { root.outtake.getClawButtonState() },
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
            )
        )
    }
}