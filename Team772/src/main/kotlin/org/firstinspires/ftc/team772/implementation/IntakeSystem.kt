package org.firstinspires.ftc.team772.implementation

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class IntakeSystem(hw: HardwareMap) {
    private val slideServo: Servo = hw.get(Servo::class.java, "SlideServo")
    private val clawServo: Servo = hw.get(Servo::class.java, "ClawServo")
    private val pivotServo: Servo = hw.get(Servo::class.java, "PivotServo")

    fun extend() {
        slideServo.position = Constants.SLIDE_SERVO_TARGET
    }

    fun retract() {
        slideServo.position = Constants.SLIDE_SERVO_HOME
    }

    fun grab() {
        clawServo.position = Constants.CLAW_SERVO_TARGET
    }

    fun ungrab() {
        clawServo.position = Constants.CLAW_SERVO_HOME
    }

    fun pivot() {
        pivotServo.position = Constants.PIVOT_SERVO_TARGET
    }

    fun unpivot() {
        pivotServo.position = Constants.PIVOT_SERVO_HOME
    }
}