package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.auto.lappath
import org.firstinspires.ftc.teamcode.implementation.RootSystem

// ~ should always sort last alphabetically
@TeleOp(name="~victorylap")
class lap : CommandOpMode() {
    override fun initialize() {

        val root = RootSystem(hardwareMap, telemetry)
        root.follower.setStartingPose(Pose(8.0, 56.0, Math.toRadians(0.0)))
//        follower.setMaxPower(0.8)


        //The actual auto code
        schedule(
            WaitUntilCommand(this::opModeIsActive),
            RunCommand({
                root.update()
            }),
            SequentialCommandGroup(
                // preload
                FollowPath(root.follower, lappath.line1, true, 0.9),
                FollowPath(root.follower, lappath.line2, true, 0.9),
                FollowPath(root.follower, lappath.line3, true, 0.9),
                //

            )
        )
    }
}