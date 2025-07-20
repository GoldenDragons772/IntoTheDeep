package org.firstinspires.ftc.teamcode.implementation

import android.util.Log
import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.helpers.LogState
import kotlin.math.max

/**
 * ClimbSystem is a subsystem that manages the climbing mechanism of the robot.
 * It controls the climb motors and provides methods to set target positions for climbing.
 * The system uses PID control to adjust the motor power based on the current position of the climb slides.
 */
@Config
class ClimbSystem(private val root: RootSystem, isAuto: Boolean) : SubsystemBase(), LogState {
    override fun stateString(): String =
        "CLIMBSYSTEM targetPosition: $targetPosition state: ${state?.name} slidesPosition: ${this.slidesPosition}"

    /**
     * Enum representing the different states of the climb system.
     * Each state corresponds to a specific target position for the climb slides.
     */
    enum class ClimbState(private val pos: () -> Int) {
        HOME({ CLIMB_HOME }),
        LOW_CHAMBER({ CLIMB_LOW_CHAMBER }),  // Because there are no pointers in java
        LOW_BASKET({ CLIMB_LOW_BASKET }),
        HIGH_CHAMBER({ CLIMB_HIGH_CHAMBER }),
        HIGH_CHAMBER_INVERTED({ CLIMB_HIGH_CHAMBER_INVERTED }),
        HIGH_BASKET({ CLIMB_HIGH_BASKET });

        fun getPos(): Int = pos()
    }

    // Motors for the climbing mechanism.
    private val motors = listOf("climbMotorUp", "climbMotorDown", "climbMotor3").map {
        root.hw.get(DcMotorEx::class.java, it)
    }

    var state: ClimbState? = ClimbState.HOME // TODO: add a third "unbound" state instead of using null reference

    // Timer for tracking elapsed time during PID calculations.
    private val timer = ElapsedTime()

    init {
        if (isAuto) resetEncoder()

        motors[0].direction = DcMotorSimple.Direction.REVERSE
        motors[1].direction = DcMotorSimple.Direction.FORWARD
        motors[2].direction = DcMotorSimple.Direction.FORWARD
    }

    /**
     * Resets the encoder of climbMotor2 and sets it to run without an encoder.
     * This is typically used to reset the position of the climb slides.
     */
    fun resetEncoder() {
        motors[1].mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motors[1].mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    val slidesPosition: Double
        /**
         * Gets the current position of the climb slides in degrees.
         * The position is calculated based on the encoder value of climbMotor2.
         *
         * @return The current position of the climb slides in degrees.
         */
        get() = (max((motors[1].currentPosition * -1).toDouble(), 0.0) / 8192.0 * 360)

    override fun periodic() {
        val error: Double = targetPosition - this.slidesPosition
        val derivative: Double = (error - lastError) / timer.seconds()

        // sum everything up
        val pdfController: Double = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative) + PID_SLIDES.f

        root.telemetry.addData("Slide Position", this.slidesPosition)
        Log.i("ClimbSystem", stateString())

        //        Make sure to stop PIDing when we're home
        if (state == ClimbState.HOME && this.slidesPosition < 75) {
            setMotorPower(0.0)
        } else if (state != null) {
            setMotorPower(pdfController)
        }

        lastError = error
        timer.reset()
    }

    /**
     * Sets the target position for the climb motors.
     *
     * @param climbState The desired climb state to set the target position to.
     * @return A command that sets the target position of the climb motors.
     */
    fun setTargetPosition(climbState: ClimbState): Command {
        return setTargetPosition(climbState.getPos().toDouble()).andThen(InstantCommand({
            this.state = climbState
        }))
    }

    fun setTargetPosition(climbPosition: Double): Command {
        return InstantCommand(Runnable { targetPosition = climbPosition })
    }


    private fun setMotorPower(speed: Double) {
        if (!ENABLED) return
        motors.forEach { it.power = speed * POWER_SCALAR }
    }

    /**
     * Sends the climb motors to a specific speed, ignoring the position.
     *
     * @param speed The speed to set the motors to.
     * @return A command that sets the motors to the specified speed.
     */
    fun overrideClimbController(speed: Double): Command {
        return InstantCommand(Runnable {
            state = null
            setMotorPower(speed)
        })
    }

    var targetPosition = ClimbState.HOME.getPos().toDouble()
    var lastError = 0.0

    companion object {
        @JvmField var CLIMB_HOME = 0
        @JvmField var CLIMB_LOW_CHAMBER = 100
        @JvmField var CLIMB_HIGH_CHAMBER = 420
        @JvmField var CLIMB_LOW_BASKET = 700
        @JvmField var CLIMB_HIGH_BASKET = 2200
        @JvmField var CLIMB_HIGH_CHAMBER_INVERTED = 1200

        // PID coefficients for the climb slides.
        @JvmField var PID_SLIDES = PIDFCoefficients(0.007, 0.00, 0.0001, 0.05)

        // Target position for the climb slides, initialized to the HOME position.
        @JvmField var ENABLED = true
        @JvmField var POWER_SCALAR = 0.8
    }
}