package org.firstinspires.ftc.team772.implementation

import android.util.Log
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class OuttakeSystem(hw: HardwareMap) {


    //Defines servos
    private val rstrikeServo: Servo = hw.get(Servo::class.java, "rStrikeServo")
    private val lstrikeServo: Servo = hw.get(Servo::class.java, "lStrikeServo")
    private val pivotServo: Servo = hw.get(Servo::class.java, "pivotServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "wristServo")
    private val clawServo: Servo = hw.get(Servo::class.java, "clawServo")

    //States
    var clawState = false


    init {
        pivotHome()
    }


    fun pivotHome(): InstantCommand{
        Log.i("ROBO", "Pivoted Pivot")
        return InstantCommand({pivotServo.position = Constants.PIVOT_SERVO_HOME})
    }

    fun strikeHome(): InstantCommand{
        return InstantCommand({
            rstrikeServo.position = Constants.OUT_STRIKE_R_HOME
            lstrikeServo.position = Constants.OUT_STRIKE_L_HOME
        })
    }

    fun wristHome(): InstantCommand{
        return InstantCommand({wristServo.position = Constants.WRIST_SERVO_HOME})
    }

    fun clawOpen(): InstantCommand{
        return InstantCommand({
            if(!clawState) return@InstantCommand
            clawServo.position = Constants.CLAW_SERVO_TARGET
            clawState = false})
    }

    fun clawClosed(): InstantCommand{
        return InstantCommand({
            if(clawState) return@InstantCommand
            clawServo.position = Constants.CLAW_SERVO_HOME
            clawState = true})
    }

    fun moveArmToHome(): Command =
        strikeHome()
            .andThen(pivotHome())
            .andThen(wristHome())

    fun toggleClaw() = ConditionalCommand(clawOpen(), clawClosed(), {clawState})
}