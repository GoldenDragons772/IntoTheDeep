package org.firstinspires.ftc.teamcode.implementation

import android.util.Log
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.kotlin.extensions.util.clamp
import com.qualcomm.robotcore.hardware.Servo
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.vision.SampleDetection
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam
import kotlin.math.acos
import kotlin.math.cos

enum class IntakePosition {
    HOME,
    TARGET,
    TRANSFER,
    HOVER
}

enum class LinkagePosition {
    HOME,
    FULL,
    HALF
}

enum class WristPosition {
    HOME,
    TARGET,
    ANGLE,
    ANGLE_BUCKET
}

@Config
class IntakeSystem(private val root: RootSystem, private val isAuto: Boolean, private val isSpecAuto: Boolean) {
    companion object {
        // Set Positions for Linkage
        @JvmField var LEFT_LINKAGE_HOME: Double = 0.0
        @JvmField var LEFT_LINKAGE_TARGET: Double = 0.42
        @JvmField var LEFT_LINKAGE_HALF: Double = 0.23

        //    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_HALF = 0.23;
        @JvmField var STRIKE_PIVOT_HOME: Double = 0.35
        @JvmField var STRIKE_PIVOT_TARGET: Double = 0.91
        @JvmField var STRIKE_PIVOT_TRANSFER: Double = 0.84

        // Set Positions for Strike Servos

        // Set Positions for main pivot
        @JvmField var PIVOT_HOME: Double = 0.5
        @JvmField var PIVOT_TARGET: Double = 0.28
        @JvmField var PIVOT_TRANSFER: Double = 1.0

        // Set Positions for Wrist
        @JvmField var WRIST_HOME: Double = 0.35
        @JvmField var WRIST_TARGET: Double = 1.0
        @JvmField var WRIST_ANGLE: Double = 0.85
        @JvmField var WRIST_ANGLE_BUCKET: Double = 0.5
        @JvmField var WRIST_INC: Double = 0.4

        // Set Positions for claw
        @JvmField var CLAW_HOME: Double = 1.0
        @JvmField var CLAW_TARGET: Double = 0.765
        @JvmField var CLAW_STROKE: Double = 0.5

    }

    private var pivotState: IntakePosition = IntakePosition.HOME
    private var wristState: WristPosition = WristPosition.HOME
    private var intakeState: IntakePosition = IntakePosition.HOME
    private var linkageState: LinkagePosition = LinkagePosition.HOME
    private var clawState: Boolean = false

    inner class ValueCache {
        var wristPos: Double = 0.64
        var linkagePosition: Double = 0.0
    }


    private val leftLinkageServo: Servo = root.hw.get(Servo::class.java, "lLinkageServo")
    private val rightLinkageServo: Servo = root.hw.get(Servo::class.java, "rLinkageServo")
    private val leftStrikeServo: Servo = root.hw.get(Servo::class.java, "hLeftStrike")
    private val rightStrikeServo: Servo = root.hw.get(Servo::class.java, "hRightStrike")
    private val pivotServo: Servo = root.hw.get(Servo::class.java, "hPivot")
    private val wristServo: Servo = root.hw.get(Servo::class.java, "hSwivelServo")
    private val clawServo: Servo = root.hw.get(Servo::class.java, "intakeClawServo")

    var camera: OpenCvWebcam
    var sampleDetector: SampleDetection
    var valueCache: ValueCache = ValueCache()


    init {
        rightLinkageServo.direction = Servo.Direction.REVERSE
        rightStrikeServo.direction = Servo.Direction.REVERSE
        wristServo.direction = Servo.Direction.REVERSE

        rightLinkageServo.position = LEFT_LINKAGE_HOME
        leftLinkageServo.position = LEFT_LINKAGE_HOME

        pivotServo.position = PIVOT_HOME

        if (!isAuto || !isSpecAuto) {
            setLinkage(LEFT_LINKAGE_HOME)


            clawServo.position = CLAW_HOME

            leftStrikeServo.position = STRIKE_PIVOT_HOME
            rightStrikeServo.position = STRIKE_PIVOT_HOME

            pivotServo.position = PIVOT_HOME + 0.2
            wristServo.position = WRIST_HOME
        }

        val webcamName = root.hw.get(WebcamName::class.java, "GDVision")
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName)

