package org.firstinspires.ftc.teamcode.implementation

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.command.WaitCommand
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.helpers.LogState

/**
 * Controls the outtake system and its related servos.
 * It does not control the climb system, which must be changed separately in order to actually climb.
 *
 */
class OuttakeSystem(root: RootSystem, private val isAuto: Boolean) : SubsystemBase(), LogState {

    // Defines servos
    private val rstrikeServo: Servo = root.hw.get(Servo::class.java, "vRightStrikeServo")
    private val lstrikeServo: Servo = root.hw.get(Servo::class.java, "vLeftStrikeServo")

    private val pivotServo: Servo = root.hw.get(Servo::class.java, "outPivotServo")
    private val wristServo: Servo = root.hw.get(Servo::class.java, "outWristServo")

    private val clawServo: Servo = root.hw.get(Servo::class.java, "outClawServo")

    //Define Sensors
    private val clawButton: DigitalChannel = root.hw.get(DigitalChannel::class.java, "outLimitSwitch")


    // State Machine
    var clawState = false
    var wristState = false
    var homeState = false
    private var specState = false

    /**
     * Represents the different positions the outtake system can be in.
     * Each position corresponds to a specific servo position.
     */
    enum class OuttakePosition {
        HOME,
        TARGET,
        SPEC_TARGET,
        SPEC_INV,
        TRANSFER_PREP,
        TRANSFER,
        SAFE,
        PRELOAD
    }

    // initialize the servos with initial positions.
    init {
        rstrikeServo.direction = Servo.Direction.REVERSE
        clawServo.direction = Servo.Direction.FORWARD
        pivotServo.direction = Servo.Direction.REVERSE

//        rstrikeServo.position = Constants.OUT_STRIKE_HOME
//        lstrikeServo.position = Constants.OUT_STRIKE_HOME
//
//        pivotServo.position = Constants.PIVOT_SERVO_HOME
//        wristServo.position = Constants.WRIST_SERVO_HOME
//        clawServo.position = Constants.CLAW_SERVO_TARGET
        moveArmToTransferPrep().schedule()
        clawOpen().schedule()

//        rstrikeServo.position = Constants.OUT_STRIKE_SCORE
//        lstrikeServo.position = Constants.OUT_STRIKE_SCORE
        // clawButton.mode = DigitalChannel.Mode.INPUT

    }

    /**
     * Returns the current state of the outtake system.
     * @return true if the outtake system is in the home position, false otherwise.
     */
    fun getSpecState(): Boolean {
        return homeState
    }

