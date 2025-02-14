package org.firstinspires.ftc.team772.implementation

import android.util.Log
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

/**
 * Controls the outtake system and its related servos.
 * It does not control the climb system, which must be changed separately in order to actually climb.
 */
class OuttakeSystem(hw: HardwareMap) {

    // Defines servos
    private val rstrikeServo: Servo = hw.get(Servo::class.java, "rStrikeServo")
    private val lstrikeServo: Servo = hw.get(Servo::class.java, "lStrikeServo")
    private val pivotServo: Servo = hw.get(Servo::class.java, "outPivotServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "outWristServo")
    private val clawServo: Servo = hw.get(Servo::class.java, "outClawServo")


    // State Machine
    var clawState = false
    var homeState = false

    init {
        lstrikeServo.direction = Servo.Direction.REVERSE
        rstrikeServo.position = Constants.OUT_STRIKE_R_SCORE
        lstrikeServo.position = Constants.OUT_STRIKE_L_SCORE
        pivotServo.position = Constants.PIVOT_SERVO_SCORE
        wristServo.position = Constants.WRIST_SERVO_TARGET
    }

    /**
     * Moves the pivot servo to the home position.
     */
    fun pivotHome(): InstantCommand {
        Log.i("ROBO", "Pivoted Pivot")
        return InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_HOME })
    }

    fun pivotTransfer(): InstantCommand {
        return InstantCommand({pivotServo.position = Constants.PIVOT_SERVO_TRANSFER})
    }

    /**
     * Moves the pivot servo to the scoring position.
     * @return A command to be executed later.
     */
    fun pivotScore(): InstantCommand {
        return InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SCORE })
    }

    fun pivotScoreSpec(): InstantCommand {
        return InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SPEC })
    }

    /**
     * Moves the strike servos to the home position.
     * @return A command to be executed later.
     */
    fun strikeHome(): InstantCommand {
        return InstantCommand({
            if (homeState) return@InstantCommand
            rstrikeServo.position = Constants.OUT_STRIKE_R_HOME
            lstrikeServo.position = Constants.OUT_STRIKE_L_HOME
            homeState = true
        })
    }

    fun strikeTransfer(): InstantCommand {
        return InstantCommand({
            if (homeState) return@InstantCommand
            rstrikeServo.position = Constants.OUT_STRIKE_R_TRANSFER
            lstrikeServo.position = Constants.OUT_STRIKE_L_TRANSFER
            homeState = true
        })
    }

    /**
     * Moves the strike servos to the scoring position.
     * @return A command to be executed later.
     */
    fun strikeScore(): InstantCommand {
        return InstantCommand({
            if (!homeState) return@InstantCommand
            rstrikeServo.position = Constants.OUT_STRIKE_R_SCORE
            lstrikeServo.position = Constants.OUT_STRIKE_L_SCORE
            homeState = false
        })
    }

    fun strikeScoreSpec(): InstantCommand {
        return InstantCommand({
            if (!homeState) return@InstantCommand
            rstrikeServo.position = Constants.OUT_STRIKE_R_SPEC
            lstrikeServo.position = Constants.OUT_STRIKE_L_SPEC
            homeState = false
        })
    }

    /**
     * Moves the wrist servo to the home position.
     * @return A command to be executed later.
     */
    fun wristHome(): InstantCommand {
        return InstantCommand({ wristServo.position = Constants.WRIST_SERVO_HOME })
    }

    /**
     * Moves the wrist servo to the scoring position.
     * @return A command to be executed later.
     */
    fun wristScore(): InstantCommand {
        return InstantCommand({ wristServo.position = Constants.WRIST_SERVO_TARGET })
    }

    /**
     * Opens the claw to score.
     * @return A command to be executed later.
     */
    fun clawOpen(): InstantCommand {
        return InstantCommand({
            if (!clawState) return@InstantCommand
            clawServo.position = Constants.CLAW_SERVO_TARGET
            clawState = false
        })
    }

    /**
     * Closes the claw, with or without a specimen.
     * @return A command to be executed later.
     */
    fun clawClose(): InstantCommand {
        return InstantCommand({
            if (clawState) return@InstantCommand
            clawServo.position = Constants.CLAW_SERVO_HOME
            clawState = true
        })
    }

    /**
     * Moves the entire arm assembly to the home position.
     * @return A command to be executed later.
     */
    fun moveArmToHome(): Command =
        strikeHome()
            .andThen(pivotHome())
            .andThen(wristHome())

    /**
     * Moves the entire arm to the scoring position.
     * @return A command to be executed later.
     */
    fun moveArmToScore(): Command =
        strikeScore()
            .andThen(pivotScore())
            .andThen(wristScore())

    fun moveArmToScoreSpec(): Command =
        strikeScoreSpec()
            .andThen(pivotScoreSpec())
            .andThen(wristScore())

    fun moveArmToTransfer(): Command =
        strikeTransfer()
            .andThen(pivotTransfer())
            .andThen(wristHome())
    /**
     * Toggles the arm between the home position and the scoring position based on the saved arm state.
     * @return A command to be executed later.
     */
    fun toggleArm() = ConditionalCommand(moveArmToScore(), moveArmToTransfer()) { homeState }

    /**
     * Toggles the arm between the home position and the Specimen scoring position based on the saved arm state.
     * @return A command to be executed later.
     */
    fun toggleArmSpec() = ConditionalCommand(moveArmToScoreSpec(), moveArmToHome()) { homeState }

    /**
     * Toggles teh claw between the open (ready to score) position and the closed position.
     * @return A command to be executed later.
     */
    fun toggleClaw() = ConditionalCommand(clawOpen(), clawClose()) { clawState }
}