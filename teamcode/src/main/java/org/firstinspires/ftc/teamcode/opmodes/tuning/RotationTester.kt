package org.firstinspires.ftc.teamcode.opmodes.tuning

import com.arcrobotics.ftclib.command.*
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathBuilder
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import kotlin.math.PI

@TeleOp(name = "~RotationTester")
class RotationTester : CommandOpMode() {
    lateinit var root: RootSystem

    override fun initialize() {
        root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = true)
        var offset = 0.0
        val fp = { angle: Double ->
            offset += 2.0;
            val newPoint = Pose(root.follower.pose.x + offset, root.follower.pose.y, angle * 2.0)
            FollowPath(root.follower,
                PathBuilder().addPath(BezierLine(root.follower.pose, newPoint))
                    .setConstantHeadingInterpolation(angle * 2.0).build(),
                true
            )
        }

        waitForStart()
        RepeatCommand(
        SequentialCommandGroup(
            fp(PI / 2),
            WaitCommand(1000),
            fp(PI),
            WaitCommand(1000),
            fp(3 * PI / 2),
            WaitCommand(1000),
            fp(0.0),
            WaitCommand(1000),
        )
        )
            .alongWith(
                RepeatCommand(
                    InstantCommand({
                        root.update()
                    })
                )
            ).schedule()
    }

}
