package org.firstinspires.ftc.team772.implementation

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.geometry.Pose2d
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.arcrobotics.ftclib.kinematics.HolonomicOdometry
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.math.*

class ParallelPlateDrivesystem(
    private val hw: HardwareMap,
    val FLMotor: MotorEx = MotorEx(hw, "FLMotor"),
    val FRMotor: MotorEx = MotorEx(hw, "FRMotor"),
    val BLMotor: MotorEx = MotorEx(hw, "BLMotor"),
    val BRMotor: MotorEx = MotorEx(hw, "BRMotor"),
    private val hubs: MutableList<LynxModule> = hw.getAll(LynxModule::class.java),
) : MecanumDrive(FRMotor, FLMotor, BRMotor, BLMotor){
    // TODO: Implement things from control theory: Motion Profiling, PID.
    val climbSystem: ClimbSystem = ClimbSystem(hw)
    val intakeSystem: IntakeSystem = IntakeSystem(hw)
    val outtakeSystem: OuttakeSystem = OuttakeSystem(hw)

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
    }

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

    val position: Pose2d
        /**
         * Returns the position of the robot.
         */
        get() = this.odometry.pose

    init {
        // Initialize bulk reads
        for (hub in hubs){
            hub.bulkCachingMode = LynxModule.BulkCachingMode.AUTO
        }
        encoderLeft.setDistancePerPulse(1 / TICKS_PER_INCHES)
        encoderRight.setDistancePerPulse(1 / TICKS_PER_INCHES)
        encoderRight.setDirection(Motor.Direction.REVERSE)
        encoderCenter.setDistancePerPulse(1 / TICKS_PER_INCHES)

        FRMotor.inverted = true

    }

    fun update() {
        for (hub in hubs) {
            hub.clearBulkCache()
        }
        odometry.updatePose()
    }


    fun drive(x: Double, y: Double, theta: Double) {
        // Calculate the denominator so that it scales from [-1, 1]
        val denominator: Double = max(abs(y) + abs(x) + abs(theta), 1.0)
        val squaredX = x.pow(2) * x.sign
        val squaredY = y.pow(2) * y.sign
        val squaredTheta = theta.pow(2) * theta.sign

        // Drive the robot
        FRMotor.setRunMode(Motor.RunMode.RawPower)
        BRMotor.setRunMode(Motor.RunMode.RawPower)
        FLMotor.setRunMode(Motor.RunMode.RawPower)
        BLMotor.setRunMode(Motor.RunMode.RawPower)

        FLMotor.set((squaredY - squaredX + squaredTheta ) / denominator)
        FRMotor.set((-squaredY + squaredX - squaredTheta ) / denominator)

        BLMotor.set((-squaredY + squaredX + squaredTheta) / denominator)
        BRMotor.set((-squaredY - squaredX - squaredTheta) / denominator)
    }

    fun halt() {
        drive(0.0, 0.0, 0.0)
    }

    override fun stop() {
        super.stop()
        halt()
    }


}