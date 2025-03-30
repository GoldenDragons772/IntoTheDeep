package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.arcrobotics.ftclib.command.CommandOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.vision.RedSampleDetection
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

// ~ should always sort last alphabetically
@TeleOp(name = "~Camera Test")
class CameraTest : CommandOpMode() {
    private lateinit var camera: OpenCvCamera
    private lateinit var sampleDetector: RedSampleDetection
    private lateinit var wristServo: Servo
    private var lastRotation = 0.0;
    private lateinit var root: RootSystem

    override fun run() {
        //Wrist Servo
        val rotationValue = if (sampleDetector.sampleRotation == -70.0) lastRotation else sampleDetector.sampleRotation
        val inputValue = rotationValue / 3.141592653589793 / 2 + 0.5
        this.root.telemetry.addData("Theta",inputValue)
        wristServo.position = inputValue
        lastRotation = rotationValue
    }

    override fun initialize() {
        root = RootSystem(hardwareMap, telemetry)
        wristServo = hardwareMap.get(Servo::class.java, "hSwivelServo")
        wristServo.direction = Servo.Direction.REVERSE
        sampleDetector = RedSampleDetection(root)
        val webcamName = hardwareMap.get(WebcamName::class.java, "GDVision")
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName)
        camera.openCameraDeviceAsync(object: OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                camera.startStreaming(640,480,OpenCvCameraRotation.UPRIGHT)
                camera.setPipeline(sampleDetector)
                FtcDashboard.getInstance().startCameraStream(camera, 100.0);
            }
            override fun onError(p0: Int) {}
        })
    }
}