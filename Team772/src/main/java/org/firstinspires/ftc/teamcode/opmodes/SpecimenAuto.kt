package org.firstinspires.ftc.teamcode.opmodes

import androidx.core.math.MathUtils.clamp
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.ParallelCommandGroup
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.commands.FollowPath
import com.pedropathing.follower.Follower
import com.pedropathing.localization.GoBildaPinpointDriver
import com.pedropathing.localization.Localizer
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.auto.SpecimenAutoPaths
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.ParallelPlateDrivesystem
import org.firstinspires.ftc.teamcode.implementation.commands.AutoSpecWallCommand
import org.firstinspires.ftc.teamcode.implementation.commands.AutoSpecimenCommand
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous(name = "Specimen Auto")
class SpecimenAuto : CommandOpMode() {
    override fun initialize() {

        //val pinpoint: GoBildaPinpointDriver = hardwareMap.get(GoBildaPinpointDriver::class.java, "pinpoint");

        CommandScheduler.getInstance().reset()

        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);

        val follower = Follower(hardwareMap, FConstants::class.java, LConstants::class.java)
        follower.setupConstants(FConstants::class.java, LConstants::class.java)

        follower.setStartingPose(Pose(8.50, 53.500, Math.toRadians(180.0)))

        val intakeSystem = IntakeSystem(hardwareMap)
        val outtakeSystem = OuttakeSystem(hardwareMap)
        val climbSystem = ClimbSystem(hardwareMap)
       // val drivesystem = ParallelPlateDrivesystem(hardwareMap) // The bulk read code could be pulled out of here

       // var batteryVoltage = drivesystem.voltageSensor.voltage
        val nominalVoltage = 13.8

        // reset the encoder only in auto
       // climbSystem.resetEncoders()

        val specimenCommand = AutoSpecimenCommand(intakeSystem, outtakeSystem, climbSystem)
        val specWallCommand = AutoSpecWallCommand(intakeSystem, outtakeSystem, climbSystem)

        climbSystem.resetEncoders()

       // outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.HOME)
        //outtakeSystem.setStrike(OuttakeSystem.OuttakePosition.HOME)
//        follower.setMaxPower(0.8)


        //Voltage Compensation!!!
//        fun scaleFollowerPower(): Double {
//            // scale the follower maxPower based on the battery voltage
//            val maxPowerAdjusted = clamp(nominalVoltage / batteryVoltage, 0.0, 1.0)
//            val scaledPower = maxPowerAdjusted * 1.0
//            return scaledPower
//        }
//
//        fun getFilteredBatteryVoltage(): Double {
//            val alpha = 0.8  // for a low pass filter on battvoltage
//            val newBatteryVoltage = drivesystem.voltageSensor.voltage
//            batteryVoltage = alpha * newBatteryVoltage + (1 - alpha) * batteryVoltage
//            return batteryVoltage
//        }

        //The actual auto code
        schedule(
            InstantCommand({
//                pinpoint.resetPosAndIMU()
//                follower.setStartingPose(Pose(8.50, 53.500, Math.toRadians(180.0)))
                climbSystem.resetEncoders()
            }),
            outtakeSystem.clawClose(),
            outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE),
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                follower.update()
                //drivesystem.update()
                if (follower.isBusy) follower.telemetryDebug(telemetry)
                if(follower.isRobotStuck) {
                    outtakeSystem.moveArmToScore()
                }
            }),
            SequentialCommandGroup(
                //Preload
                outtakeSystem.setStrike(OuttakeSystem.OuttakePosition.HOME),
                outtakeSystem.clawClose(),
                specimenCommand, // score position
                FollowPath(follower, SpecimenAutoPaths.preload(), true, 0.9)
                    .andThen(
                        outtakeSystem.toggleClaw(),
                        WaitCommand(150),
                        outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE)
                    ),

                ParallelCommandGroup(
                    //Knock the Specimens
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    ),
                    FollowPath(follower, SpecimenAutoPaths.knockSpecsIntoZone(), false, 0.9),
                ),
                //Spec 2
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.spec1(), true, 0.9),
                WaitCommand(150),
                outtakeSystem.toggleClaw(),
                outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                ParallelCommandGroup(
                    FollowPath(follower, SpecimenAutoPaths.grab2(), true, 0.8),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                // spec3
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.spec2(), true, 0.9),
                WaitCommand(150),
                outtakeSystem.toggleClaw(),
                outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                ParallelCommandGroup(
                    FollowPath(follower, SpecimenAutoPaths.grab3(), true, 0.8),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                // spec4
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.spec3(), true, 0.9),
                WaitCommand(150),
                outtakeSystem.toggleClaw(),
                outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                ParallelCommandGroup(
                    FollowPath(follower, SpecimenAutoPaths.grab4(), true, 0.8),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                // spec5
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.spec4(), true, 0.9),
            )
        )
    }

}