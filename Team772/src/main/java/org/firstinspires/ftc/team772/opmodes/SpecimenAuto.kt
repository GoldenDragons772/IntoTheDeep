package org.firstinspires.ftc.team772.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.follower.Follower
import com.pedropathing.util.Constants
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem
import org.firstinspires.ftc.team772.implementation.commands.FollowPathCommand
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
        val transferSpecimenCommand = TransferSpecimenCommand(intakeSystem, outtakeSystem)
        follower.setStartingPose(Pose(7.852, 55.945, Math.PI))

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
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.SPEC_HANG),
                outtakeSystem.moveArmToScoreSpec(),
                FollowPathCommand(follower,)
            )
        )
    }

}