package org.firstinspires.ftc.team772.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.commands.FollowPath
import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.util.Constants
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.team772.auto.BucketAutoPaths
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem
import org.firstinspires.ftc.team772.pedroPathing.constants.FConstants
import org.firstinspires.ftc.team772.pedroPathing.constants.LConstants

@Autonomous(name = "BucketAuto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        val follower = Follower(hardwareMap)
        val intakeSystem = IntakeSystem(hardwareMap)
        val outtakeSystem = OuttakeSystem(hardwareMap)
        val climbSystem = ClimbSystem(hardwareMap)
        follower.setStartingPose(Pose(5.9, 103.8, Math.toRadians(270.0)))
//        follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                follower.update()
                if (follower.isBusy) follower.telemetryDebug(telemetry)
            }),
            SequentialCommandGroup(
                // preload
                FollowPath(follower, BucketAutoPaths.scorePreloadPath, true, 0.2),
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET),
                outtakeSystem.moveArmToScore(),
                WaitCommand(1000),
                outtakeSystem.toggleClaw(),
                outtakeSystem.moveArmToHome(),
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                //Field sample 1
                FollowPath(follower, BucketAutoPaths.moveToFirstSample, true, 0.2),
                intakeSystem.moveToTarget()






                //

            )
        )
    }

}