package org.firstinspires.ftc.team772.implementation

import android.util.Log
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.command.* //Stop dumping all the tools out of the toolbox
import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.* //Stop dumping all the tools out of the toolbox
import kotlin.math.abs

class IntakeSystem(hw: HardwareMap) : SubsystemBase() {
    val slideMotor: DcMotorEx = hw.get(DcMotorEx::class.java, "slideMotor") // Port 3

    private val clawServo: Servo = hw.get(Servo::class.java, "clawServo") // Port 1
    private val clawPivotServo: Servo = hw.get(Servo::class.java, "joint2")
    private val swivelServo: Servo = hw.get(Servo::class.java, "swivelServo")
    private val intakePivot: Servo = hw.get(Servo::class.java, "joint1")
    var stopSwitch: TouchSensor = hw.get(TouchSensor::class.java, "hardStop")

    // States
    // Right now these are all binary, but in the future some of these might need to be in an enum.
    var pivotState = false
    var aimState = false
    var clawState = false
    var wristState = false

    companion object {

        @JvmField
        var point = 0.0

        @JvmField
        var extendPos: ExtendPos = ExtendPos.HOME
    }

    //Sets the positions of the arm to a enum.
    enum class ExtendPos(val position: Int) {
        HOME(Constants.SLIDE_HOME),
        TARGET(Constants.SLIDE_TARGET),
        RECALIBRATE(Constants.SLIDE_RECALIBRATE)
    }

    init {

        slideMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        slideMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        //slideMotor.setRunMode(Motor.RunMode.PositionControl)
        //slideMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)

        // Set Home Positions for Servo
        clawPivotServo.direction = Servo.Direction.REVERSE
        clawPivotServo.position = Constants.PIVOT_SERVO_HOME


        // Claw Defaults
        clawServo.position = Constants.CLAW_SERVO_HOME

        // Strike positions Defaults
        intakePivot.position = Constants.STRIKE_SERVO_HOME

        //Lock Wrist
        swivelServo.position = Constants.INTAKE_WRIST_HOME
        swivelServo.scaleRange(0.07, 0.8)

//        slideMotor.direction = DcMotorSimple.Direction.REVERSE
        //We might have to reverse motors


    }

    /**
     * Sets the slide to a certain position ([ExtendPos])
     */
    fun setSlideToPos(pos: ExtendPos) {
        extendPos = pos
        setSlideToPos(pos.position)
    }

    /**
     * Function that sets the slide to a position.
     * <br>
     * It also makes sure to block the motor from moving if it goes home to prevent the string from snapping
     * or overheating the motor.
     * @param pos The desired position
     */
    private fun setSlideToPos(pos: Int): SlideCommand {
        val command = SlideCommand(this, pos, switch = stopSwitch)
        return command
    }

    /**
     * Gets the position of the [slideMotor]
     */
    private fun getSlidePosition(): Int {
        return slideMotor.currentPosition
    }

    /**
     * Sets the slide power
     * @param power The power being supplied to the robo
     */
    fun setSlidePower(power: Double) {
        slideMotor.power = power
    }

    /**
     * Sets the intake to the [extendPos]
     */
    fun extendCommand(): SlideCommand {
        return setSlideToPos(ExtendPos.TARGET.position)
    }

    /**
     * Sets the intake to the home position.
     */
    fun retractCommand(): SlideCommand {
        return setSlideToPos(ExtendPos.HOME.position)
    }

    /**
     * Sets the intake to the recalibration position (It hits the button)
     */
    fun recalCommand(): SlideCommand {
        return setSlideToPos(ExtendPos.RECALIBRATE.position)
    }

    /**
     * Unpivot the head.
     */
    fun joint2UnPivot(): Command {
        return InstantCommand({
            clawPivotServo.position = Constants.PIVOT_SERVO_TARGET // BUG: Maybe seems backwards?
        })
    }

