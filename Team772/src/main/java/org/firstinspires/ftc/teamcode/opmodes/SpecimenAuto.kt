package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.InstantCommand
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
import org.firstinspires.ftc.teamcode.implementation.commands.AutoSpecWallCommand
import org.firstinspires.ftc.teamcode.implementation.commands.AutoSpecimenCommand

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : CommandOpMode() {
    override fun initialize() {

        //val pinpoint: GoBildaPinpointDriver = hardwareMap.get(GoBildaPinpointDriver::class.java, "pinpoint");

        val root = RootSystem(hardwareMap, telemetry)

        root.follower.setStartingPose(Pose(8.50, 53.500, Math.toRadians(180.0)))


        val specimenCommand = AutoSpecimenCommand(root.intake, root.outtake, root.climb)
        val specWallCommand = AutoSpecWallCommand(root.intake, root.outtake, root.climb)

        root.climb.resetEncoders()

       // outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.HOME)
        //outtakeSystem.setStrike(OuttakeSystem.OuttakePosition.HOME)
//        root.follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            InstantCommand({
//                pinpoint.resetPosAndIMU()
//                root.follower.setStartingPose(Pose(8.50, 53.500, Math.toRadians(180.0)))
                root.climb.resetEncoders()
            }),
            root.outtake.clawClose(),
            root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                root.follower.update()
                //drivesystem.update()
                if (root.follower.isBusy) root.follower.telemetryDebug(telemetry)
                if(root.follower.isRobotStuck) {
                    root.outtake.moveArmToScore()
                }
            }),
            SequentialCommandGroup(
                //Preload
                root.outtake.setStrike(OuttakeSystem.OuttakePosition.HOME),
                root.outtake.clawClose(),
                specimenCommand, // score position
                FollowPath(root.follower, SpecimenAutoPaths.preload(), true, 0.9)
                    .andThen(
                        root.outtake.toggleClaw(),
                        WaitCommand(150),
                        root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE)
                    ),

                ParallelCommandGroup(
                    //Knock the Specimens
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        root.outtake.moveArmToHome(),
                        root.intake.moveToHome(),
                    ),
                    FollowPath(root.follower, SpecimenAutoPaths.knockSpecsIntoZone(), false, 0.9),
                ),
                //Spec 2
                root.outtake.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(root.follower, SpecimenAutoPaths.spec1(), true, 0.9),
                WaitCommand(150),
                root.outtake.toggleClaw(),
                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                ParallelCommandGroup(
                    FollowPath(root.follower, SpecimenAutoPaths.grab2(), true, 0.8),
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        root.outtake.moveArmToHome(),
                        root.intake.moveToHome(),
                    )
                ),
                // spec3
                root.outtake.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(root.follower, SpecimenAutoPaths.spec2(), true, 0.9),
                WaitCommand(150),
                root.outtake.toggleClaw(),
                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                ParallelCommandGroup(
                    FollowPath(root.follower, SpecimenAutoPaths.grab3(), true, 0.8),
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        root.outtake.moveArmToHome(),
                        root.intake.moveToHome(),
                    )
                ),
                // spec4
                root.outtake.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(root.follower, SpecimenAutoPaths.spec3(), true, 0.9),
                WaitCommand(150),
                root.outtake.toggleClaw(),
                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                ParallelCommandGroup(
                    FollowPath(root.follower, SpecimenAutoPaths.grab4(), true, 0.8),
                    WaitCommand(800).andThen(
                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        root.outtake.moveArmToHome(),
                        root.intake.moveToHome(),
                    )
                ),
                // spec5
                root.outtake.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(root.follower, SpecimenAutoPaths.spec4(), true, 0.9),
            )
        )
    }

}