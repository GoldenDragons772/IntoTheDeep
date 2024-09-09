package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "DebugWheelFinder") // Disable for comps
class DebugWheelFinderOpMode : LinearOpMode() {
    override fun runOpMode() {
        val FLMotor = MotorEx(hardwareMap, "Motor1")
        val BLMotor = MotorEx(hardwareMap, "Motor2")
        val FRMotor = MotorEx(hardwareMap, "Motor3")
        val BRMotor = MotorEx(hardwareMap, "Motor4")
        FLMotor.setRunMode(Motor.RunMode.RawPower)
        BLMotor.setRunMode(Motor.RunMode.RawPower)
        FRMotor.setRunMode(Motor.RunMode.RawPower)
        BRMotor.setRunMode(Motor.RunMode.RawPower)
        while (!isStopRequested){
            if (gamepad1.dpad_up) {
                FLMotor.set(1.0)
            }
            else {
                FLMotor.set(0.0)
            }
            if (gamepad1.dpad_right){
                FRMotor.set(1.0)
            }
            else {
                FRMotor.set(0.0)
            }
            if (gamepad1.dpad_down){
                BRMotor.set(1.0)
            }
            else {
                BRMotor.set(0.0)
            }

            if (gamepad1.dpad_left){
                BLMotor.set(1.0)
            }
            else {
                BLMotor.set(0.0)
            }
        }
    }
}