    /**
     * Pivot the active intake downwards for intake.        swivelServo.direction = Servo.Direction.REVERSE
     */
    fun joint2Pivot(): Command {
        return InstantCommand({
            clawPivotServo.position = Constants.PIVOT_SERVO_HOME
        })
    }

    fun joint2PivotTransfer(): Command {
        return InstantCommand({
            clawPivotServo.position = Constants.PIVOT_SERVO_TRANSFER
        })
    }

    /**
     * Make the head begin to swallow elements.
     */
    fun closeClaw(): InstantCommand {
        return InstantCommand({
            clawState = !clawState
            Log.i("ROBO", "Swallowing")
            clawServo.position = Constants.CLAW_SERVO_TARGET
        })
    }

    /**
     * Make the head spit the elements back out.
     */
    fun openClaw(): InstantCommand {
        return InstantCommand({
            clawState = !clawState
            clawServo.position = Constants.CLAW_SERVO_HOME
            Log.i("ROBO", "Spitting")
        })
    }

    fun joint1PivotIntake(): InstantCommand {
        return InstantCommand({
            intakePivot.position = Constants.STRIKE_SERVO_TARGET
        })
    }

    fun joint1UnPivotIntake(): InstantCommand {
        return InstantCommand({
            intakePivot.position = Constants.STRIKE_SERVO_HOME
        })
    }

    fun joint1SpecPose(): InstantCommand {
        return InstantCommand({
            intakePivot.position = Constants.STRIKE_SERVO_TRANSFER_SPEC
        })
    }

    fun joint1SmackPose(): InstantCommand {
        return InstantCommand({
            intakePivot.position = Constants.STRIKE_SERVO_SMACK
        })
    }

    fun joint1Transfer(): InstantCommand {
        return InstantCommand({
            intakePivot.position = Constants.STRIKE_SERVO_TRANSFER
        })
    }

    fun wristToTransferPos(): InstantCommand {
        return InstantCommand({
            swivelServo.position = Constants.INTAKE_WRIST_HOME
            wristState = false
        })
    }

    fun wristToPerpendicPos(): InstantCommand {
        return InstantCommand({
            swivelServo.position = Constants.INTAKE_WRIST_PERP
            wristState = true
        })
    }

    fun wristToSpecPos(): InstantCommand {
        return InstantCommand({
            swivelServo.position = Constants.INTAKE_WRIST_SPEC
            wristState = true
        })
    }

    fun intakeWristAutoPrep(): InstantCommand {
        return InstantCommand({
            swivelServo.position = Constants.INTAKE_WRIST_AUTO_PREP
            wristState = true
        })
    }

    //As driver request: Aim and goHome set as toggle.
    //Sucking should be bound to a separate button.

    fun aim(): Command =
        extendCommand()
            .andThen(WaitCommand(500))
            .andThen(intakeWristAutoPrep())
            .andThen(WaitCommand(250))
            .andThen(wristToTransferPos())
            .andThen(joint2Pivot())
            .andThen(joint1PivotIntake())
            .andThen(openClaw())
            .andThen(InstantCommand({ aimState = true }))

    fun autoAim(): Command =
        extendCommand()
            .andThen(intakeWristAutoPrep())
            .andThen(WaitCommand(250))
            .andThen(wristToTransferPos())
            .andThen(joint2Pivot())
            .andThen(joint1PivotIntake())
            .andThen(openClaw())
            .andThen(InstantCommand({ aimState = true }))

    fun aimToSmack(): Command =
        extendCommand()
            .andThen(WaitCommand(500))
            .andThen(wristToPerpendicPos())
            .andThen(joint2Pivot())
            .andThen(joint1SmackPose())
            .andThen(openClaw())
            .andThen(InstantCommand({ aimState = true }))

    /**
     * Grab the sample and bring it into the robot.
     */
    fun goHome(): Command =
        retractCommand()
            .andThen(joint1Transfer())
            .andThen(joint2PivotTransfer())
            .andThen(InstantCommand({ aimState = false }))

