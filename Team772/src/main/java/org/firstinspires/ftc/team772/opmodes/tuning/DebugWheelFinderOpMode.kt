package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "DebugWheelFinder") // Disable for comps
class DebugWheelFinderOpMode : LinearOpMode() {
    override fun runOpMode() {
        val FLMotor = MotorEx(hardwareMap, "FLMotor")
        val FRMotor = MotorEx(hardwareMap, "FRMotor")
        val BLMotor = MotorEx(hardwareMap, "BLMotor")
        val BRMotor = MotorEx(hardwareMap, "BRMotor")
        FLMotor.setRunMode(Motor.RunMode.RawPower)
        BLMotor.setRunMode(Motor.RunMode.RawPower)
        FRMotor.setRunMode(Motor.RunMode.RawPower)
        BRMotor.setRunMode(Motor.RunMode.RawPower)
        while (!isStopRequested){
            val driveSpeed = gamepad1.left_trigger.toDouble()
            if (gamepad1.dpad_up) {
                FLMotor.set(driveSpeed)
            }
            else {
                FLMotor.set(0.0)
            }
            if (gamepad1.dpad_right){
                FRMotor.set(driveSpeed)
            }
            else {
                FRMotor.set(0.0)
            }
            if (gamepad1.dpad_down){
                BRMotor.set(driveSpeed)
            }
            else {
                BRMotor.set(0.0)
            }

            if (gamepad1.dpad_left){
                BLMotor.set(driveSpeed)
            }
            else {
                BLMotor.set(0.0)
            }
        }
    }
}
