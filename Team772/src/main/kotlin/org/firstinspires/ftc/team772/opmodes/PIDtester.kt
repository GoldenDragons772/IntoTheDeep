package org.firstinspires.ftc.team772.opmodes

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.controller.PIDFController
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@Config
@TeleOp(name = "PIDtester")
class PIDtester : LinearOpMode() {
    companion object {
        @JvmField var kp = 1.0
        @JvmField var ki = 0.0
        @JvmField var kd = 0.0
        @JvmField var kf = 0.0
        @JvmField var point = 1200.0
    }

    override fun runOpMode() {
        val pdMotor = MotorEx(hardwareMap, "PIDmotor")
        pdMotor.setRunMode(Motor.RunMode.RawPower)

        waitForStart()

        while (!isStopRequested) {
            val pidf = PIDFController(kp, ki, kd, kf)
            pidf.setPoint = point;

            val output = pidf.calculate(pdMotor.currentPosition.toDouble())
            pdMotor.velocity = output;
        }
    }
}