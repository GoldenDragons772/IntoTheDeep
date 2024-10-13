package org.firstinspires.ftc.team772.implementation

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.arcrobotics.ftclib.util.Timing

class IntakeSystem(hw: HardwareMap) {
    private val slideServo: Servo = hw.get(Servo::class.java, "SlideServo") // Port 0
    private val clawServo: Servo = hw.get(Servo::class.java, "ClawServo") // Port 1
    private val pivotServo: Servo = hw.get(Servo::class.java, "PivotServo") // Port 2

    private var clawTimer = Timing.Timer(1) // Create a timer to track the claw closing.

    fun extend() {
        slideServo.position = Constants.SLIDE_SERVO_TARGET
    }

    fun retract() {
        slideServo.position = Constants.SLIDE_SERVO_HOME
    }

    fun ungrab() {
        clawServo.position = Constants.CLAW_SERVO_HOME
    }

    fun grab() {
        clawServo.position = Constants.CLAW_SERVO_TARGET
    }

    fun pivot() {
        pivotServo.position = Constants.PIVOT_SERVO_HOME
    }

    fun unpivot() {
        pivotServo.position = Constants.PIVOT_SERVO_TARGET
    }

    fun aim() {
        // Set the robot up to pick up a sample.
        extend()
        pivot()
        ungrab()
    }

    fun goHome() {
        // Grab the sample and bring it into the robot.
        grab()
        clawTimer.start()
        while (!clawTimer.done()){
          //Do Nothing if the claw is not closed (Robot can still move)
        }
        unpivot()
        retract()

    }

}