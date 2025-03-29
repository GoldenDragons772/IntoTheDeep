package org.firstinspires.ftc.teamcode.opmodes

import androidx.core.math.MathUtils.clamp
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.ParallelCommandGroup
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.commands.FollowPath
import com.pedropathing.follower.Follower
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

        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry);

        val follower = Follower(hardwareMap, FConstants::class.java, LConstants::class.java)
        follower.setupConstants(FConstants::class.java, LConstants::class.java)
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
        follower.setStartingPose(Pose(7.700, 53.500, Math.toRadians(180.0)))

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
                FollowPath(follower, SpecimenAutoPaths.preload, true, 1.0)
                .andThen(
                    outtakeSystem.toggleClaw(),
                    WaitCommand(150),
                    outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE)
                ),
                ParallelCommandGroup(
                    //Knock the Specimens
                    FollowPath(follower, SpecimenAutoPaths.knockSpecsIntoZone, true, 0.9),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                //Spec 2
                outtakeSystem.moveArmToHome(),
                WaitCommand(500),
                FollowPath(follower, SpecimenAutoPaths.spec1, true, 0.8).setMaxPower(0.8),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.goToChamberFromZone, true, 0.9)
                    .andThen(outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE)),
                outtakeSystem.toggleClaw(),
                WaitCommand(250),
                ParallelCommandGroup(
                    FollowPath(follower, SpecimenAutoPaths.goToZoneFromChamber, true, 0.9),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                //Spec 3
                FollowPath(follower, SpecimenAutoPaths.pickSpecimenPreloadPath2, true, 0.8).setMaxPower(0.8),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.goToChamberFromZone2, true, 0.9)
                    .andThen(outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE)),
                outtakeSystem.toggleClaw(),
                WaitCommand(250),
                ParallelCommandGroup(
                    FollowPath(follower, SpecimenAutoPaths.goToZoneFromChamber, true, 0.9),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                //Spec 4
                FollowPath(follower, SpecimenAutoPaths.pickSpecimenPreloadPath3, true, 0.8).setMaxPower(0.8),
                outtakeSystem.toggleClaw(),
                WaitCommand(500),
                specimenCommand,
                FollowPath(follower, SpecimenAutoPaths.goToChamberFromZone3, true, 0.9)
                    .andThen(outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE)),
                outtakeSystem.toggleClaw(),
                WaitCommand(250),
                ParallelCommandGroup(
                    FollowPath(follower, SpecimenAutoPaths.goToZoneFromChamber, true, 0.9),
                    WaitCommand(800).andThen(
                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                        outtakeSystem.moveArmToHome(),
                        intakeSystem.moveToHome(),
                    )
                ),
                //Spec 5
                FollowPath(follower, SpecimenAutoPaths.pickSpecimenPreloadPath4, true, 0.8),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(500),
//                specimenCommand,
//                FollowPath(follower, SpecimenAutoPaths.goToChamberFromZone4, true, 0.9)
//                    .andThen(outtakeSystem.setPivot(OuttakeSystem.OuttakePosition.SAFE)),
//                //WaitCommand(500),
//                outtakeSystem.toggleClaw(),
//                WaitCommand(250),
//                ParallelCommandGroup(
//                    FollowPath(follower, SpecimenAutoPaths.goToZoneFromChamber, true, 0.9),
//                    WaitCommand(800).andThen(
//                        climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
//                        outtakeSystem.moveArmToHome(),
//                        intakeSystem.moveToHome(),
//                    )
//                )


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