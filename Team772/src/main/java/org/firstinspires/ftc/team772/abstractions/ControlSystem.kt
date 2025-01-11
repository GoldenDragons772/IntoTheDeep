package org.firstinspires.ftc.team772.abstractions

import com.arcrobotics.ftclib.geometry.Pose2d
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.helpers.PIDController

/**
 * Main drive system: OpModes (or helpers) should use this in order to move the robot.
 * This is basically an abstraction over the robot: If the autos or teleop need to do something, it should go through here.
 */
interface ControlSystem : ClimbExtension {
    val hw: HardwareMap
    var hubs: MutableList<LynxModule>

    /**
     * The position of the robot.
     */
    val position: Pose2d
    val pathFollowerPID: PIDController

    /**
     * Stop the robot, including cancelling all paths.
     */
    fun stop()

    /**
     * Tells the robot to stop moving.
     */
    fun halt()

    /**
     * Main drive function: takes x, y, θ and moves the robot accordingly.
     */
    fun drive(x: Double, y: Double, theta: Double)

    /**
     * Updates the robot's sensors and updates things that need updating.
     */
    fun update() {
        bulkRead()
    }

    /**
     * Sets up bulk reads/writes
     */
    fun initBulkReads() {
        for (hub in hubs) {
            hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL

        }
    }

    /**
     * Utility to update robot cache.
     */
    fun bulkRead() {
        for (hub in hubs) {
            hub.clearBulkCache()
        }
    }

}
