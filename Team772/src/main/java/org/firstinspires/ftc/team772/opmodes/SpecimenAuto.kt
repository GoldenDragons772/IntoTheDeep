package org.firstinspires.ftc.team772.opmodes

import android.util.Log
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.*
import com.pedropathing.follower.Follower
import com.pedropathing.util.Constants
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.team772.auto.SpecimenAutoPaths
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem
import org.firstinspires.ftc.team772.implementation.commands.AutoSpecWallCommand
import org.firstinspires.ftc.team772.implementation.commands.AutoSpecimenCommand
import org.firstinspires.ftc.team772.implementation.commands.FollowPathCommand
import org.firstinspires.ftc.team772.implementation.commands.SpecimenCommand
import org.firstinspires.ftc.team772.implementation.commands.TransferSpecimenCommand
import org.firstinspires.ftc.team772.pedroPathing.constants.FConstants
import org.firstinspires.ftc.team772.pedroPathing.constants.LConstants

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : CommandOpMode() {
    override fun initialize() {
        var initialTime = System.currentTimeMillis()
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);
        Constants.setConstants(FConstants::class.java, LConstants::class.java)

        val follower = Follower(hardwareMap)
        val intakeSystem = IntakeSystem(hardwareMap)
        val outtakeSystem = OuttakeSystem(hardwareMap)
        val climbSystem = ClimbSystem(hardwareMap)
        val specimenCommand = AutoSpecimenCommand(intakeSystem, outtakeSystem, climbSystem)
        val specWallCommand = AutoSpecWallCommand(intakeSystem, outtakeSystem, climbSystem)
        follower.setStartingPose(Pose(7.5, 53.5, Math.PI))
//        follower.setMaxPower(0.8)

        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive).andThen(InstantCommand({initialTime = System.currentTimeMillis()})),
            RunCommand({
                follower.update()
                if (follower.isBusy) follower.telemetryDebug(telemetry)
            }),
            SequentialCommandGroup(
                //Preload
                outtakeSystem.clawClose(),
                specimenCommand, // score position
                FollowPathCommand(follower, SpecimenAutoPaths.scoreFirstSpecimenPath, 1000, 0.9),
                WaitCommand(500).andThen(outtakeSystem.toggleClaw()),
                specWallCommand,
                FollowPathCommand(follower, SpecimenAutoPaths.knockSpecsIntoZone, 15000).setMaxPower(.95),
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                InstantCommand({
                    Log.i("ROBO", "Spec 1 in ${(System.currentTimeMillis() - initialTime)/1000.0}s.")
                }),

                //Spec 2
                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath, 2000).setMaxPower(0.9),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPathCommand(follower, SpecimenAutoPaths.goToChamberFromZone, 5000).setMaxPower(0.9),
                outtakeSystem.toggleClaw(),

                WaitCommand(500),

                specWallCommand,
                FollowPathCommand(follower, SpecimenAutoPaths.goToZoneFromChamber, 5000).setMaxPower(0.9),
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                InstantCommand({
                    Log.i("ROBO", "Spec 2 in ${(System.currentTimeMillis() - initialTime)/1000.0}s.")
                }),

                //Spec 3
                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath2, 2000).setMaxPower(0.9),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPathCommand(follower, SpecimenAutoPaths.goToChamberFromZone2, 5000).setMaxPower(0.9),
                //WaitCommand(500),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specWallCommand,
                FollowPathCommand(follower, SpecimenAutoPaths.goToZoneFromChamber, 5000).setMaxPower(0.9),
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                InstantCommand({
                    Log.i("ROBO", "Spec 3 in ${(System.currentTimeMillis() - initialTime)/1000.0}s.")
                }),


                //Spec 4
                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath3, 2000).setMaxPower(0.9),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                InstantCommand({
                    Log.i("ROBO", "Spec 4 in ${(System.currentTimeMillis() - initialTime)/1000.0}s.")
                }),

                FollowPathCommand(follower, SpecimenAutoPaths.goToChamberFromZone3, 5000).setMaxPower(0.9),
                //WaitCommand(500),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specWallCommand,
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                InstantCommand({
                    Log.i("ROBO", "Completed Auto in ${(System.currentTimeMillis() - initialTime)/1000.0}s.")
                })
//                FollowPathCommand(follower, SpecimenAutoPaths.goToZoneFromChamber).setMaxPower(0.9),
//                //Spec 5
//                FollowPathCommand(follower, SpecimenAutoPaths.pickSpecimenPreloadPath4, 2000).setMaxPower(0.9),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specimenCommand,
//                FollowPathCommand(follower, SpecimenAutoPaths.goToChamberFromZone4).setMaxPower(0.9),
//                //WaitCommand(500),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specWallCommand,
//                FollowPathCommand(follower, SpecimenAutoPaths.goToZoneFromChamber).setMaxPower(0.9)
            )
        )
    }

}