package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.command.WaitCommand
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

/**
 * Controls the outtake system and its related servos.
 * It does not control the climb system, which must be changed separately in order to actually climb.
 */
class OuttakeSystem(hw: HardwareMap): SubsystemBase() {

    // Defines servos
    private val rstrikeServo: Servo = hw.get(Servo::class.java, "rStrikeServo")
    private val lstrikeServo: Servo = hw.get(Servo::class.java, "lStrikeServo")
    private val pivotServo: Servo = hw.get(Servo::class.java, "outPivotServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "outWristServo")
    private val clawServo: Servo = hw.get(Servo::class.java, "outClawServo")


    // State Machine
    var clawState = false
    var wristState = false
    var homeState = false
    var specState = false

    enum class OuttakePosition{
        HOME,
        TARGET,
        SPEC_TARGET,
        TRANSFER,
        SAFE
    }

    init {
        lstrikeServo.direction = Servo.Direction.REVERSE
        clawServo.direction = Servo.Direction.REVERSE

        rstrikeServo.position = Constants.OUT_STRIKE_R_SCORE
        lstrikeServo.position = Constants.OUT_STRIKE_L_SCORE

        pivotServo.position = Constants.PIVOT_SERVO_SCORE
        wristServo.position = Constants.WRIST_SERVO_TARGET
        clawServo.position = Constants.CLAW_SERVO_TARGET
    }

    @JvmName("getSpecStateJaavaaa")
    fun getSpecState(): Boolean{
        return homeState
    }

    fun setPivot(pos: OuttakePosition): InstantCommand {

        return when(pos){
            OuttakePosition.HOME -> {
                InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_HOME })
            }

            OuttakePosition.TRANSFER -> {
                InstantCommand({pivotServo.position = Constants.PIVOT_SERVO_TRANSFER})
            }

            OuttakePosition.TARGET -> {
                InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SCORE })
            }

            OuttakePosition.SPEC_TARGET -> {
                InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SPEC })
            }

            OuttakePosition.SAFE -> {
                InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SAFE })
            }
        }

    }

    fun setStrike(pos: OuttakePosition): InstantCommand {

        when(pos){
            OuttakePosition.HOME -> {
                return InstantCommand({
                    rstrikeServo.position = Constants.OUT_STRIKE_R_HOME
                    lstrikeServo.position = Constants.OUT_STRIKE_L_HOME
                })
            }
            OuttakePosition.TARGET ->{
                return InstantCommand({
                    rstrikeServo.position = Constants.OUT_STRIKE_R_SCORE
                    lstrikeServo.position = Constants.OUT_STRIKE_L_SCORE
                })
            }
            OuttakePosition.SPEC_TARGET -> {
                return InstantCommand({
                    rstrikeServo.position = Constants.OUT_STRIKE_R_SPEC
                    lstrikeServo.position = Constants.OUT_STRIKE_L_SPEC
                })
            }
            OuttakePosition.TRANSFER -> {
                return InstantCommand({
                    rstrikeServo.position = Constants.OUT_STRIKE_R_TRANSFER
                    lstrikeServo.position = Constants.OUT_STRIKE_L_TRANSFER
                })
            }
            OuttakePosition.SAFE -> {
                return InstantCommand({
                    rstrikeServo.position = Constants.OUT_STRIKE_R_SAFE
                    lstrikeServo.position = Constants.OUT_STRIKE_L_SAFE
                })
            }
        }

    }

    /**
     * Moves the wrist servo to the home position.
     * @return A command to be executed later.
     */
    fun wristHome(): InstantCommand {
        return InstantCommand({
            wristState = false
            wristServo.position = Constants.WRIST_SERVO_HOME })
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
        setStrike(OuttakePosition.HOME)
            .andThen(setPivot(OuttakePosition.HOME))
            .andThen(wristHome())
            .andThen(InstantCommand({homeState = true}))

    /**
     * Moves the entire arm to the scoring position.
     * @return A command to be executed later.
     */
    fun moveArmToScore(): Command =
        setStrike(OuttakePosition.TARGET)
            .andThen(setPivot(OuttakePosition.TARGET))
            .andThen(wristScore())
            .andThen(InstantCommand({specState = false}))

    fun moveArmToScoreSpec(): Command =
        setStrike(OuttakePosition.SPEC_TARGET)
            .andThen(setPivot(OuttakePosition.SPEC_TARGET))
            .andThen(wristScore())
            .andThen(InstantCommand({homeState = false}))

    fun moveArmToTransfer(): Command =
        wristHome()
            .andThen(WaitCommand(200))
            .andThen(setPivot(OuttakePosition.TRANSFER))
            .andThen(WaitCommand(200))
            .andThen(setStrike(OuttakePosition.TRANSFER))
            .andThen(InstantCommand({specState = false}))

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