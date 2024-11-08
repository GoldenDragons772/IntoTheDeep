package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.arcrobotics.ftclib.util.Timing
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.DcMotorSimple
import java.util.Timer
import java.util.TimerTask

/**
 * Subsystem responsible for taking in pixels.
 */
class IntakeSystem(hw: HardwareMap) {

    private val slideMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "slideMotor") // Port 3
    private val intakeServo: CRServo = hw.get(CRServo::class.java, "IntakeServo") // Port 1
    private val pivotServo: Servo = hw.get(Servo::class.java, "PivotServo") // Port 2

    private var t: Timer? = null
    private var tt: TimerTask? = null
    private var pivotState = false

    //Create PIDF Controller
    val pidf = PIDFController(kp, ki, kd, kf)

    companion object {
        var extendPos: ExtendPos = ExtendPos.HOME

        //@JvmField is a static keyword because kotlin can't comprehend it.
        @JvmField
        var kp = 28.0

        @JvmField
        var ki = 0.0

        @JvmField
        var kd = 2.0

        @JvmField
        var kf = 0.0

        @JvmField
        var point = 0.0
    }

    enum class ExtendPos(val position: Int) {
        HOME(0),
        TARGET(1150)

    }

    init {

        slideMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        slideMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        slideMotor.direction = DcMotorSimple.Direction.REVERSE
        //We might have to reverse motors
    }

    /**
     * Sets the slide to a certain position ([ExtendPos])
     */
    fun setSlideToPos(pos: ExtendPos) {
        extendPos = pos
        setSlideToPos(pos.position)
    }

    /**
     * Stops and resets the slide motor power
     */
    fun stopResetSlide() {
        slideMotor.power = Constants.SLIDE_MOTOR_SPEED
    }

    /**
     * Function that sets the slide to a position.
     * <br>
     * It also makes sure to block the motor from moving if it goes home to prevent the string from snapping
     * or overheating the motor.
     * @param pos The desired position
     */
    private fun setSlideToPos(pos: Int) {
        //Set the Target Position
        slideMotor.targetPosition = pos

        //Set the motor mode
        slideMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

        //Set the stop behavior for the motor
        slideMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        slideMotor.power = Constants.SLIDE_MOTOR_SPEED

//        pidf.atSetPoint()
//
//        //Sets the pid input to the target position
//        pidf.setPoint = pos.toDouble()

        //
//        while(!pidf.atSetPoint()) {
//            //Get the position of the motor and input it into the pid equation
//            val output = pidf.calculate(slideMotor.currentPosition.toDouble())
//            //set the slideMotor power
//            slideMotor.velocity = output
//        }

        //Continue if the position being moved to is the HOME (0)
        if (pos == 0) {
            //Continue if the current position of the slides greater than 50 (?)
            if (getSlidePosition() > 50) {
                try {
                    //Cancel any previous timers
                    tt?.cancel()
                } catch (e: Exception) {
                    // Do Nothing
                    // If the code breaks, just keep swimming
                } finally {
                    //Initialize the timer
                    t = Timer()
                    //Create timer two
                    tt = object : TimerTask() {
                        //What is this
                        override fun run() {
                            stopResetSlide()
                        }
                    }
                    // Set the timer to 3 seconds and wait
                    t?.schedule(tt, 3000)
                }
            } else {
                // Stop the Motor Since We are already at Home
                slideMotor.power = 0.0
            }
        } else {
            //Since we are not going home, make sure any running timers are cancelled since we won't need them
            try {
                tt?.cancel()
            } catch (e: Exception) {
                // Do Nothing
                // If the code breaks, just keep swimming
            }
        }
    }

    /**
     * Gets the position of the [slideMotor]
     */
    private fun getSlidePosition(): Int {
        return slideMotor.currentPosition
    }

    /**
     * Sets the slide power
     * @param power The power being supplied to the robo
     */
    fun setSlidePower(power: Double) {
        slideMotor.power = power
    }

    fun extend() {
        setSlideToPos(enumValues<ExtendPos>()[1])
    }

    fun retract() {
        setSlideToPos(enumValues<ExtendPos>()[0])
    }

    fun unpivot() {
        pivotServo.position = Constants.PIVOT_SERVO_TARGET
    }

    fun pivot() {
        pivotServo.position = Constants.PIVOT_SERVO_HOME
    }

    //Ayo
    fun suck() {
        intakeServo.power = -1.0
    }

    fun unSuck() {
        intakeServo.power = 1.0
    }

    fun stopSuck() {
        intakeServo.power = 0.0
    }

    //As driver request: Aim and goHome set as toggle.
    //Sucking should be binded to a separate button.

    /**
     * Extend the arm and pivot in preparation to pick up a sample.
     */
    fun aim() {
        extend()
        pivot()
    }

    /**
     * Grab the sample and bring it into the robot.
     */
    fun goHome() {
        unpivot()
        retract()
    }

    /**
     * Transition between pivoted/extended ("aimed") and unpivoted/retracted ("home") states.
     */
    fun aimToggle() {
        if (!pivotState) {
            aim()
        } else {
            goHome()
        }
        pivotState = !pivotState

    }

}