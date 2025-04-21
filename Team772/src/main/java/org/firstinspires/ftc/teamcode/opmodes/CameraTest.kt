package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.implementation.IntakePosition
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.LinkagePosition
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.GrabSampleCommand

// ~ should always sort last alphabetically
@TeleOp(name = "~Camera Test")
class CameraTest : LinearOpMode() {

    private lateinit var wristServo: Servo
    private lateinit var root: RootSystem

    override fun runOpMode() {
        root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = false)


        wristServo = hardwareMap.get(Servo::class.java, "hSwivelServo")
        wristServo.direction = Servo.Direction.REVERSE

        runBlocking {
            root.intake.setClaw(IntakePosition.HOME)
            root.intake.setLinkageFunc(LinkagePosition.FULL)
            root.intake.hoverIntake()
            root.intake.setPivot(IntakePosition.HOME)
        }
        waitForStart()
        runBlocking {
            GrabSampleCommand(root);
        }
//        root.intake.toggleIntake().schedule()
        while (!isStopRequested) {
            root.update()
        }
    }


}