    /**
     * Sets the state of the outtake system to the specified position.
     * @param pos The desired position of the outtake system.
     * @return An InstantCommand that sets the pivot servo to the specified position.
     */
    fun setPivot(pos: OuttakePosition): InstantCommand {
        return when (pos) {
            OuttakePosition.HOME -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_HOME })
            OuttakePosition.TRANSFER_PREP -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_TRANSFER })
            OuttakePosition.TRANSFER -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_TRANSFER })
            OuttakePosition.TARGET -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SCORE })
            OuttakePosition.SPEC_TARGET -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SPEC })
            OuttakePosition.SPEC_INV -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SPEC_INV })
            OuttakePosition.SAFE -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_SAFE })
            OuttakePosition.PRELOAD -> InstantCommand({ pivotServo.position = Constants.PIVOT_SERVO_PRELOAD })
        }

    }

    /**
     * Sets the strike servos to the specified position.
     * @param pos The desired position of the strike servos.
     * @return An InstantCommand that sets the positions of the right and left strike servos.
     */
    fun setStrike(pos: OuttakePosition): InstantCommand {

        return when (pos) {
            OuttakePosition.HOME -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_HOME
                lstrikeServo.position = Constants.OUT_STRIKE_HOME
            })

            OuttakePosition.TARGET -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_SCORE
                lstrikeServo.position = Constants.OUT_STRIKE_SCORE
            })

            OuttakePosition.SPEC_TARGET -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_SPEC
                lstrikeServo.position = Constants.OUT_STRIKE_SPEC
            })

            OuttakePosition.TRANSFER_PREP -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_TRANSFER_PREP
                lstrikeServo.position = Constants.OUT_STRIKE_TRANSFER_PREP
            })

            OuttakePosition.TRANSFER -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_TRANSFER
                lstrikeServo.position = Constants.OUT_STRIKE_TRANSFER
            })

            OuttakePosition.SAFE -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_SAFE
                lstrikeServo.position = Constants.OUT_STRIKE_SAFE
            })

            OuttakePosition.PRELOAD -> InstantCommand({
                // do
                rstrikeServo.position = Constants.OUT_STRIKE_SAFE
                lstrikeServo.position = Constants.OUT_STRIKE_SAFE
            })

            OuttakePosition.SPEC_INV -> InstantCommand({
                rstrikeServo.position = Constants.OUT_STRIKE_SPEC_INV
                lstrikeServo.position = Constants.OUT_STRIKE_SPEC_INV
            })

        }
    }

    /**
     * Returns the current state of the claw button.
     * @return The state of the claw button.
     */
    fun getClawButtonState() = clawButton.state

    /**
     * Moves the wrist servo to the home position.
     */
    fun wristHome(): InstantCommand {
        return InstantCommand({
            wristState = false
            wristServo.position = Constants.WRIST_SERVO_HOME
        })
    }

    /**
     * Moves the wrist servo to the scoring position.
     */
    fun wristScore(): InstantCommand {
        return InstantCommand({ wristServo.position = Constants.WRIST_SERVO_TARGET })
    }

    /**
     * Opens the claw to score.
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
     */
    fun moveArmToHome(): Command =
        setPivot(OuttakePosition.HOME)
            .andThen(WaitCommand(250))
            .andThen(setStrike(OuttakePosition.HOME))
            .andThen(wristHome())
            .andThen(InstantCommand({ homeState = true }))
    //.andThen(PerpetualCommand(ConditionalCommand(clawClose(), clawOpen()) { getClawButtonState() }))

    /**
     * Moves the entire arm to the scoring position.
     */
    fun moveArmToScore(): Command =
        setStrike(OuttakePosition.TARGET)
            .andThen(setPivot(OuttakePosition.TARGET))
            .andThen(wristHome())
            .andThen(InstantCommand({ specState = false }))

    /**
     * Moves the arm to the Specimen scoring position.
     * This is used for scoring specimens in the Specimen Auto.
     */
    fun moveArmToScoreSpec(): Command =
        setStrike(OuttakePosition.SPEC_TARGET)
            .andThen(setPivot(OuttakePosition.SPEC_TARGET))
            .andThen(wristScore())
            .andThen(InstantCommand({ homeState = false }))

    /**
     * Moves the arm to the Specimen scoring position with inverted strike servos.
     * This is used for scoring specimens during Tele-op with inverted strike servos.
     */
    fun moveArmToScoreSpecInv(): Command =
        setStrike(OuttakePosition.SPEC_INV)
            .andThen(WaitCommand(500))
            .andThen(setPivot(OuttakePosition.SPEC_INV))
            .andThen(wristHome())
            .andThen(InstantCommand({ homeState = false }))

    /**
     * Moves the arm to the transfer preparation position.
     * This is used for preparing the arm for transferring specimens.
     */
    fun moveArmToTransferPrep(): Command =
        wristHome()
            .andThen(setPivot(OuttakePosition.TRANSFER_PREP))
            .andThen(setStrike(OuttakePosition.TRANSFER_PREP))
            .andThen(InstantCommand({ specState = false }))

    /**
     * Moves the arm to the transfer position.
     * This is used for transferring specimens.
     */
    fun moveArmToTransfer(): Command =
        wristHome()
            .andThen(WaitCommand(200))
            .andThen(setPivot(OuttakePosition.TRANSFER))
            .andThen(WaitCommand(200))
            .andThen(setStrike(OuttakePosition.TRANSFER))
            .andThen(InstantCommand({ specState = false }))

    /**
     * Toggles the arm between the home position and the scoring position based on the saved arm state.
     */
    fun toggleArm() = ConditionalCommand(moveArmToScore(), moveArmToTransfer()) { homeState }

    /**
     * Toggles the arm between the home position and the Specimen scoring position based on the saved arm state.
     */
    fun toggleArmSpec() = ConditionalCommand(moveArmToScoreSpec(), moveArmToHome()) { homeState }

    /**
     * Toggles the arm between the home position and the Specimen scoring position with inverted strike servos.
     * This is used for scoring specimens during Tele-op with inverted strike servos.
     */
    fun toggleArmSpecInv() = ConditionalCommand(moveArmToScoreSpecInv(), moveArmToHome()) { homeState }

    /**
     * Toggles the claw between the open (ready to score) position and the closed position.
     */
    fun toggleClaw() = ConditionalCommand(clawOpen(), clawClose()) { clawState }
    override fun stateString(): String {
        return "OUTTAKE SYSTEM Claw: $clawState Home: $homeState Wrist: $wristState Spec: $specState"
    }
}