package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.helpers.AllianceSelector
import org.firstinspires.ftc.teamcode.helpers.DriveManager

@TeleOp(name = "Duo TeleOp")
class Duo: CommandOpMode() {

    private lateinit var driveManager: DriveManager

    override fun initialize() {

        val mapping = DriveManager.Mapping(
            gripMapping = gamepad2::x,
            lowclimbMapping = gamepad2::dpad_left,
            unClimbMapping = gamepad2::dpad_down,
            highclimbMapping = gamepad2::dpad_up,
            climbToHangSpec = gamepad2::dpad_right,
            aimMapping = gamepad2::y,
            wristMappingLeft = gamepad2::left_bumper,
            wristMappingRight = gamepad2::right_bumper,
            clawMapping = gamepad2::right_trigger,
            hangSpecMapping = gamepad2::b,
            transferMapping = gamepad2::a,
            climbUpMapping = gamepad1::right_bumper,
            climbDownMapping = gamepad1::left_bumper,
            moveIntakeMapping = gamepad2::left_trigger
        )
        driveManager = DriveManager(hardwareMap, telemetry, gamepad1, gamepad2, mapping, AllianceSelector.selectAlliance(gamepad1, telemetry))

    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
        driveManager.update()
    }

}