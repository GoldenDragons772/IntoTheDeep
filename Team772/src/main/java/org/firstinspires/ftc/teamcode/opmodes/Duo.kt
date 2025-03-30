package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.helpers.DriveManager

@TeleOp(name = "Duo TeleOp")
class Duo: CommandOpMode() {

    private lateinit var driveManager: DriveManager

    override fun initialize() {

        val mapping = DriveManager.Mapping(
            gripMapping = Pair(GamepadKeys.Button.X, 2),
            lowclimbMapping = Pair(GamepadKeys.Button.DPAD_LEFT, 2),
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 2),
            highclimbMapping = Pair(GamepadKeys.Button.DPAD_UP, 2),
            climbToHangSpec = Pair(GamepadKeys.Button.DPAD_RIGHT, 2),
            aimMapping = Pair(GamepadKeys.Button.Y, 2),
            parallelMapping = Pair(GamepadKeys.Button.RIGHT_BUMPER, 2),
            clawMapping = Pair(GamepadKeys.Trigger.RIGHT_TRIGGER, 2),
            hangSpecMapping = Pair(GamepadKeys.Button.B, 2),
            transferMapping = Pair(GamepadKeys.Button.A, 2),
        )
        driveManager = DriveManager(hardwareMap, telemetry, gamepad1, gamepad2, mapping)

    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
        driveManager.update()
    }

}