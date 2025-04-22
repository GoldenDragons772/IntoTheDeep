package org.firstinspires.ftc.teamcode.opmodes


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.implementation.IntakeState
import org.firstinspires.ftc.teamcode.implementation.LinkageState
import org.firstinspires.ftc.teamcode.implementation.RootSystem

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
            root.intake.setClaw(IntakeState.HOME)
            root.intake.setLinkage(LinkageState.FULL)
            root.intake.hoverIntake()
            root.intake.setPivot(IntakeState.HOME)
        }
        waitForStart()
        root.vision.enable()
//        root.intake.toggleIntake().schedule()
        while (!isStopRequested) {
            root.update()
        }
    }


}