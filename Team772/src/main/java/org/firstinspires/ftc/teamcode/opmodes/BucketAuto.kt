package org.firstinspires.ftc.teamcode.opmodes

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
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.auto.BucketAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.ParallelPlateDrivesystem
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous(name = "Bucket Auto")
class BucketAuto(): CommandOpMode() {
    override fun initialize() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);

        val follower = Follower(hardwareMap, FConstants::class.java, LConstants::class.java)
        val intakeSystem = IntakeSystem(hardwareMap)
        val outtakeSystem = OuttakeSystem(hardwareMap)
        val climbSystem = ClimbSystem(hardwareMap)
        val drivesystem = ParallelPlateDrivesystem(hardwareMap)
        follower.setStartingPose(Pose(5.9, 103.8, Math.toRadians(270.0)))
//        follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                follower.update()
                drivesystem.update()
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