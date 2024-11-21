import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.team772.implementation.Constants
import org.firstinspires.ftc.team772.opmodes.PIDtester
import org.firstinspires.ftc.team772.opmodes.PIDtester.Companion
import java.util.*

//PID???

class ClimbSystem(hw: HardwareMap) {

    companion object {
        var armPos: ArmPos = ArmPos.HOME
    }

    private val leftArmMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "LeftClimb")
    private val rightArmMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "RightClimb")
    private var t: Timer? = null
    private var tt: TimerTask? = null

    enum class ArmPos(val position: Int) {
        HOME(Constants.ARM_HOME),
        LOWCLIMB(Constants.ARM_LOW_CLIMB),
        HIGHCLIMB(Constants.ARM_HIGH_CLIMB),

    }

    init {
        // Brake the Motors
        leftArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        rightArmMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        leftArmMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        // Reverse Motors
        leftArmMotor.direction = DcMotorSimple.Direction.REVERSE
    }

    fun waitForIdle() {
        while (!Thread.currentThread().isInterrupted && leftArmMotor.isBusy && rightArmMotor.isBusy) {
            // Waiting for the motors to become idle
        }
    }

    fun setArmToPos(pos: ArmPos) {
        armPos = pos
        setArmToPos(pos.position)
    }

    fun stopResetArm() {
        leftArmMotor.power = 0.0
        rightArmMotor.power = 0.0
    }

    private fun setArmToPos(pos: Int) {
        leftArmMotor.targetPosition = pos
        rightArmMotor.targetPosition = pos

        leftArmMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        rightArmMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

        leftArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        leftArmMotor.power = 1.0
        rightArmMotor.power = 1.0

        if (pos == 0) {
            if (getAvgArmPosition() > 50) {
                try {
                    tt?.cancel()
                } catch (e: Exception) {
                    // Do Nothing
                } finally {
                    t = Timer()
                    tt = object : TimerTask() {
                        override fun run() {
                            stopResetArm()
                        }
                    }
                    t?.schedule(tt, 3000)
                }
            } else {
                // Stop the Motor Since We are already at Home
                leftArmMotor.power = 0.0
                rightArmMotor.power = 0.0
            }
        } else {
            try {
                tt?.cancel()
            } catch (e: Exception) {
                // Do Nothing
            }
        }
    }

    fun setArmPower(power: Double) {
        leftArmMotor.power = power
        rightArmMotor.power = power
    }

    fun setArmMode(mode: DcMotor.RunMode) {
        if (mode != leftArmMotor.mode || mode != rightArmMotor.mode) {
            leftArmMotor.mode = mode
            rightArmMotor.mode = mode
        }
    }

    fun incrementArmPos() {
        val nextPos = armPos.ordinal + 1
        if (nextPos < ArmPos.values().size) {
            armPos = ArmPos.values()[nextPos]
            setArmToPos(armPos)
        }

        if (leftArmMotor.power == 0.0 || rightArmMotor.power == 0.0) {
            leftArmMotor.power = 1.0
            rightArmMotor.power = 1.0
        }
    }

    fun decrementArmPos() {
        val nextPos = armPos.ordinal - 1
        if (nextPos >= 0) {
            armPos = ArmPos.values()[nextPos]
            setArmToPos(armPos)
        }

        if (leftArmMotor.power == 0.0 || rightArmMotor.power == 0.0) {
            leftArmMotor.power = 1.0
            rightArmMotor.power = 1.0
        }
    }

    private fun getAvgArmPosition(): Int {
        return (leftArmMotor.currentPosition + rightArmMotor.currentPosition) / 2
    }

    fun getArmPos(): ArmPos {
        return armPos
    }
}
