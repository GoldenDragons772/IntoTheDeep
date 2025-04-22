package org.firstinspires.ftc.teamcode.implementation

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.max

enum class ClimbState(val position: Double) {
    HOME(0.0),
    LOW_CHAMBER(100.0),
    LOW_BASKET(1100.0),
    HIGH_CHAMBER(420.0),
    HIGH_CHAMBER_INVERTED(1350.0),
    HIGH_BASKET(2200.0),
    UNSHACKLED(-1.0);
}

@Config
class ClimbSystem(private val root: RootSystem, isAuto: Boolean) {
    companion object {
        val PID_SLIDES: PIDFCoefficients = PIDFCoefficients(0.007, 0.00, 0.0001, 0.05)
    }


    private val climbMotor1 = root.hw.get(DcMotorEx::class.java, "climbMotorUp")
    private val climbMotor2 = root.hw.get(DcMotorEx::class.java, "climbMotorDown")
    private val climbMotor3 = root.hw.get(DcMotorEx::class.java, "climbMotor3")
    private val timer = ElapsedTime()
    private val lastSlideChangeTimer = ElapsedTime()


    private var lastError: Double = 0.0
    private val slidesPosition: Double
        get() = (max((climbMotor2.currentPosition * -1).toDouble(), 0.0) / 8192.0 * 360)
    private var targetPosition: Double = ClimbState.HOME.position
    var climbState: ClimbState = ClimbState.HOME
        set(value) {
            field = value
            if (field != ClimbState.UNSHACKLED) targetPosition = field.position
        }

    init {
        if (isAuto) resetEncoder()
        climbMotor1.direction = DcMotorSimple.Direction.REVERSE
        climbMotor3.direction = DcMotorSimple.Direction.FORWARD
        climbMotor2.direction = DcMotorSimple.Direction.FORWARD
    }

    private fun resetEncoder() {
        climbMotor2.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        climbMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    fun set(value: ClimbState){
        climbState = value
        if (climbState != ClimbState.UNSHACKLED) targetPosition = climbState.position
    }
    suspend fun periodic() {
        val error = targetPosition - this.slidesPosition
        val derivative = (error - lastError) / timer.seconds()

        // sum everything up
        val pdfOut = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative) + PID_SLIDES.f

        root.telemetry.addData("Slide Position", this.slidesPosition)

        //Make sure to stop PIDing when we're home
        if (climbState != ClimbState.UNSHACKLED) {
            val power = if (climbState == ClimbState.HOME && slidesPosition < 75) 0.0 else pdfOut
            setPower(power)
        }

        // reset if the slides aren't moving and we're at home (supposedly)
        if (derivative > 0.01) { // Value probably needs tuning
            lastSlideChangeTimer.reset()
        }
        if (lastSlideChangeTimer.milliseconds() > 150 && climbState == ClimbState.HOME) {
            resetEncoder()
        }
        lastError = error
        timer.reset()
    }

    fun setPower(power: Double) {
        climbMotor1.power = power
        climbMotor2.power = power
        climbMotor3.power = power
    }

    fun sendRawMotors(speed: Double) {
        climbState = ClimbState.UNSHACKLED
        setPower(speed)
    }

}
