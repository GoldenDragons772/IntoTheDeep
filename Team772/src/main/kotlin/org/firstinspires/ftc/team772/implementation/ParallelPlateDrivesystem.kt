package org.firstinspires.ftc.team772.implementation
import ClimbSystem
import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.geometry.Pose2d
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.helpers.PIDController
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max

class ParallelPlateDrivesystem(
    override val hw: HardwareMap,
    val FLMotor: MotorEx = MotorEx(hw, "Motor1"),
    val FRMotor: MotorEx = MotorEx(hw, "Motor2"),
    val BLMotor: MotorEx = MotorEx(hw, "Motor3"),
    val BRMotor: MotorEx = MotorEx(hw, "Motor4"),
    override var hubs: MutableList<LynxModule> = hw.getAll(LynxModule::class.java),
    override var climbState: Boolean = false,
) : MecanumDrive(FRMotor, FLMotor, BRMotor, BLMotor), ControlSystem {
    // TODO: Implement things from control theory: Motion Profiling, PID.
    val climbSystem: ClimbSystem = ClimbSystem(hw)
    val intakeSystem: IntakeSystem = IntakeSystem(hw)

    companion object {
        // Constants. Change these to tune, et cetera.
        // Specific values for a specific chassis.
        const val MILLIMETERS_PER_INCH = 25.4
        const val TRACK_WIDTH = 155 / MILLIMETERS_PER_INCH
        const val CENTER_WHEEL_OFFSET = 113.5 / MILLIMETERS_PER_INCH
        const val TICKS_PER_REVOLUTION = 2000
        const val WHEEL_DIAMETER = 48 / MILLIMETERS_PER_INCH
        const val WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * PI
        const val TICKS_PER_INCHES = TICKS_PER_REVOLUTION / WHEEL_CIRCUMFERENCE

        // PID coefficients for the path follower.
        const val PFKP = 0.1
        const val PFKD = 0.1
        const val PFKI = 0.0
    }

    override val pathFollowerPID = PIDController(PFKP, PFKI, PFKD)

    // Initialize the motors
    private val encoderLeft = BLMotor.encoder
    private val encoderRight = FLMotor.encoder
    private val encoderCenter = FRMotor.encoder

    private val odometry = HolonomicOdometry(
        encoderLeft::getDistance,
        encoderRight::getDistance,
        encoderCenter::getDistance,
        TRACK_WIDTH,
        CENTER_WHEEL_OFFSET
    )

    override val position: Pose2d
        /**
         * Returns the position of the robot.
         */
        get() = this.odometry.pose

    init {
        super.initBulkReads()
        encoderLeft.setDistancePerPulse(1 / TICKS_PER_INCHES)
        encoderRight.setDistancePerPulse(1 / TICKS_PER_INCHES)
        encoderRight.setDirection(Motor.Direction.REVERSE)
        encoderCenter.setDistancePerPulse(1 / TICKS_PER_INCHES)

        FRMotor.inverted = true;
        BLMotor.inverted = true;
        BRMotor.inverted = true;


    }

    override fun update() {
        super.update()
        odometry.updatePose()
    }


    override fun drive(x: Double, y: Double, theta: Double) {
        // Calculate the denominator so that it scales from [-1, 1]
        val denominator: Double = max(abs(y) + abs(x) + abs(theta), 1.0)

        // Drive the robot
        FRMotor.setRunMode(Motor.RunMode.RawPower)
        BRMotor.setRunMode(Motor.RunMode.RawPower)
        FLMotor.setRunMode(Motor.RunMode.RawPower)
        BLMotor.setRunMode(Motor.RunMode.RawPower)


        FLMotor.set((y + x - theta) / denominator)
        BLMotor.set((y - x - theta) / denominator)

        FRMotor.set((y - x + theta) / denominator)
        BRMotor.set((y + x + theta) / denominator)
    }

    override fun halt() {
        drive(0.0, 0.0, 0.0)
    }

    override fun stop() {
        super.stop()
        halt()
    }

    override fun highclimb() {
        climbSystem!!.setArmToPos(ClimbSystem.ArmPos.HIGHCLIMB)
    }

    override fun lowclimb() {
        climbSystem!!.setArmToPos(ClimbSystem.ArmPos.LOWCLIMB)
    }

    override fun unclimb() {
        climbSystem!!.setArmToPos(ClimbSystem.ArmPos.HOME)
    }

}