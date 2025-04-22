package org.firstinspires.ftc.teamcode.opmodes.tuning

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

// ~ should always sort last alphabetically
@TeleOp(name = "~DebugWheelFinder") // Disable for comps
class DebugWheelFinderOpMode : LinearOpMode() {
    override fun runOpMode() {
        val FLMotor = hardwareMap.get(DcMotorEx::class.java, "FLMotor")
        val FRMotor = hardwareMap.get(DcMotorEx::class.java, "FRMotor")
        val BLMotor = hardwareMap.get(DcMotorEx::class.java, "BLMotor")
        val BRMotor = hardwareMap.get(DcMotorEx::class.java, "BRMotor")
        FLMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        BLMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        FRMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        BRMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        while (!isStopRequested) {
            val driveSpeed = gamepad1.left_trigger.toDouble()
            FLMotor.power = if (gamepad1.dpad_up) driveSpeed else 0.0
            FRMotor.power = if (gamepad1.dpad_right) driveSpeed else 0.0
            BLMotor.power = if (gamepad1.dpad_down) driveSpeed else 0.0
            BRMotor.power = if (gamepad1.dpad_left) driveSpeed else 0.0
        }
    }
}
