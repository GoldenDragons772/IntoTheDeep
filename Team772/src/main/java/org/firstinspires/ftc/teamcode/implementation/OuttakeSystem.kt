package org.firstinspires.ftc.teamcode.implementation

import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.Servo
import kotlinx.coroutines.delay

enum class OuttakeState {
    HOME,
    TARGET,
    SPEC_TARGET,
    SPEC_INV,
    TRANSFER_PREP,
    TRANSFER,
    SAFE,
    PRELOAD
}
/**
 * Controls the outtake system and its related servos.
 * It does not control the climb system, which must be changed separately in order to actually climb.
 *
 */
class OuttakeSystem(root: RootSystem, private val isAuto: Boolean) {

    // Defines servos
    private val rstrikeServo: Servo = root.hw.get(Servo::class.java, "vRightStrikeServo")
    private val lstrikeServo: Servo = root.hw.get(Servo::class.java, "vLeftStrikeServo")

    private val pivotServo: Servo = root.hw.get(Servo::class.java, "outPivotServo")
    private val wristServo: Servo = root.hw.get(Servo::class.java, "outWristServo")

    private val clawServo: Servo = root.hw.get(Servo::class.java, "outClawServo")

    //Define Sensors
    private val clawButton: DigitalChannel = root.hw.get(DigitalChannel::class.java, "outLimitSwitch")


    // State Machine
    private var isClawClosed = false
    private var homeState = false



    init {
        rstrikeServo.direction = Servo.Direction.REVERSE
        clawServo.direction = Servo.Direction.FORWARD
        pivotServo.direction = Servo.Direction.REVERSE

        setStrike(OuttakeState.HOME)
        setPivot(OuttakeState.HOME)
        setWrist(OuttakeState.HOME)
        setClaw(false)
    }

    fun getClawButtonState() = clawButton.state
    fun getSpecState(): Boolean = homeState

    fun setPivot(pos: OuttakeState) {
        pivotServo.position = when (pos) {
            OuttakeState.HOME -> Constants.PIVOT_SERVO_HOME
            OuttakeState.TRANSFER_PREP -> Constants.PIVOT_SERVO_TRANSFER
            OuttakeState.TRANSFER -> Constants.PIVOT_SERVO_TRANSFER
            OuttakeState.TARGET -> Constants.PIVOT_SERVO_SCORE
            OuttakeState.SPEC_TARGET -> Constants.PIVOT_SERVO_SPEC
            OuttakeState.SPEC_INV -> Constants.PIVOT_SERVO_SPEC_INV
            OuttakeState.SAFE -> Constants.PIVOT_SERVO_SAFE
            OuttakeState.PRELOAD -> Constants.PIVOT_SERVO_PRELOAD
        }

    }

    fun setStrike(state: OuttakeState) {
        val pos = when (state) {
            OuttakeState.HOME -> Constants.OUT_STRIKE_HOME
            OuttakeState.TARGET -> Constants.OUT_STRIKE_SCORE
            OuttakeState.SPEC_TARGET -> Constants.OUT_STRIKE_SPEC
            OuttakeState.TRANSFER_PREP -> Constants.OUT_STRIKE_TRANSFER_PREP
            OuttakeState.TRANSFER -> Constants.OUT_STRIKE_TRANSFER
            OuttakeState.SAFE -> Constants.OUT_STRIKE_SAFE
            OuttakeState.PRELOAD -> Constants.OUT_STRIKE_SAFE
            OuttakeState.SPEC_INV -> Constants.OUT_STRIKE_SPEC_INV
        }
        rstrikeServo.position = pos
        lstrikeServo.position = pos
    }

    fun setWrist(state: OuttakeState) {
        assert(state == OuttakeState.HOME || state == OuttakeState.TARGET) { "state must be one of TARGET or HOME" }
        wristServo.position = when (state) {
            OuttakeState.HOME -> Constants.WRIST_SERVO_HOME
            OuttakeState.TARGET -> Constants.WRIST_SERVO_TARGET
            else -> TODO()
        }
    }

    fun setClaw(shouldClose: Boolean) {
        if (!shouldClose) {
            assert(isClawClosed) { "Claw is already open!" }
            clawServo.position = Constants.CLAW_SERVO_TARGET // Open
        } else {
            assert(!isClawClosed) { "Claw is already closed!" }
            clawServo.position = Constants.CLAW_SERVO_HOME // Close
        }
        isClawClosed = shouldClose

    }

    /**
     * Moves the entire arm assembly to the home position.
     */
    suspend fun moveArmToHome() {
        setPivot(OuttakeState.HOME)
        delay(250L)
        setStrike(OuttakeState.HOME)
        setWrist(OuttakeState.HOME)
        homeState = true
    }

    /**
     * Moves the entire arm to the scoring position.
     */
    suspend fun moveArmToScore() {
        setStrike(OuttakeState.TARGET)
        setPivot(OuttakeState.TARGET)
        setWrist(OuttakeState.TARGET)
    }

    suspend fun moveArmToScoreSpec() {
        setStrike(OuttakeState.SPEC_TARGET)
        setPivot(OuttakeState.SPEC_TARGET)
        setWrist(OuttakeState.TARGET)
        homeState = false
    }

    suspend fun moveArmToScoreSpecInv() {
        setStrike(OuttakeState.SPEC_INV)
        delay(500L)
        setPivot(OuttakeState.SPEC_INV)
        setWrist(OuttakeState.HOME)
        homeState = false
    }

    suspend fun moveArmToTransferPrep() {
        setWrist(OuttakeState.HOME)
        setPivot(OuttakeState.TRANSFER)
        setStrike(OuttakeState.TRANSFER_PREP)
    }

    suspend fun moveArmToTransfer() {
        setWrist(OuttakeState.HOME)
        delay(200L)
        setPivot(OuttakeState.TRANSFER)
        delay(200L)
        setStrike(OuttakeState.TRANSFER)
    }

    /**
     * Toggles the arm between the home position and the scoring position based on the saved arm state.
     */
    suspend fun toggleArm() = if (homeState) moveArmToScore() else moveArmToTransfer()

    /**
     * Toggles the arm between the home position and the Specimen scoring position based on the saved arm state.
     */
    suspend fun toggleArmSpec() = if (homeState) moveArmToScoreSpec() else moveArmToHome()
    suspend fun toggleArmSpecInv() = if (homeState) moveArmToScoreSpecInv() else moveArmToHome()

    /**
     * Toggles the claw between the open (ready to score) position and the closed position.
     */
    suspend fun toggleClaw() = setClaw(!isClawClosed)
}