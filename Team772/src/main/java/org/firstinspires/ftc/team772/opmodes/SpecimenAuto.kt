package org.firstinspires.ftc.team772.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.follower.Follower
import com.pedropathing.util.Constants
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.team772.auto.SpecimenAutoPaths
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem
import org.firstinspires.ftc.team772.implementation.commands.FollowPathCommand
import org.firstinspires.ftc.team772.implementation.commands.SpecimenCommand
import org.firstinspires.ftc.team772.implementation.commands.TransferSpecimenCommand
import org.firstinspires.ftc.team772.pedroPathing.constants.FConstants
import org.firstinspires.ftc.team772.pedroPathing.constants.LConstants

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : CommandOpMode() {
    override fun initialize() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        val follower = Follower(hardwareMap)
        val intakeSystem = IntakeSystem(hardwareMap)
        val outtakeSystem = OuttakeSystem(hardwareMap)
        val climbSystem = ClimbSystem(hardwareMap)
        val specimenCommand = SpecimenCommand(intakeSystem, outtakeSystem, climbSystem)
        follower.setStartingPose(Pose(7.1, 53.5, Math.PI))
//        follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                follower.update()
                //follower.setMaxPower(0.8)
                if (follower.isBusy) follower.telemetryDebug(telemetry)
            })
            ,
            SequentialCommandGroup(
                //Preload
                outtakeSystem.clawClose(),
                specimenCommand,
                specimenCommand, // to get it to the right postion
                FollowPathCommand(follower, SpecimenAutoPaths.scoreFirstSpecimenPath, 1000, 0.8),
                WaitCommand(500),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                //Knock the Specimens

                //InstantCommand({ follower.setMaxPower(0.8) }),

                FollowPathCommand(follower, SpecimenAutoPaths.knockSpecsIntoZone, 15000, 0.9).setMaxPower(0.9),
                //Spec 2
                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath, 5000, 0.2).setMaxPower(0.55),
                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specimenCommand,
//                FollowPathCommand(follower, SpecimenAutoPaths.goToChamberFromZone, 1000, 0.8),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specimenCommand,
//                FollowPathCommand(follower, SpecimenAutoPaths.goToZoneFromChamber, 1000, 0.8),
//                //Spec 3
//                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath, 1000, 0.1),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specimenCommand,
//                FollowPathCommand(follower, SpecimenAutoPaths.goToChamberFromZone, 1000, 0.8),
//                WaitCommand(1000),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specimenCommand,
                //FollowPathCommand(follower, SpecimenAutoPaths.goToZoneFromChamber)
            )
        )
    }

}