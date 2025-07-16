package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.helpers.AllianceSelector
import org.firstinspires.ftc.teamcode.helpers.DriveManager

/**
 * Tele-Op mode for the Solo robot.
 *
 * This mode allows a single driver to control the robot's movements and actions.
 * primarily used for practice and testing.
 */
@TeleOp(name = "Solo TeleOp")
class Solo : CommandOpMode() {

    private lateinit var driveManager: DriveManager

    override fun initialize() {

        val mapping = DriveManager.Mapping(
            gripMapping = Pair(GamepadKeys.Button.X, 1),

            lowclimbMapping = Pair(GamepadKeys.Button.DPAD_LEFT, 1),
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 1),
            highclimbMapping = Pair(GamepadKeys.Button.DPAD_UP, 1),
            climbToHangSpec = Pair(GamepadKeys.Button.DPAD_RIGHT, 1),

            aimMapping = Pair(GamepadKeys.Button.Y, 1), // Triangle
            wristMappingLeft = Pair(GamepadKeys.Button.LEFT_BUMPER, 1),
            wristMappingRight = Pair(GamepadKeys.Button.RIGHT_BUMPER, 1),
            clawMapping = Pair(GamepadKeys.Trigger.RIGHT_TRIGGER, 1),
            hangSpecMapping = Pair(GamepadKeys.Button.B, 1), // Circle
            transferMapping = Pair(GamepadKeys.Button.A, 1), // Cross
            climbUpMapping = Pair(GamepadKeys.Button.A, 2), // Cross
            climbDownMapping = Pair(GamepadKeys.Button.B, 2), // Circle
            moveIntakeMapping = Pair(GamepadKeys.Trigger.LEFT_TRIGGER, 1)
        )
        driveManager = DriveManager(
            hardwareMap,
            telemetry,
            gamepad1,
            gamepad2,
            mapping,
            AllianceSelector.selectAlliance(gamepad1, telemetry)
        )
    }


    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
        driveManager.update()
    }

}