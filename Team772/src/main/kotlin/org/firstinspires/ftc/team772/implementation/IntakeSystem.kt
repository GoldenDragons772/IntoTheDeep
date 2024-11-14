package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.DcMotorSimple
import java.util.Timer
import java.util.TimerTask

class IntakeSystem(hw: HardwareMap) : SubsystemBase() {
    val slideMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "slideMotor") // Port 3
    private val intakeServo: CRServo = hw.get(CRServo::class.java, "IntakeServo") // Port 1
    private val pivotServo: Servo = hw.get(Servo::class.java, "PivotServo") // Port 2

    private var t: Timer? = null
    private var tt: TimerTask? = null

    // States
    // Right now these are all binary, but in the future some of these might need to be in an enum.
    var aimState = false
        private set
    var pivotState = false
        private set
    //Create PIDF Controller
    val pidf = PIDFController(kp, ki, kd, kf)
    var extendPos: ExtendPos = ExtendPos.HOME

    companion object {

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

        @JvmField
        var extendPoint = 1100
    }


    enum class ExtendPos(val position: Int) {
        HOME(Constants.SLIDE_HOME), // Changed to 50 because that's the maximum acceptable minimum value.
        TARGET(Constants.SLIDE_TARGET) // Original Value: 1150
        // TODO: Consider removing this and replacing it with constants
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
        setSlideToPos(ExtendPos.TARGET.position)
    }

    fun retract() {
        setSlideToPos(ExtendPos.HOME.position)
    }

    /**
     * Unpivot the head.
     */
    fun unpivot() {
        pivotServo.position = Constants.PIVOT_SERVO_TARGET // BUG: Maybe seems backwards?
        pivotState = false
    }

    fun pivot() {
        pivotServo.position = Constants.PIVOT_SERVO_HOME
        pivotState = true
    }

    //Ayo
    /**
     * Make the head begin to swallow elements.
     */
    fun swallow() {
        intakeServo.power = 1.0
    }

    /**
     * Make the head spit the elements back out.
     */
    fun spit() {
        intakeServo.power = -1.0
    }

    fun stopSpit() {
        intakeServo.power = 0.0
    }

    //As driver request: Aim and goHome set as toggle.
    //Sucking should be bound to a separate button.

    fun aim() {
        // Set the robot up to pick up a sample.
        if (extendPos != ExtendPos.TARGET) extend() // Enforce our desired state.
        // Wait until slide is extended enough
        while (slideMotor.currentPosition > 800.0) {
            // DO NOTHING
            //TODO: find some other method of executing this besides busywaiting
        }
        if (!pivotState) pivot() // Couldn't physically be pivoted and unextended.
        aimState = true
    }

    /**
     * Grab the sample and bring it into the robot.
     */
    fun goHome() {
        if (pivotState) unpivot()
        if (extendPos != ExtendPos.HOME) retract()
        aimState = false
    }

    fun aimToggle() = if (!aimState) aim() else goHome()
}