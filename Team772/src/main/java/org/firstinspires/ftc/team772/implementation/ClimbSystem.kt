package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class ClimbSystem(hw: HardwareMap): SubsystemBase() {

    val climbMotor1: DcMotorEx = hw.get(DcMotorEx::class.java, "climbMotor1")
    //val climbMotor2: DcMotorEx = hw.get(DcMotorEx::class.java, "climbMotor2")

    //Enum object that holds the values for arm presets
    enum class ArmPos(val position: Int) {
        HOME(Constants.ARM_HOME),
        LOWCLIMB(Constants.ARM_LOW_CLIMB),
        HIGHCLIMB(Constants.ARM_HIGH_CLIMB),
        SPECPREP(Constants.SPEC_HANG_PREP),
        SPECSCORE(Constants.SPEC_HANG),
    }

    init {

        climbMotor1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        //climbMotor2.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        climbMotor1.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        //climbMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        //climbMotor1.direction = DcMotorSimple.Direction.REVERSE

    }

    /**
     * Public function to set the Arm to a position.
     * @param pos The height that the arm should run to (MUST BE ONE OF THE CONSTANT PRESETS)
     */
    fun setArmToPos(pos: ArmPos): InstantCommand {
        return InstantCommand({ //It runs the set arm to position as a command in order to work with the scheduler.
            setArmToPos(pos.position).schedule()
        })
    }

    /**
     * Function that returns the Average of the two arms' heights.
     * @return An integer that represents the avg of the two arms
     */
    fun getArmPosition(): Int {
        return climbMotor1.targetPosition
    }


    /**
     * The private function that is called by the public setArmToPos function
     * @param pos An integer that represents the climb value
     */
    private fun setArmToPos(pos: Int): Command {
        return SetArmPosCommand(pos, this)
    }

    fun highclimb(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.HIGHCLIMB)
    }

    fun lowclimb(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.LOWCLIMB)
    }

    fun unclimb(): InstantCommand {
        return setArmToPos(ClimbSystem.ArmPos.HOME)
    }

    fun specHangPrep(): Command =
        setArmToPos(ArmPos.SPECPREP)

    fun specHangAttach(): Command =
        setArmToPos(ArmPos.SPECSCORE)

}

class SetArmPosCommand(
    private val destination: Int,
    private val climbSystem: ClimbSystem,
    private val epsilon: Int = 25
) : CommandBase() {


    init {
        addRequirements(climbSystem)
    }


    override fun initialize() {
        super.initialize()

        climbSystem.climbMotor1.targetPosition = destination

        climbSystem.climbMotor1.mode = DcMotor.RunMode.RUN_TO_POSITION
        //climbSystem.climbMotor2.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        climbSystem.climbMotor1.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        //climbSystem.climbMotor2.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        climbSystem.climbMotor1.power = 1.0
        //climbSystem.climbMotor2.power = 1.0

    }

    override fun execute() {
        super.execute()
    }

    override fun isFinished(): Boolean {

        //Return some data when the command is finished
        val pos = climbSystem.getArmPosition()
        return pos in (destination - epsilon)..(destination + epsilon)
    }
}

