package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.GrabSampleCommand

// ~ should always sort last alphabetically
@TeleOp(name = "~Camera Test")
class CameraTest : CommandOpMode() {

    private lateinit var wristServo: Servo
    private lateinit var root: RootSystem


    override fun run() {
        super.run()
        root.update()

/*
=======
        root.teleOpDrive(1.0, 0.0, 0.0)


//        // Wrist Servo
//        var rotationValue = if (sampleDetector.sampleRotation == -70.0) lastRotation else sampleDetector.sampleRotation
//        var inputValue = ((rotationValue) / PI + 0.5) % 1
//        if (inputValue < 0) inputValue += 1
//        wristServo.position = inputValue * Constants.VISION_SERVO_MULTIPLIER


//        telemetry.addData("Theta --", rotationValue)
//        telemetry.addData("Rotation", inputValue)


        // Wrist Servo
        if (done) return
        if (foundSample != null) {
            // 500 - 50 - transformation of linkage position in linkage space to linkage position in initial camera space
            // Claw is initially at an extreme x value, and when the position of the found sample is saved it needs to be able to move closer to the sample
            // Right now we're freezing a snapshot of camera space, which needs to be converted into linkage space in order to give the difference to the linkages
            // We also need to convert the y coordinates to the x (?) space of the robot
            val xPosInches = corelation(SampleDetection.WIDTH - foundSample!!.x)
            telemetry.addData("xPos", SampleDetection.WIDTH - foundSample!!.x)
            telemetry.addData("xPos -- related", xPosInches)
            val xDiff = Constants.CAMERA_BOTTOM_OFFSET - xPosInches // inches
                // Convert the difference (inches) to an output (servo space) to be used with the horizontal slides.
                val outputValue =
                    (root.intake.horizontalSlideExtensionConversion(root.intake.horizontalSlideExtension - xDiff))
                // Throwing an error if the value is out of the range makes debugging easier than coercing the values
                // into an acceptable range because we see if the values are actually insane.
                assert(outputValue in 0.0..IntakeSystem.LEFT_PIVOT_TARGET)
                Log.i("Camera", outputValue.toString())
                done = true
                root.intake.setLinkage(outputValue).andThen(WaitCommand(250)).andThen(InstantCommand({
                    var rotationValue =
                        if (sampleDetector.sampleRotation == -70.0) lastRotation else sampleDetector.sampleRotation
                    var inputValue = ((rotationValue) / PI + 0.5) % 1
                    if (inputValue < 0) inputValue += 1
                    wristServo.position = inputValue * Constants.VISION_SERVO_MULTIPLIER
                    telemetry.addData("Theta --", rotationValue)
                    telemetry.addData("Rotation", inputValue)
                })).andThen(WaitCommand(500)).andThen(root.intake.strikeIntake())
                    .andThen(WaitCommand(500))
                    .andThen(root.intake.setClaw(IntakeSystem.IntakePosition.TARGET))
                    .andThen(WaitCommand(500))
                    .andThen(root.intake.hoverIntake()).schedule()
                Log.i("Camera", "diff ${xDiff / Constants.INCHES_PER_LINKAGE}")
                telemetry.addData("theory out", outputValue)
                telemetry.addData("initial", initialLinkage)
                telemetry.addData("Moving slides ppv", xDiff)
                telemetry.addLine("Moving slides ppv ${foundSample!!}")
            return
        }

        try {
            if (sampleDetector.centroid != null) {
                // TODO: Move a little bit farther and then find the centroid
                foundSample = sampleDetector.centroid
                val yDiff = ((SampleDetection.HEIGHT / 2) - foundSample!!.y) * Constants.INCHES_PER_CAMERA_Y
                initialLinkage = root.intake.valueCache.linkagePosition
                root.follower.followPath(
                    PathBuilder().addPath(
                        BezierLine(
                            root.follower.pose,
                            Pose(root.follower.pose.x + yDiff * sin(root.follower.pose.heading), root.follower.pose.y)
                        )
                    ).build()
                )
            } else {
                root.intake.setLinkage(root.intake.valueCache.linkagePosition - Constants.VISION_LONG_SEARCH_SPEED).schedule()
            }
        } catch (e: NullPointerException) {
            Log.i("CAMERA", "ong dumbahh error")
        }


        telemetry.update()
*/

//        telemetry.update()

//        lastRotation = rotationValue
    }

    override fun initialize() {

        root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = false)

        wristServo = hardwareMap.get(Servo::class.java, "hSwivelServo")
        wristServo.direction = Servo.Direction.REVERSE
        root.intake.setClaw(IntakeSystem.IntakePosition.HOME).schedule()

        root.intake.setLinkage(IntakeSystem.LinkagePosition.FULL).schedule()
        root.intake.hoverIntake().schedule()
        root.intake.setPivot(IntakeSystem.IntakePosition.HOME).schedule()
        waitForStart()
        GrabSampleCommand(root).schedule();
//        root.intake.toggleIntake().schedule()
    }



}