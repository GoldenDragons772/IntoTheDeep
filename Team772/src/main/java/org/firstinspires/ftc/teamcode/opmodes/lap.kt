package org.firstinspires.ftc.teamcode.opmodes

import com.pedropathing.localization.Pose
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.auto.lappath
import org.firstinspires.ftc.teamcode.helpers.Util.blockPath
import org.firstinspires.ftc.teamcode.implementation.RootSystem

// ~ should always sort last alphabetically
@TeleOp(name="~victorylap")
class lap : LinearOpMode() {
    override fun runOpMode() {

        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = false)
        root.follower.setStartingPose(Pose(8.0, 56.0, Math.toRadians(0.0)))
//        follower.setMaxPower(0.8)


        waitForStart()
        //The actual auto code
        runBlocking {
            launch { root.update() }
            root.follower.blockPath( lappath.line1, 0.9, true).join()
            root.follower.blockPath( lappath.line2, 0.9, true).join()
            root.follower.blockPath( lappath.line3, 0.9, true).join()
        }
    }
}
