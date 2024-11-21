package org.firstinspires.ftc.team772.helpers

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.geometry.Pose2d
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.autos.AutoPath
import org.firstinspires.ftc.team772.implementation.Constants
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem
import org.ftc772.purepursuit.Point
import org.ftc772.purepursuit.PurePursuit
import kotlin.math.*

class PathFollower(private val hw: HardwareMap) {
    private val robot: ParallelPlateDrivesystem = ParallelPlateDrivesystem(hw)
    private var pathToFollow: List<Point>? = null
    val isFollowingPath: Boolean
        get() = pathToFollow != null

    /**
     * Follows the path.
     */
    private fun tickPath(tel: TelemetryPacket) {
        val currentPoint = Point(robot.position.x, robot.position.y)
        // Check if the robot is currently within the minimum required distance to the goal.
        if (currentPoint.distanceTo(pathToFollow!![pathToFollow!!.size - 1]) < Constants.MIN_GOAL_DISTANCE) {
            pathToFollow = null
            return
        }
        var tempLookahead = Constants.LOOKAHEAD + 0.0
        var goal: Point? = null
        // Increase search radius until MAX_LOOKAHEAD and break if a point is found.
        while (tempLookahead < Constants.MAX_LOOKAHEAD) {
            goal = PurePursuit.pathIntersection(Constants.LOOKAHEAD, pathToFollow!!, currentPoint)
            if (goal != null) break
            tempLookahead += Constants.LOOKAHEAD_STEP
        }
        if (goal == null) return
        // Drive towards the goal.
        val a = currentPoint.angleTo(goal)
        tel.fieldOverlay().setStroke("pink")
        tel.fieldOverlay().strokeCircle(currentPoint.x, currentPoint.y, tempLookahead)
        tel.fieldOverlay().strokeCircle(goal.x, goal.y, 0.5)
        // TODO: use motion profiling instead of just PID

        val drivePower = robot.pathFollowerPID.tick(currentPoint.distanceTo(goal))
        // lowkey might be wrong but that's alright.
        robot.drive(-cos(a) * drivePower, sin(a) * drivePower, 0.0)
    }

    fun update() {
        robot.update()

        val packet = debugDashboardUpdate(TelemetryPacket())
        if (isFollowingPath) {
            for (i in 1 until this.pathToFollow!!.size) {
                packet.fieldOverlay().setStroke("green")
                packet.fieldOverlay().strokeLine(
                    this.pathToFollow!![i - 1].x,
                    this.pathToFollow!![i - 1].y,
                    this.pathToFollow!![i].x,
                    this.pathToFollow!![i].y
                )
                packet.fieldOverlay().setStroke("blue")
                packet.fieldOverlay().strokeCircle(robot.position.x, robot.position.y, 0.5)
            }
            tickPath(packet)
        } else {
            robot.stop()
        }

        FtcDashboard.getInstance().sendTelemetryPacket(packet)
    }

    private fun debugDashboardUpdate(packet: TelemetryPacket): TelemetryPacket {
        packet.fieldOverlay().fillCircle(robot.position.x, robot.position.y, 1.0)
        packet.fieldOverlay().setFill("green")
        packet.addLine("x: ${robot.position.x}")
        packet.addLine("y: ${robot.position.y}")
        return packet
    }

    /**
     * PID Tech demo.
     */
    fun hold(pos: Pose2d, packet: TelemetryPacket) {
        val error = sqrt((robot.position.x - pos.x).pow(2.0) + (robot.position.y - pos.y).pow(2.0))
        val power = robot.pathFollowerPID.tick(error) * .25
        val angle = atan2(pos.y - robot.position.y, pos.x - robot.position.x)
        packet.fieldOverlay().strokeLine(robot.position.x, robot.position.y, pos.x, pos.y)
        packet.fieldOverlay().setStroke("green")
        packet.fieldOverlay().strokeLine(
            robot.position.x,
            robot.position.y,
            robot.position.x + cos(angle) * 5.0,
            robot.position.y + sin(angle) * 5.0
        )
        robot.drive(cos(angle) * power, sin(angle) * power, 0.0)
    }


    /**
     * Tells the robot to follow a path.
     */
    fun followPath(path: List<Point>) {
        pathToFollow = path
    }

    /**
     * Tells the robot to follow a path.
     */
    fun followPath(path: AutoPath) {
        followPath(path.internalPath)
    }

    /**
     * Cancels the robot's currently followed path.
     */
    fun stopFollowing() {
        pathToFollow = null
    }
}