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
    private val pivotServo: Servo = hw.get(Servo::class.java, "outPivotServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "outWristServo")
    private val clawServo: Servo = hw.get(Servo::class.java, "outClawServo")

    //State Machine!!!
    var clawState = false
    var homeState = false


    init {
        pivotHome()
    }


    fun pivotHome(): InstantCommand{
        Log.i("ROBO", "Pivoted Pivot")
        return InstantCommand({pivotServo.position = Constants.PIVOT_SERVO_HOME})
    }

    fun pivotScore(): InstantCommand{
        return InstantCommand({pivotServo.position = Constants.PIVOT_SERVO_SCORE})
    }

    fun strikeHome(): InstantCommand{
        return InstantCommand({
            if(homeState) return@InstantCommand
            rstrikeServo.position = Constants.OUT_STRIKE_R_HOME
            lstrikeServo.position = Constants.OUT_STRIKE_L_HOME
            homeState = true
        })
    }

    fun strikeScore(): InstantCommand{
        return InstantCommand({
            if(!homeState) return@InstantCommand
            rstrikeServo.position = Constants.OUT_STRIKE_R_SCORE
            lstrikeServo.position = Constants.OUT_STRIKE_L_SCORE
            homeState = false
        })
    }

    fun wristHome(): InstantCommand{
        return InstantCommand({wristServo.position = Constants.WRIST_SERVO_HOME})
    }

    fun wristScore(): InstantCommand{
        return InstantCommand({wristServo.position = Constants.WRIST_SERVO_TARGET})
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

    fun moveArmToScore(): Command =
        strikeScore()
            .andThen(pivotScore())
            .andThen(wristScore())

    fun toggleArm() = ConditionalCommand(moveArmToScore(), moveArmToHome()) { homeState }

    fun toggleClaw() = ConditionalCommand(clawOpen(), clawClosed()) { clawState }
}