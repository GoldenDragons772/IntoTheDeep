package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.helpers.AllianceSelector
import org.firstinspires.ftc.teamcode.helpers.DriveManager

@TeleOp(name = "Solo TeleOp")
class Solo: LinearOpMode() {

    private lateinit var driveManager: DriveManager

    override fun runOpMode() {
        val mapping = DriveManager.Mapping(
            gripMapping = gamepad1::x,
            lowclimbMapping = gamepad1::dpad_left,
            unClimbMapping =  gamepad1::dpad_down,
            highclimbMapping = gamepad1::dpad_up,
            climbToHangSpec = gamepad1::dpad_right,
            aimMapping = gamepad1::y,
            wristMappingLeft = gamepad1::left_bumper,
            wristMappingRight = gamepad1::right_bumper,
            clawMapping = gamepad1::right_trigger,
            hangSpecMapping = gamepad1::b,
            transferMapping = gamepad1::a,
            climbUpMapping = gamepad2::a,
            climbDownMapping = gamepad2::b,
            moveIntakeMapping = null
        )
        driveManager = DriveManager(hardwareMap,telemetry, gamepad1, gamepad2, mapping,AllianceSelector.selectAlliance(gamepad1, telemetry))
        waitForStart()
        while (!isStopRequested) {
            driveManager.update()
        }
    }

}