package org.firstinspires.ftc.team772.implementation

import android.util.Log
import com.arcrobotics.ftclib.command.*
import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.team772.implementation.IntakeSystem.Companion.kd
import org.firstinspires.ftc.team772.implementation.IntakeSystem.Companion.kf
import org.firstinspires.ftc.team772.implementation.IntakeSystem.Companion.ki
import org.firstinspires.ftc.team772.implementation.IntakeSystem.Companion.kp
import java.util.logging.Logger

class IntakeSystem(hw: HardwareMap) : SubsystemBase() {
    val slideMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "slideMotor") // Port 3
    private val intakeServo: CRServo = hw.get(CRServo::class.java, "IntakeServo") // Port 1
    private val pivotServo: Servo = hw.get(Servo::class.java, "PivotServo") // Port 2

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

        //@JvmField is a static keyword because java can't comprehend it.
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
        TARGET(Constants.SLIDE_TARGET), // Original Value: 1150
        EDGE(Constants.SLIDE_EDGE)
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
    private fun setSlideToPos(pos: Int): SlideCommand {
        val command = SlideCommand(this, pos)
        return command
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

    fun extendCommand(): SlideCommand {
        return setSlideToPos(ExtendPos.TARGET.position)
    }

    fun edgeCommand(): SlideCommand{
        return setSlideToPos(ExtendPos.EDGE.position)
    }

    fun retractCommand(): SlideCommand {
        // TODO: Return if it's already there.
        return setSlideToPos(ExtendPos.HOME.position)
    }

    /**
     * Unpivot the head.
     */
    fun unpivot(): Command {
        return InstantCommand({
            if (!pivotState) return@InstantCommand
            pivotServo.position = Constants.PIVOT_SERVO_TARGET // BUG: Maybe seems backwards?
            pivotState = false
        })
    }

    fun pivot(): InstantCommand {
        return InstantCommand({
            if (pivotState) return@InstantCommand // Return if we're already in the desired state.
            pivotServo.position = Constants.PIVOT_SERVO_HOME
            pivotState = true
            Log.i("ROBO", "true")
        })
    }

    //Ayo
    /**
     * Make the head begin to swallow elements.
     */
    fun swallow(): InstantCommand {
        return InstantCommand({ intakeServo.power = 1.0 })
    }

    /**
     * Make the head spit the elements back out.
     */
    fun spit(): InstantCommand {
        return InstantCommand({ intakeServo.power = -1.0 })
    }

    fun stopSpit(): InstantCommand {
        return InstantCommand({ intakeServo.power = 0.0 })
    }

    //As driver request: Aim and goHome set as toggle.
    //Sucking should be bound to a separate button.

    fun aim(): Command =
        extendCommand().andThen(pivot()).andThen(InstantCommand({ aimState = true }))

    /**
     * Grab the sample and bring it into the robot.
     */
    fun goHome(): Command = unpivot().andThen(retractCommand()).andThen(InstantCommand({ aimState = false }))

    //    fun aimToggle() = if (!aimState) aim() else goHome()
    fun aimToggle() = ConditionalCommand(goHome(), aim()) { aimState }
}

class SlideCommand(private val intake: IntakeSystem, private val position: Int) : CommandBase() {
    private val pidf = PIDFController(kp, ki, kd, kf)
    private var isEnded = false;

    init {
        isEnded = false// Try to reset isEnded every time
        addRequirements(intake)
    }

    override fun initialize() {
        isEnded = false
        //Set the Target Position
        intake.slideMotor.targetPosition = position

        //Set the motor mode
        intake.slideMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

        //Set the stop behavior for the motor
        intake.slideMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        if(position == Constants.SLIDE_EDGE) intake.slideMotor.power = 0.3
        else intake.slideMotor.power = Constants.SLIDE_MOTOR_SPEED
        pidf.setTolerance(25.0)
//        pidf.atSetPoint()
//
//        //Sets the pid input to the target position
        pidf.setPoint = position.toDouble()
//        while(!pidf.atSetPoint()) {
//            //Get the position of the motor and input it into the pid equation
//        }
    }

    override fun execute() {
        if (this.isEnded) return
        val output = pidf.calculate(intake.slideMotor.currentPosition.toDouble())
        //set the slideMotor power
        intake.slideMotor.velocity = output
        Log.i("ROBO", "looping")
    }

    override fun end(interrupted: Boolean) {
        super.end(interrupted)
        Log.i("ROBO", "ended")
        this.isEnded = true
    }

    override fun isFinished(): Boolean {
        // TODO: don't stop when extended
        Log.i("ROBO", "finished looping")
        this.isEnded = true
        return true //THIS NEEDS TO RETURN TRUE!!! IF THE CODE TRIES TO RETURN ANYTHING ELSE, IT WILL LOOP OVER ITSELF UNTILL FOREVER!
    }
}

