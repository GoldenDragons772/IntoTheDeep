package org.firstinspires.ftc.team772.implementation

import android.util.Log
import org.firstinspires.ftc.team772.implementation.ClimbSystem.Companion.kd
import org.firstinspires.ftc.team772.implementation.ClimbSystem.Companion.kf
import org.firstinspires.ftc.team772.implementation.ClimbSystem.Companion.ki
import org.firstinspires.ftc.team772.implementation.ClimbSystem.Companion.kp
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
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
/**
 * Class that handles the robot's vertical slides and hanging off the bar
 * @param hw The robot's hardwareMap
 * @property SubsystemBase Extends the robot's subsystem commands
 */
@Config
class ClimbSystem(hw: HardwareMap) : SubsystemBase() {

    /**
     * Creates a one time object to hold PID variables
     */
    companion object {
        var armPos: ArmPos = ArmPos.HOME

        @JvmField var kp = 0.5
        @JvmField var kd = 0.5
        @JvmField var ki = 0.5
        @JvmField var kf = 0.5
    }

    //Climb Variables
    /*private*/ val leftArmMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "LeftClimb")
    /*private*/ val rightArmMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "RightClimb")

    //Enum object that holds the values for arm presets
    enum class ArmPos(val position: Int) {
        HOME(Constants.ARM_HOME),
        LOWCLIMB(Constants.ARM_LOW_CLIMB),
        HIGHCLIMB(Constants.ARM_HIGH_CLIMB),
        SPECPREP(Constants.SPEC_HANG_PREP)
    }

    init {
        // Brake the Motors when not moving
        leftArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        //Make sure to stop and reset the encoders upon startup
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

    /**
     * Public function to set the Arm to a position.
     * @param pos The height that the arm should run to (MUST BE ONE OF THE CONSTANT PRESETS)
     */
    fun setArmToPos(pos: ArmPos): InstantCommand {
        return InstantCommand({ //It runs the set arm to position as a command in order to work with the scheduler.
            armPos = pos
            setArmToPos(pos.position).schedule()
        })
    }

    /**
     * Sets the Arm speed to zero
     */
    fun stopResetArm() {
        leftArmMotor.power = 0.0
        rightArmMotor.power = 0.0
    }

    /**
     * The private function that is called by the public setArmToPos function
     * @param pos An integer that represents the climb value
     */
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

    /**
     * Function that returns the Average of the two arms' heights.
     * @return An integer that represents the avg of the two arms
     */
    fun getAvgArmPosition(): Int {
        return (leftArmMotor.currentPosition + rightArmMotor.currentPosition) / 2
    }

    /**
     * Gets the position of the preset that the arms should be at.
     * @return ArmPos object that is the preset position.
     */
    fun getArmPos(): ArmPos {
        return armPos
    }

    // TODO: Modify the climb subsystem so that everything is a command.
    fun highclimb(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.HIGHCLIMB)
    }

    fun lowclimb(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.LOWCLIMB)
    }

    fun unclimb(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.HOME)
    }
    fun specHangPrep(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.SPECPREP)
    }
}

/**
 * Class that runs the arms to where they need to go
 * @property CommandBase Extends the properties of the commandBase
 */
class SetArmPosCommand(
    private val destination: Int,
    private val climbSystem: ClimbSystem,
    private val epsilon: Int = 25
) : CommandBase() {
    val pidf = PIDFController(kp, ki, kd, kf) //Creates a pid controller with the preset values.

    init {
        addRequirements(climbSystem) //Uses the climbsystem object in this command.
    }


    override fun initialize() {
        super.initialize() //Initializes the parent class. This function runs once.

        //Sets the target position of each motor to the destination.
        climbSystem.leftArmMotor.targetPosition = destination
        climbSystem.rightArmMotor.targetPosition = destination

        //Sets the climb motors mode to run to a position.
        climbSystem.leftArmMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        climbSystem.rightArmMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

        //Makes the motors break when power is no applied
        climbSystem.leftArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        climbSystem.rightArmMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        //Run the motors at max speed
        climbSystem.leftArmMotor.power = 1.0
        climbSystem.rightArmMotor.power = 1.0
        pidf.setPoint = destination.toDouble() // Send the destination into the PID loop.
        pidf.setTolerance(25.0)
//        if (destination == 0) {
//            if (pos > 50) {
//                CommandScheduler.getInstance().schedule(SequentialCommandGroup(WaitCommand(3000), InstantCommand({
//                    climbSystem.stopResetArm()
//                })))
//            } else {
//                // Stop the Motor Since We are already at Home
//                climbSystem.stopResetArm()
//            }
//        }
    }

    override fun execute() {
        super.execute() //This function runs in a loop.
        val packet = TelemetryPacket()
        val current_position = climbSystem.getAvgArmPosition().toDouble() // creates a variable that stores the current position of the arms for later refrence
        val output = pidf.calculate(current_position) //Gets the output of the pid loop
        packet.addLine("arm position: ${current_position}") //Debug stuff
        packet.addLine("pidf out: $output")
        packet.addLine("destination: $destination")
        packet.addLine("armPos ${climbSystem.getArmPos()}")
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
        Log.i("ClimbExtension", "Running arms")

//        climbSystem.leftArmMotor.velocity = output
//        climbSystem.rightArmMotor.velocity = output
    }


    override fun isFinished(): Boolean {
        //Return some data when the command is finished
        val pos = climbSystem.getAvgArmPosition()
        return pos in (destination - epsilon)..(destination + epsilon)
//        return true
    }
}