        sampleDetector = SampleDetection(root.telemetry, root.isAllianceRed)
        Log.i("Camera", "Started with color" + root.isAllianceRed)

        Log.i("Camera", "Before camera initialization")
        camera.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                Log.i("Camera", "Started streaming")
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT, OpenCvWebcam.StreamFormat.MJPEG)
                camera.setPipeline(sampleDetector)
                FtcDashboard.getInstance().startCameraStream(camera, 100.0)
                //                camera.getGainControl().setGain(Constants.CAMERA_GAIN);
//                camera.pauseViewport(); // have it paused by default.
            }

            override fun onError(i: Int) {
            }
        })
    }

    fun periodic() {
        if (!isAuto) {
            root.telemetry.addData("extendState", intakeState.toString())
            root.telemetry.addData("pivotPosition", pivotState.toString())
            root.telemetry.addData("linkageState", linkageState.toString())
            if (pivotState == IntakePosition.HOME || pivotState == IntakePosition.HOVER && sampleDetector.sampleRotation.get() != -70.0 && !clawState) {
                visionWristRotation()
            }
        }
    }

    fun visionWristRotation() {
        val rotationValue = sampleDetector.sampleRotation.get()
        var inputValue = ((rotationValue) / Math.PI + 0.5) % 1
        if (inputValue < 0) inputValue += 1.0
        wristServo.position =
            inputValue * Constants.VISION_SERVO_MULTIPLIER
        this.valueCache.wristPos = inputValue
        root.telemetry.addData("Theta --", rotationValue)
        root.telemetry.addData("Rotation", inputValue)
    }

    /**
     * @param desiredLength the desired length of the slides in inches (between zero and the maximum length, in this case 15 inches).
     * @return a command
     */
    fun horizontalSlideExtensionConversion(desiredLength: Double): Double {
        val adjustedLength = desiredLength + 3.92904
        Log.i("Vision", "Adjusted $adjustedLength")
        val angle = acos(adjustedLength / (2 * Constants.LINKAGE_LENGTH))
        assert(Constants.LINKAGE_TARGET_ANGLE < angle && angle < Constants.LINKAGE_HOME_ANGLE) {
            String.format(
                "%f, %f",
                desiredLength,
                angle
            )
        }  //
        val servoOutput = angleToLinkageServo(angle)
        assert(0.0 < servoOutput && servoOutput < LEFT_LINKAGE_TARGET) {
            String.format(
                "%f, %f, %f",
                servoOutput,
                desiredLength,
                angle
            )
        }
        return servoOutput
    }

    val horizontalSlideExtension: Double
        get() {
            val lowerBound = Constants.LINKAGE_HOME_ANGLE
            val angleIntervalWidth =
                (Constants.LINKAGE_TARGET_ANGLE - lowerBound)
            val currentServoRatio =
                valueCache.linkagePosition / LEFT_LINKAGE_TARGET
            return (2 * Constants.LINKAGE_LENGTH * cos(
                currentServoRatio * angleIntervalWidth + lowerBound
            )) - 3.92904
            // 2l*cos((1-current/max) * (max_angle - min_angle) + min_angle)
        }

    fun angleToLinkageServo(angle: Double): Double {
        // 0.46 * (78 - 12) + 12
        // s * (m_1 - m_0) + m_0 = a
        // (a - m_0) / (m_1 - m_0)
        return LEFT_LINKAGE_TARGET * ((angle - Constants.LINKAGE_HOME_ANGLE) / (Constants.LINKAGE_TARGET_ANGLE - Constants.LINKAGE_HOME_ANGLE))
    }

    fun setLinkage(state: LinkagePosition) {
        when (state) {
            LinkagePosition.HOME -> {
                setLinkage(LEFT_LINKAGE_HOME)
                linkageState = LinkagePosition.HOME
            }

            LinkagePosition.FULL -> {
                setLinkage(LEFT_LINKAGE_TARGET)
                linkageState = LinkagePosition.FULL
            }

            LinkagePosition.HALF -> {
                setLinkage(LEFT_LINKAGE_HALF)
                linkageState = LinkagePosition.HALF
            }
        }
    }

    fun setLinkage(pos: Double) {
        assert(pos in 0.0..LEFT_LINKAGE_TARGET) { pos }
        Log.i("Linkage", pos.toString())
        valueCache.linkagePosition = pos
        leftLinkageServo.position = pos
        rightLinkageServo.position = pos

    }

    fun setStrike(pos: IntakePosition): () -> Unit {
        return when (pos) {
            IntakePosition.HOME -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_HOME
                rightStrikeServo.position = STRIKE_PIVOT_HOME
            }

            IntakePosition.TARGET -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_TARGET
                rightStrikeServo.position = STRIKE_PIVOT_TARGET
            }

            IntakePosition.TRANSFER -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_TRANSFER
                rightStrikeServo.position = STRIKE_PIVOT_TRANSFER
            }

            IntakePosition.HOVER -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_TRANSFER
                rightStrikeServo.position = STRIKE_PIVOT_TRANSFER
            }
        }
    }

    fun setPivot(pos: IntakePosition): () -> Unit {
        return when (pos) {
            IntakePosition.HOME -> { ->
                pivotServo.position = PIVOT_HOME
                pivotState = pos
            }

            IntakePosition.TARGET -> { ->
                pivotServo.position = PIVOT_TARGET
                pivotState = pos
            }

            IntakePosition.TRANSFER -> { ->
                pivotServo.position = PIVOT_TRANSFER
                pivotState = pos
            }

            IntakePosition.HOVER -> { ->
                pivotServo.position = PIVOT_HOME
                pivotState = pos
            }
        }
    }

    fun setWrist(pos: WristPosition) {
        when (pos) {
            WristPosition.HOME -> {
                wristServo.position = WRIST_HOME
                wristState = WristPosition.HOME
            }

            WristPosition.TARGET -> {
                wristState = WristPosition.TARGET
                wristServo.position = WRIST_TARGET
            }

            WristPosition.ANGLE -> {
                wristState = WristPosition.ANGLE
                wristServo.position = WRIST_ANGLE
            }

            WristPosition.ANGLE_BUCKET -> {
                wristState = WristPosition.ANGLE_BUCKET
                wristServo.position = WRIST_ANGLE_BUCKET
            }
        }
    }

    fun incWrist(pos: Double) {
        val newPos = ((valueCache.wristPos + pos) % 1.0).clamp(0.0, 1.0)
        setWrist(newPos)
        wristState = when (valueCache.wristPos) {
            WRIST_TARGET -> WristPosition.TARGET
            WRIST_ANGLE -> WristPosition.ANGLE
            else -> WristPosition.HOME
        }
        Log.i("Intake", valueCache.wristPos.toString())
    }

    fun setWrist(pos: Double) {
        wristServo.position = pos
        valueCache.wristPos = pos
    }

    fun setClaw(pos: IntakePosition) {
        when (pos) {
            IntakePosition.HOME -> {
                clawState = false
                clawServo.position = CLAW_HOME
            }

            IntakePosition.TARGET -> {
                clawState = true
                clawServo.position = CLAW_TARGET
            }

            IntakePosition.TRANSFER -> {
                clawState = false
                clawServo.position = CLAW_STROKE
            }

            IntakePosition.HOVER -> {
                clawState = false
                clawServo.position = CLAW_HOME
            }
        }
    }

    suspend fun moveToHome() {
        intakeState = IntakePosition.HOME
        setWrist(WristPosition.HOME)
        setLinkage(LinkagePosition.HOME)
        setStrike(IntakePosition.HOME)
        setPivot(IntakePosition.HOME)
    }

    suspend fun moveToTransfer() {
        intakeState = IntakePosition.TRANSFER
        //                    camera.stopStreaming();
        sampleDetector.isEnabled.set(false)
        setWrist(WristPosition.HOME)
        setLinkage(LinkagePosition.HOME)
        setStrike(IntakePosition.TRANSFER)
        setPivot(IntakePosition.TRANSFER)
    }

    suspend fun moveToTarget() {
        when (this.linkageState) {
            LinkagePosition.HOME -> {
                setLinkage(LinkagePosition.FULL)
                hoverIntake()
            }

            LinkagePosition.FULL -> {
                setLinkage(LinkagePosition.HOME)
                moveToTransfer()
            }

            LinkagePosition.HALF -> {
                setLinkage(LinkagePosition.HOME)
                moveToTransfer()
            }
        }
//                    camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT, OpenCvWebcam.StreamFormat.MJPEG);
//                    camera.setPipeline(sampleDetector);
        sampleDetector.isEnabled.set(true)
        //                setClaw(IntakePosition.HOME),
        //                setStrike(IntakePosition.TARGET),
        //                setPivot(IntakePosition.TARGET)
    }

    suspend fun toggleHover() {
        when (this.pivotState) {
            IntakePosition.HOVER -> strikeIntake()
            IntakePosition.TARGET -> hoverIntake()
            IntakePosition.HOME -> TODO()
            IntakePosition.TRANSFER -> TODO()
        }
    }

    suspend fun hoverIntake() {
        setStrike(IntakePosition.TRANSFER)
        delay(150)
        setPivot(IntakePosition.HOME)
        pivotState = IntakePosition.HOVER
    }

    suspend fun strikeIntake() {
        setPivot(IntakePosition.TARGET)
        delay(150)
        setStrike(IntakePosition.TARGET)
        pivotState = IntakePosition.TARGET
    }

    fun toggleClaw() {
        if (clawState) setClaw(IntakePosition.HOME) else setClaw(IntakePosition.TARGET)
        clawState = !clawState
    }

    fun toggleWrist() {
        when (this.wristState) {
            WristPosition.HOME -> setWrist(WristPosition.TARGET)
            WristPosition.ANGLE -> setWrist(WristPosition.HOME) //Each state must trigger the next one
            WristPosition.TARGET -> setWrist(WristPosition.ANGLE)
            WristPosition.ANGLE_BUCKET -> TODO()
        }
    }

    suspend fun transferSample() {
        root.climb.climbState = ClimbState.HOME
        root.outtake.setClaw(false)
        this.moveToTransfer()
        delay(200L)
        root.outtake.moveArmToTransfer()
        withTimeout(800L) {
            while (root.outtake.getClawButtonState()) {
                delay(10) // busywaiting, but whatever
            }
        }
        root.outtake.setClaw(true)
        delay(500L)
        this.setClaw(IntakePosition.HOME)
        root.outtake.moveArmToScore()
    }

    suspend fun toggleIntake() {
        root.outtake.setClaw(false)
        root.outtake.moveArmToTransferPrep()
        when (this.intakeState) {
            IntakePosition.HOME -> this.moveToTarget()
            IntakePosition.TRANSFER -> this.moveToTarget()
            IntakePosition.TARGET -> this.moveToTransfer()
            IntakePosition.HOVER -> TODO()
        }
    }

    suspend fun specimenCommandInverted() {
        this.moveToHome() // retract intake system
        if (this.linkageState == LinkagePosition.FULL) delay(500)
        root.climb.climbState = if (root.outtake.getSpecState()) {ClimbState.HIGH_CHAMBER_INVERTED} else ClimbState.HOME
        delay(100L) // Move arm to spec grab position
        root.outtake.toggleArmSpecInv()
    }

}
