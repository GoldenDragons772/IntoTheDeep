package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.arcrobotics.ftclib.util.Timing
import com.qualcomm.robotcore.hardware.CRServo

class IntakeSystem(hw: HardwareMap) {
    //private val slideMotor: MotorEx = hw.get(MotorEx::class.java, "slideMotor") // Port 0
    private val intakeServo: CRServo = hw.get(CRServo::class.java, "IntakeServo") // Port 1
    private val pivotServo: Servo = hw.get(Servo::class.java, "PivotServo") // Port 2

    private var suckTimer = Timing.Timer(1) // Create a timer to track the claw closing.
    private var pivotState = false

    init {
        //slideMotor.setRunMode(Motor.RunMode.PositionControl)
        //slideMotor.setTargetPosition(1350)
    }

    //gabe should add arm functions here cuz Im dum

    fun extend(){

    }

    fun retract(){

    }

    fun unpivot(){
        pivotServo.position = Constants.PIVOT_SERVO_TARGET
    }

    fun pivot(){
        pivotServo.position = Constants.PIVOT_SERVO_HOME
    }

    //Ayo
    fun suck(){
        intakeServo.power = -1.0;
    }

    fun unSuck(){
        intakeServo.power = 1.0;
    }

    fun stopSuck(){
        intakeServo.power = 0.0;
    }

    //As driver request: Aim and goHome set as toggle.
    //Sucking should be binded to a separate button.

    fun aim() {
        // Set the robot up to pick up a sample.
        extend()
        pivot()
    }

    fun goHome() {
        // Grab the sample and bring it into the robot.
        unpivot()
        retract()

    }

    fun aimToggle() {

        if (!pivotState) {
            extend()
            pivot()
        }
        else {
            unpivot()
            retract()
        }
        pivotState = !pivotState

    }

}