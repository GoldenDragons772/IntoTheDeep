package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.helpers.DriveManager

@TeleOp(name = "Solo TeleOp")
class Solo: CommandOpMode() {

    private lateinit var driveManager: DriveManager

    override fun initialize() {
        CommandScheduler.getInstance().reset()

        val mapping = DriveManager.Mapping(
            //swingMapping = Pair(GamepadKeys.Button.A, 1),
            gripMapping = Pair(GamepadKeys.Button.X, 1),

            lowclimbMapping = Pair(GamepadKeys.Button.DPAD_LEFT, 1),
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 1),
            highclimbMapping = Pair(GamepadKeys.Button.DPAD_UP, 1),
            climbToHangSpec = Pair(GamepadKeys.Button.DPAD_RIGHT, 1),

            aimMapping = Pair(GamepadKeys.Button.Y, 1),
            parallelMapping = Pair(GamepadKeys.Button.RIGHT_BUMPER, 1),
            clawMapping = Pair(GamepadKeys.Trigger.RIGHT_TRIGGER, 1),
            hangSpecMapping = Pair(GamepadKeys.Button.B, 1),
            transferMapping = Pair(GamepadKeys.Button.A, 1),

        )
        driveManager = DriveManager(hardwareMap, gamepad1, gamepad2, mapping)

    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
        driveManager.update()
    }

}