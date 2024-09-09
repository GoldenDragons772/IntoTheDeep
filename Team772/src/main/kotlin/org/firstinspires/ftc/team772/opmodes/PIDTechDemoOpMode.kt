package org.firstinspires.ftc.team772.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.geometry.Pose2d
import com.arcrobotics.ftclib.geometry.Rotation2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.team772.helpers.PathFollower

@TeleOp(name = "PID Tech Demo")
class PIDTechDemoOpMode : CommandOpMode() {
    lateinit var pathFollower: PathFollower

    override fun initialize() {
        pathFollower = PathFollower(hardwareMap)

    }

    override fun run() {
        super.run()
        pathFollower.update()
        val packet = TelemetryPacket()
        pathFollower.hold(Pose2d(0.0,0.0, Rotation2d.fromDegrees(0.0)), packet)
        FtcDashboard.getInstance().sendTelemetryPacket(packet)
    }
}
