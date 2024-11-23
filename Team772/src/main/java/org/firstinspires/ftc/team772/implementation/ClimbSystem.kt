import ClimbSystem.Companion.kf
import ClimbSystem.Companion.kp
import ClimbSystem.Companion.ki
import ClimbSystem.Companion.kd
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.Subsystem
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.team772.implementation.Constants
import org.firstinspires.ftc.team772.opmodes.PIDtester
import org.firstinspires.ftc.team772.opmodes.PIDtester.Companion
import java.time.Instant
import java.util.*

//PID???

class ClimbSystem(hw: HardwareMap) : SubsystemBase() {

    companion object {
        var armPos: ArmPos = ArmPos.HOME
        val kp = 0.5
        val kd = 0.5
        val ki = 0.5
        val kf = 0.5
    }

    /*private*/ val leftArmMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "LeftClimb")
    /*private*/ val rightArmMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "RightClimb")

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

    fun setArmToPos(pos: ArmPos): InstantCommand {
        return InstantCommand({
            armPos = pos
            setArmToPos(pos.position).schedule()
        })
    }

    fun stopResetArm() {
        leftArmMotor.power = 0.0
        rightArmMotor.power = 0.0
    }

    private fun setArmToPos(pos: Int): Command {
        return SetArmPosCommand(pos, this)
    }

/*
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
*/

/*
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
*/

/*    fun decrementArmPos() {
        val nextPos = armPos.ordinal - 1
        if (nextPos >= 0) {
            armPos = ArmPos.values()[nextPos]
            setArmToPos(armPos)
        }

        if (leftArmMotor.power == 0.0 || rightArmMotor.power == 0.0) {
            leftArmMotor.power = 1.0
            rightArmMotor.power = 1.0
        }
    }*/

    fun getAvgArmPosition(): Int {
        return (leftArmMotor.currentPosition + rightArmMotor.currentPosition) / 2
    }

    fun getArmPos(): ArmPos {
        return armPos
    }
}

class SetArmPosCommand(
    private val destination: Int,
    private val climbSystem: ClimbSystem,
    private val epsilon: Int = 25
) : CommandBase() {
    val pidf = PIDFController(kp, ki, kd, kf)

    init {
        addRequirements(climbSystem)
        climbSystem.leftArmMotor.targetPosition = destination
        climbSystem.rightArmMotor.targetPosition = destination

        climbSystem.leftArmMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        climbSystem.rightArmMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

        climbSystem.leftArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        climbSystem.rightArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        climbSystem.leftArmMotor.power = 1.0
        climbSystem.rightArmMotor.power = 1.0
    }


    override fun initialize() {
        super.initialize()
        val pos = climbSystem.getAvgArmPosition()
        pidf.setPoint = destination.toDouble()
        pidf.setTolerance(25.0)
        if (destination == 0) {
            if (pos > 50) {
                CommandScheduler.getInstance().schedule(SequentialCommandGroup(WaitCommand(3000), InstantCommand({
                    climbSystem.stopResetArm()
                })))
            } else {
                // Stop the Motor Since We are already at Home
                climbSystem.stopResetArm()
            }
        }
    }

    override fun execute() {
        super.execute()
        val output = pidf.calculate(climbSystem.getAvgArmPosition().toDouble())
        climbSystem.leftArmMotor.velocity = output
        climbSystem.rightArmMotor.velocity = output
    }


    override fun isFinished(): Boolean {
        val pos = climbSystem.getAvgArmPosition()
        return pos in (destination - epsilon)..(destination + epsilon)
    }
}