package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.gamepad.GamepadKeys
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

        val mapping = DriveManager.Mapping(
            climbMapping = Pair(GamepadKeys.Button.DPAD_UP, 1),
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 1)
        )

        driveManager = DriveManager(hardwareMap, gamepad1, gamepad2, mapping)

    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
//        TODO("Add logic: run drive manager")
        while (!isStopRequested){
            driveManager.update()
        }
    }
}