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

        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                follower.update()
                follower.setMaxPower(0.8)
                if (follower.isBusy) follower.telemetryDebug(telemetry)
            })
            ,
            SequentialCommandGroup(
                outtakeSystem.clawClose(),
                specimenCommand,
                specimenCommand, // to get it to the right postion
                FollowPathCommand(follower, SpecimenAutoPaths.scoreFirstSpecimenPath),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPathCommand(follower, SpecimenAutoPaths.knockSpecsIntoZone),
                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath, 1000, 0.4),
                outtakeSystem.toggleClaw()
            )
        )
    }

}