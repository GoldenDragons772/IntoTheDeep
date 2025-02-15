package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.team772.helpers.DriveManager

@TeleOp(name = "Demo TeleOp")
class DemoTeleOp : CommandOpMode() {
    private lateinit var driveManager: DriveManager

    /**
     * Runs when init button is pressed on the drive hub. Initializes button mappings and the drive manager.
     */
    override fun initialize() {
        CommandScheduler.getInstance().reset()
        val mapping = DriveManager.Mapping(
            lowclimbMapping = Pair(GamepadKeys.Button.DPAD_LEFT, 1),
            highclimbMapping = Pair(GamepadKeys.Button.DPAD_UP, 1),
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 1),
            aimMapping = Pair(GamepadKeys.Button.Y, 1),
            transferMapping = Pair(GamepadKeys.Button.A, 1),
            swingMapping = Pair(GamepadKeys.Button.B, 1),
            gripMapping = Pair(GamepadKeys.Button.X, 1),
            calibrateMapping = Pair(GamepadKeys.Button.DPAD_RIGHT, 1),
            clawMapping = Pair(GamepadKeys.Trigger.RIGHT_TRIGGER, 1),
            parallelMapping = Pair(GamepadKeys.Button.RIGHT_BUMPER, 1),
            hangSpecMapping = Pair(GamepadKeys.Button.DPAD_RIGHT, 1)
        )
        driveManager = DriveManager(hardwareMap, gamepad1, gamepad2, mapping, 0.5)
    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
        driveManager.update()
    }
}
