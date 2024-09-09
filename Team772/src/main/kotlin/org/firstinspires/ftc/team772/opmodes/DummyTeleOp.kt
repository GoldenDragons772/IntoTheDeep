package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.team772.helpers.DriveManager

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
        }
    }
}