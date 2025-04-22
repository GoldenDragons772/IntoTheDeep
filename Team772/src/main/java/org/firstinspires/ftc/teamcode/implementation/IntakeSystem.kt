package org.firstinspires.ftc.teamcode.implementation

import android.annotation.SuppressLint
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

enum class IntakeState {
    HOME,
    TARGET,
    TRANSFER,
    HOVER
}

enum class LinkageState {
    HOME,
    FULL,
    HALF
}

enum class WristState {
    HOME,
    TARGET,
    ANGLE,
    ANGLE_BUCKET
}

enum class ClawState {
    OPEN,
    CLOSED
}

@Config
class IntakeSystem(private val root: RootSystem, private val isAuto: Boolean, isSpecAuto: Boolean) {
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

    private var pivotState: IntakeState = IntakeState.HOME
    private var wristState: WristState = WristState.HOME
    private var intakeState: IntakeState = IntakeState.HOME
    private var clawState: ClawState = ClawState.OPEN

    val linkage = Linkage()
    val wrist = Wrist()

    private val leftStrikeServo: Servo = root.hw.get(Servo::class.java, "hLeftStrike")
    private val rightStrikeServo: Servo = root.hw.get(Servo::class.java, "hRightStrike")
    private val pivotServo: Servo = root.hw.get(Servo::class.java, "hPivot")
    private val clawServo: Servo = root.hw.get(Servo::class.java, "intakeClawServo")

    var camera: OpenCvWebcam
    var sampleDetector: SampleDetection


    init {

        linkage.set(LinkageState.HOME)

        setPivot(IntakeState.HOME)

        if (!isAuto || !isSpecAuto) {

            clawServo.position = CLAW_HOME

            setStrike(IntakeState.HOME)

            pivotServo.position = PIVOT_HOME + 0.2
            wrist.set(WristState.HOME)
        }

        val webcamName = root.hw.get(WebcamName::class.java, "GDVision")
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName)

        sampleDetector = SampleDetection(root.telemetry, root.isAllianceRed)
        Log.i("Camera", "Started with color" + root.isAllianceRed)

