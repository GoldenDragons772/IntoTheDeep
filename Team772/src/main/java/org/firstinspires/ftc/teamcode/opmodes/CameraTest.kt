package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.implementation.Constants
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.AlignWithSample
import org.firstinspires.ftc.teamcode.vision.SampleDetection
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import kotlin.math.PI

// ~ should always sort last alphabetically
@TeleOp(name = "~Camera Test")
class CameraTest : CommandOpMode() {
    private lateinit var root: RootSystem
    private lateinit var camera: OpenCvCamera
    private lateinit var sampleDetector: SampleDetection
    private lateinit var wristServo: Servo
    private lateinit var leftStrikeServo: Servo
    private lateinit var rightStrikeServo: Servo
    private lateinit var leftLinkageServo: Servo
    private lateinit var rightLinkageServo: Servo
    private var lastRotation = 0.0

    private var linkagePosition = 0.0;
    private lateinit var alignWithSample: AlignWithSample


    override fun run() {
        super.run()
        root.update()
        root.teleOpDrive(1.0, 0.0, 0.0)


//        // Wrist Servo
//        var rotationValue = if (sampleDetector.sampleRotation == -70.0) lastRotation else sampleDetector.sampleRotation
//        var inputValue = ((rotationValue) / PI + 0.5) % 1
//        if (inputValue < 0) inputValue += 1
//        wristServo.position = inputValue * Constants.VISION_SERVO_MULTIPLIER


//        telemetry.addData("Theta --", rotationValue)
//        telemetry.addData("Rotation", inputValue)

//        leftStrikeServo.position = IntakeSystem.LEFT_PIVOT_TARGET
//        rightStrikeServo.position = IntakeSystem.RIGHT_PIVOT_TARGET

//        try {
//            if (sampleDetector.centroid != null) {
//                val xval = sampleDetector.centroid.x
//                val difference =
//                    (SampleDetection.WIDTH / 2 - xval) / SampleDetection.WIDTH * Constants.GOOFY_AHH_VERTICAL_SEGMENT
//            leftLinkageServo.position = (leftLinkageServo.position + difference).clamp(0.0,1.0)
//            rightLinkageServo.position = (rightLinkageServo.position + difference).clamp(0.0,1.0)
//            telemetry.addLine("Moving slides ppv $difference")
//            telemetry.addLine("Moving slides ppv ${xval}")
//
//            }
//        } catch (e: NullPointerException){
//            Log.i("CAMERA","ong dumbahh error")
//        }

//        telemetry.update()
//        lastRotation = rotationValue
    }

    override fun initialize() {
        root = RootSystem(hardwareMap, telemetry, false)
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        wristServo = hardwareMap.get(Servo::class.java, "hSwivelServo")
        wristServo.direction = Servo.Direction.REVERSE

        // strike servo
        leftStrikeServo = hardwareMap.get(Servo::class.java, "hLeftStrike")
        rightStrikeServo = hardwareMap.get(Servo::class.java, "hRightStrike")

        // Linkage Servo
        leftLinkageServo = hardwareMap.get(Servo::class.java, "lLinkageServo")
        rightLinkageServo = hardwareMap.get(Servo::class.java, "rLinkageServo")

        rightLinkageServo.direction = Servo.Direction.REVERSE
        rightStrikeServo.direction = Servo.Direction.REVERSE

        leftStrikeServo.position = IntakeSystem.LEFT_PIVOT_TARGET
        rightStrikeServo.position = IntakeSystem.RIGHT_PIVOT_TARGET


        rightLinkageServo.position = IntakeSystem.RIGHT_LINKAGE_TARGET
        leftLinkageServo.position = IntakeSystem.LEFT_LINKAGE_TARGET

//        sampleDetector = SampleDetection( telemetry)
//        val webcamName = hardwareMap.get(WebcamName::class.java, "GDVision")
//        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName)
//        camera.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
//            override fun onOpened() {
//                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT)
//                camera.setPipeline(sampleDetector)
//                FtcDashboard.getInstance().startCameraStream(camera, 100.0);
//            }
//
//            override fun onError(p0: Int) {}
//        })

        alignWithSample = AlignWithSample(root.follower, root.intake)

//        alignWithSample.schedule()

//        root.intake.toggleIntake().schedule()

        schedule(
            WaitUntilCommand(this::opModeIsActive),
            SequentialCommandGroup(
                alignWithSample
            )
        )
    }
}