    fun goHomeForSpec(): Command =
        retractCommand()
            .andThen(wristToSpecPos())
            .andThen(joint1Transfer())
            .andThen(joint2PivotTransfer())
            .andThen(InstantCommand({ aimState = false }))

    //Move the claw to the home state.
    fun pivotClawToHome(): Command =
        joint1UnPivotIntake()
            .andThen(InstantCommand({ pivotState = false }))

    //Move the claw to the grabbing state.
    fun pivotClawToPick(): Command =
        joint1PivotIntake()
            .andThen(InstantCommand({ pivotState = true }))

    fun aimToggle() = ConditionalCommand(goHomeForSpec(), aim()) { aimState }

    fun swivelToggle() = ConditionalCommand(wristToTransferPos(), wristToPerpendicPos()) { wristState }

    // toggle the Intake Pivot
    fun toggleIntakePivot() =
        ConditionalCommand(pivotClawToHome(), pivotClawToPick()) { pivotState }

    //    fun toggleIntake
    fun toggleIntakeClaw() =
        ConditionalCommand(
            openClaw(),
            closeClaw()
        ) { clawState }

}

class SlideCommand(private val intake: IntakeSystem, private val position: Int, switch: TouchSensor) : CommandBase() {
    private var isEnded = false;
    private val stopSwitch = switch

    init {
        isEnded = false// Try to reset isEnded every time
        addRequirements(intake)
    }

    override fun initialize() {
        isEnded = false
        //Set the Target Position
        intake.slideMotor.setTargetPosition(position)

        //Set the motor mode
        intake.slideMotor.mode = DcMotor.RunMode.RUN_TO_POSITION

        //Set the stop behavior for the motor
        intake.slideMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        when (position) {
            Constants.SLIDE_TARGET -> intake.slideMotor.power = 1.0
            else -> intake.slideMotor.power = (Constants.SLIDE_MOTOR_SPEED)
        }

    }

    override fun execute() {

        val packet = TelemetryPacket()

        if (stopSwitch.isPressed && position == Constants.SLIDE_HOME) {
            intake.slideMotor.mode = DcMotor.RunMode.RESET_ENCODERS
            intake.slideMotor.power = 0.0
            Log.i("ROBO", "Slide Limit Hit!")
            this.isEnded = true
            return
        }

        packet.addLine("Current Position: ${intake.slideMotor.currentPosition}")
        packet.addLine("Target Position: ${intake.slideMotor.targetPosition}")
        FtcDashboard.getInstance().sendTelemetryPacket(packet)

        if (this.isEnded) return
        Log.i("ROBO", "looping")
    }

    override fun end(interrupted: Boolean) {
        super.end(interrupted)
        Log.i("ROBO", "ended")
        this.isEnded = true
    }

    override fun isFinished(): Boolean {
        // TODO: don't stop when extended
        Log.i("ROBO", "finished looping")
        this.isEnded = true

        var diff = 0
        var returnVal = 0

        when (position) {
            Constants.SLIDE_HOME -> {
                diff = abs(IntakeSystem.extendPos.position - IntakeSystem.ExtendPos.HOME.position)
                returnVal = 50
                if (intake.stopSwitch.isPressed && position == Constants.SLIDE_HOME) {
                    intake.slideMotor.power = 0.0
                    intake.slideMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                    Log.i("ROBO", "Slide Limit Hit!")
                }
            }

            Constants.SLIDE_TARGET -> {
                diff = abs(IntakeSystem.extendPos.position - IntakeSystem.ExtendPos.TARGET.position)
                returnVal = 1850
            }
        }

        Log.i("ROBO", diff.toString())

        return diff < returnVal

        //return intake.slideMotor.currentPosition == intake.slideMotor.targetPosition //THIS NEEDS TO RETURN TRUE FOR THE CODE TO FINISH!!! IF THE CODE TRIES TO RETURN ANYTHING ELSE, IT WILL LOOP OVER ITSELF UNTILL TRUE IS RETURNED!
    }

}