        Log.i("Camera", "Before camera initialization")
        camera.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                Log.i("Camera", "Started streaming")
                camera.startStreaming(
                    SampleDetection.SUBWIDTH,
                    SampleDetection.SUBHEIGHT,
                    OpenCvCameraRotation.UPRIGHT,
                    OpenCvWebcam.StreamFormat.MJPEG
                )
                camera.setPipeline(sampleDetector)
                FtcDashboard.getInstance().startCameraStream(camera, 100.0)
            }

            override fun onError(i: Int) {}
        })
    }

    fun periodic() {
        if (!isAuto) {
            root.telemetry.addData("extendState", intakeState.toString())
            root.telemetry.addData("pivotPosition", pivotState.toString())
            root.telemetry.addData("linkageState", linkage.state.toString())
            if (pivotState == IntakeState.HOME || pivotState == IntakeState.HOVER && sampleDetector.sampleRotation.get() != -70.0 && clawState == ClawState.OPEN) {
                wrist.visionWristRotation()
            }
        }
    }


    fun setStrike(pos: IntakeState): () -> Unit {
        return when (pos) {
            IntakeState.HOME -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_HOME
                rightStrikeServo.position = STRIKE_PIVOT_HOME
            }

            IntakeState.TARGET -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_TARGET
                rightStrikeServo.position = STRIKE_PIVOT_TARGET
            }

            IntakeState.TRANSFER -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_TRANSFER
                rightStrikeServo.position = STRIKE_PIVOT_TRANSFER
            }

            IntakeState.HOVER -> { ->
                leftStrikeServo.position = STRIKE_PIVOT_TRANSFER
                rightStrikeServo.position = STRIKE_PIVOT_TRANSFER
            }
        }
    }
    fun setPivot(pos: IntakeState): () -> Unit {
        return when (pos) {
            IntakeState.HOME -> { ->
                pivotServo.position = PIVOT_HOME
                pivotState = pos
            }

            IntakeState.TARGET -> { ->
                pivotServo.position = PIVOT_TARGET
                pivotState = pos
            }

            IntakeState.TRANSFER -> { ->
                pivotServo.position = PIVOT_TRANSFER
                pivotState = pos
            }

            IntakeState.HOVER -> { ->
                pivotServo.position = PIVOT_HOME
                pivotState = pos
            }
        }
    }
    fun setClaw(state: ClawState) {
        clawState = state
        clawServo.position = when (state){
            ClawState.CLOSED -> CLAW_TARGET
            ClawState.OPEN -> CLAW_HOME
        }
    }

    suspend fun moveToHome() {
        intakeState = IntakeState.HOME
        wrist.set(WristState.HOME)
        linkage.set(LinkageState.HOME)
        setStrike(IntakeState.HOME)
        setPivot(IntakeState.HOME)
    }

    suspend fun moveToTransfer() {
        intakeState = IntakeState.TRANSFER
        //                    camera.stopStreaming();
        sampleDetector.isEnabled.set(false)
        wrist.set(WristState.HOME)
        linkage.set(LinkageState.HOME)
        setStrike(IntakeState.TRANSFER)
        setPivot(IntakeState.TRANSFER)
    }

    suspend fun moveToTarget() {
        when (this.linkage.state) {
            LinkageState.HOME -> {
                linkage.set(LinkageState.FULL)
                hoverIntake()
            }

            LinkageState.FULL -> {
                linkage.set(LinkageState.HOME)
                moveToTransfer()
            }

            LinkageState.HALF -> {
                linkage.set(LinkageState.HOME)
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
            IntakeState.HOVER -> strikeIntake()
            IntakeState.TARGET -> hoverIntake()
            IntakeState.HOME -> TODO()
            IntakeState.TRANSFER -> TODO()
        }
    }

    suspend fun hoverIntake() {
        setStrike(IntakeState.TRANSFER)
        delay(150)
        setPivot(IntakeState.HOME)
        pivotState = IntakeState.HOVER
    }

    suspend fun strikeIntake() {
        setPivot(IntakeState.TARGET)
        delay(150)
        setStrike(IntakeState.TARGET)
        pivotState = IntakeState.TARGET
    }

    fun toggleClaw() {
        if (clawState == ClawState.CLOSED) setClaw(ClawState.OPEN) else setClaw(ClawState.CLOSED)
    }

    fun toggleWrist() {
        when (this.wristState) {
            WristState.HOME -> wrist.set(WristState.TARGET)
            WristState.ANGLE -> wrist.set(WristState.HOME) //Each state must trigger the next one
            WristState.TARGET -> wrist.set(WristState.ANGLE)
            WristState.ANGLE_BUCKET -> TODO()
        }
    }

    suspend fun transferSample() {
        root.climb.climbState = ClimbState.HOME
        root.outtake.setClaw(ClawState.OPEN)
        this.moveToTransfer()
        delay(200L)
        root.outtake.moveArmToTransfer()
        withTimeout(800L) {
            while (root.outtake.getClawButtonState()) {
                delay(10) // busywaiting, but whatever
            }
        }
        root.outtake.setClaw(ClawState.CLOSED)
        delay(500L)
        this.setClaw(ClawState.OPEN)
        root.outtake.moveArmToScore()
    }

    suspend fun toggleIntake() {
        root.outtake.setClaw(ClawState.OPEN)
        root.outtake.moveArmToTransferPrep()
        when (this.intakeState) {
            IntakeState.HOME -> this.moveToTarget()
            IntakeState.TRANSFER -> this.moveToTarget()
            IntakeState.TARGET -> this.moveToTransfer()
            IntakeState.HOVER -> TODO()
        }
    }

    suspend fun specimenCommandInverted() {
        this.moveToHome() // retract intake system
        if (this.linkage.state == LinkageState.FULL) delay(500)
        root.climb.climbState = if (root.outtake.getSpecState()) {
            ClimbState.HIGH_CHAMBER_INVERTED
        } else ClimbState.HOME
        delay(100L) // Move arm to spec grab position
        root.outtake.toggleArmSpecInv()
    }

    inner class Wrist {
        private val wristServo: Servo = root.hw.get(Servo::class.java, "hSwivelServo")
        internal var cachedPos: Double = 0.64
        init {
            wristServo.direction = Servo.Direction.REVERSE
        }

        fun visionWristRotation() {
            val rotationValue = sampleDetector.sampleRotation.get()
            var inputValue = ((rotationValue) / Math.PI + 0.5) % 1
            if (inputValue < 0) inputValue += 1.0
            wristServo.position =
                inputValue * Constants.VISION_SERVO_MULTIPLIER
            cachedPos = inputValue
            root.telemetry.addData("Theta --", rotationValue)
            root.telemetry.addData("Rotation", inputValue)
        }
        fun set(pos: WristState) {
            when (pos) {
                WristState.HOME -> {
                    wristServo.position = WRIST_HOME
                    wristState = WristState.HOME
                }

                WristState.TARGET -> {
                    wristState = WristState.TARGET
                    wristServo.position = WRIST_TARGET
                }

                WristState.ANGLE -> {
                    wristState = WristState.ANGLE
                    wristServo.position = WRIST_ANGLE
                }

                WristState.ANGLE_BUCKET -> {
                    wristState = WristState.ANGLE_BUCKET
                    wristServo.position = WRIST_ANGLE_BUCKET
                }
            }
        }

        fun incWrist(pos: Double) {
            val newPos = ((cachedPos + pos) % 1.0).clamp(0.0, 1.0)
            set(newPos)
            wristState = when (cachedPos) {
                WRIST_TARGET -> WristState.TARGET
                WRIST_ANGLE -> WristState.ANGLE
                else -> WristState.HOME
            }
            Log.i("Intake", cachedPos.toString())
        }

        fun set(pos: Double) {
            wristServo.position = pos
            cachedPos = pos
        }

    }
    inner class Linkage {
        private var cachedPos = 0.0
        internal var state: LinkageState = LinkageState.HOME
        private val leftLinkageServo: Servo = root.hw.get(Servo::class.java, "lLinkageServo")
        private val rightLinkageServo: Servo = root.hw.get(Servo::class.java, "rLinkageServo")

        init {
            rightLinkageServo.direction = Servo.Direction.REVERSE
            rightStrikeServo.direction = Servo.Direction.REVERSE
        }


        /**
         * @param desiredLength the desired length of the slides in inches (between zero and the maximum length, in this case 15 inches).
         * @return a command
         */
        @SuppressLint("DefaultLocale")
        fun horizontalSlideExtensionConversion(desiredLength: Double): Double {
            val adjustedLength = desiredLength + Constants.LINKAGE_OFFSET
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
                    cachedPos / LEFT_LINKAGE_TARGET
                return (2 * Constants.LINKAGE_LENGTH * cos(
                    currentServoRatio * angleIntervalWidth + lowerBound
                )) - Constants.LINKAGE_OFFSET
                // 2l*cos((1-current/max) * (max_angle - min_angle) + min_angle)
            }

        private fun angleToLinkageServo(angle: Double): Double {
            // 0.46 * (78 - 12) + 12
            // s * (m_1 - m_0) + m_0 = a
            // (a - m_0) / (m_1 - m_0)
            return LEFT_LINKAGE_TARGET * ((angle - Constants.LINKAGE_HOME_ANGLE) / (Constants.LINKAGE_TARGET_ANGLE - Constants.LINKAGE_HOME_ANGLE))
        }

        fun set(newState: LinkageState) {
            when (newState) {
                LinkageState.HOME -> {
                    set(LEFT_LINKAGE_HOME)
                    this.state = LinkageState.HOME
                }

                LinkageState.FULL -> {
                    set(LEFT_LINKAGE_TARGET)
                    this.state = LinkageState.FULL
                }

                LinkageState.HALF -> {
                    set(LEFT_LINKAGE_HALF)
                    this.state = LinkageState.HALF
                }
            }
        }

        fun set(pos: Double) {
            assert(pos in 0.0..LEFT_LINKAGE_TARGET) { pos }
            Log.i("Linkage", pos.toString())
            cachedPos = pos
            leftLinkageServo.position = pos
            rightLinkageServo.position = pos

        }

    }
}
