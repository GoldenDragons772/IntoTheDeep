package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.drivebase.MecanumDrive
import com.arcrobotics.ftclib.geometry.Pose2d
import com.arcrobotics.ftclib.hardware.motors.Motor
import com.arcrobotics.ftclib.hardware.motors.MotorEx
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.abstractions.ExampleExtension
import org.firstinspires.ftc.team772.helpers.PIDController
import kotlin.math.abs
import kotlin.math.max

class ColonThreeDrivesystem(
    override val hw: HardwareMap,
    val FLMotor: MotorEx = MotorEx(hw, "Motor1"),
    val BLMotor: MotorEx = MotorEx(hw, "Motor2"),
    val FRMotor: MotorEx = MotorEx(hw, "Motor3"),
    val BRMotor: MotorEx = MotorEx(hw, "Motor4"),
    override var hubs: MutableList<LynxModule> = hw.getAll(LynxModule::class.java),
) : MecanumDrive(FRMotor, FLMotor, BRMotor, BLMotor), ControlSystem {

    override val pathFollowerPID: PIDController
        get() = TODO("Not yet implemented")
    override val position: Pose2d
        get() = Pose2d()
    override val extension1: ExampleExtension = ThingDoer(hw)

    override fun doThing() {
        TODO("Not yet implemented")
    }

    override fun halt() {
        drive(0.0,0.0,0.0)
    }

    override fun drive(x: Double, y: Double, theta: Double) {
        // Calculate the denominator so that it scales from [-1, 1]
        val denominator: Double = max(abs(y) + abs(x) + abs(theta), 1.0)

        // Drive the robot
        FLMotor.setRunMode(Motor.RunMode.RawPower)
        BLMotor.setRunMode(Motor.RunMode.RawPower)
        FRMotor.setRunMode(Motor.RunMode.RawPower)
        BRMotor.setRunMode(Motor.RunMode.RawPower)

        FLMotor.set((y + x - theta) / denominator)
        BLMotor.set((y - x - theta) / denominator)

        FRMotor.set((-y + x - theta) / denominator) // Robot is backwards in code but this should allow the robot to be better for soccer.
        BRMotor.set((-y - x - theta) / denominator)
    }

    override fun rotate(theta: Double) {
        TODO("Not yet implemented")
    }

    override fun update() {
        super.update()
//        TODO("Not yet implemented")
    }
}