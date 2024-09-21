package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.abstractions.ClimbExtension
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.helpers.DriveManager
import org.firstinspires.ftc.team772.implementation.Constants
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem

@TeleOp(name = "DummyTeleOp")
class DummyTeleOp : CommandOpMode() {
    private lateinit var driveManager: DriveManager


    /**
     * Runs when init button is pressed on the drive hub. Initializes button mappings and the drive manager.
     */
    override fun initialize() {
        driveManager = DriveManager(hardwareMap, gamepad1, gamepad2)

//        TODO("Create mappings and initialize drive manager")
    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
//        TODO("Add logic: run drive manager")
        while (!isStopRequested){
            driveManager.update()
            override val hw: HardwareMap
            val robot: ControlSystem = Constants.CURRENT_IMPLEMENTATION(hw)

            if (gamepad1.dpad_up){

                robot.climb()

            }
            if (gamepad1.dpad_down){

                robot.unClimb()

            }
        }
    }
}