package org.firstinspires.ftc.teamcode.implementation.commands

import android.util.Log
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.Subsystem
import com.arcrobotics.ftclib.command.WaitCommand
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import org.firstinspires.ftc.teamcode.auto.AlignTranslationalPath
import org.firstinspires.ftc.teamcode.implementation.Constants
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.vision.SampleDetection
import org.opencv.core.Point
import kotlin.math.pow

class GrabSampleCommand(private val root: RootSystem) : Command {
    private var foundSample: Point? = null
    private var done = false
    private var firstGo = true

    override fun initialize() {
        super.initialize()

        Log.i("Grab Command", "Grab Command Init")

//        sampleDetector = SampleDetection(root.telemetry, false)
//        val webcamName = root.hw.get(WebcamName::class.java, "GDVision")
//        root.intake.camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName)
    }

    override fun execute() {
        if (done) return
        try {
            if (root.intake.sampleDetector.centroid.get() != null) {
                // TODO: Move a little bit farther and then find the centroid
                if (firstGo) {
//                    root.intake.setLinkage(root.intake.horizontalSlideExtensionConversion(root.intake.horizontalSlideExtension - 0.5))
//                        .schedule()
                    firstGo = false
                    return
                }

                foundSample = root.intake.sampleDetector.centroid.get()

            } else {
                val linkageValue = root.intake.valueCache.linkagePosition - Constants.VISION_LONG_SEARCH_SPEED
                root.telemetry.addData("linkageValue", linkageValue)
                if (linkageValue < 0) { // TODO: get a better lower bound than 0
                    // Move to the left and start searching again if nothing is found.
                    FollowPath(root.follower, AlignTranslationalPath.alignLatitudinal(root.follower, root.follower.pose.y - Constants.VISION_LAT_SEARCH_SPEED)).andThen(
                    root.intake.setLinkage(IntakeSystem.LinkagePosition.FULL)).schedule()
                } else {
                    root.intake.setLinkage(linkageValue).schedule()
                }
            }
        } catch (e: NullPointerException) {
            Log.i("CAMERA", "ong dumbahh error") // I don't know why this line is necessary
        }
        Log.i("Vision", root.intake.horizontalSlideExtension.toString())
        if (foundSample != null) {
            Log.i("Vision", "found sample")
            // Claw is initially at an extreme x value, and when the position of the found sample is saved it needs to be able to move closer to the sample
            // Right now we're freezing a snapshot of camera space, which needs to be converted into linkage space in order to give the difference to the linkages
            // We also need to convert the y coordinates to the x (?) space of the robot
            // All operations have to be operated on a single frame because we can't be sure that a sample is the same sample between frames.
            val xPosInches = corelation(SampleDetection.WIDTH - foundSample!!.x)
            val xDiff = Constants.CAMERA_BOTTOM_OFFSET - xPosInches // inches
            if (xDiff > Constants.VISION_MAX_HEIGHT) { // Discard if it's too high up the screen and keep looking for values further down
                foundSample = null;
                return
            }
            // Convert the difference (inches) to an output (servo space) to be used with the horizontal slides.

            // horizontalSlideExtension seems to not be updating.
            assert(root.intake.horizontalSlideExtension > 0) { root.intake.horizontalSlideExtension }
            assert(root.intake.horizontalSlideExtension > xDiff) { "$xDiff $xPosInches" }
            Log.i("Vision", "$xDiff $xPosInches")
            val outputValue =
                root.intake.horizontalSlideExtensionConversion(root.intake.horizontalSlideExtension - xDiff)
            // Throwing an error if the value is out of the range makes debugging easier than coercing the values
            // into an acceptable range because we see if the values are actually insane.
            assert(outputValue in 0.0..IntakeSystem.LEFT_PIVOT_TARGET) { outputValue }

            val yDiff = ((320 / 2) - foundSample!!.y) * Constants.INCHES_PER_CAMERA_Y
//            val sign = if (root.follower.pose.heading in Math.PI..2 * Math.PI) -1 else 1;
            Log.i("VisionH", root.follower.pose.heading.toString())
            Log.i("VisionH", yDiff.toString())
            Log.i("Vision", outputValue.toString())
            // Set the linkage to the position, then perform the vision wrist stuff, then strike, grab, and hover again.
            root.intake.setLinkage(outputValue)
                            .andThen(WaitCommand(500))
                            .andThen(InstantCommand(root.intake::visionWristRotation)).andThen(WaitCommand(250))
//                           ` .andThen(HoldPointCommand(root.follower, Pose(root.follower.pose.x, root.follower.pose.y - (yDiff)))).withTimeout(1000)
                            .andThen(FollowPath(root.follower, AlignTranslationalPath.alignLatitudinal(root.follower, root.follower.pose.x - yDiff), 1.0))
                            .andThen(root.intake.strikeIntake())
                            .andThen(WaitCommand(250))
                            .andThen(root.intake.setClaw(IntakeSystem.IntakePosition.TARGET))
                            .andThen(WaitCommand(250))
                            .andThen(root.intake.hoverIntake()).schedule()
            Log.i("Grab Command", "Command Finished")
            done = true
        }
    }

    override fun isFinished(): Boolean {
        return done
    }

    fun corelation(x: Double): Double { // input pixels to output inches
//        return 0.842 * exp(x* 0.00594)
//        return 0.0176 * x - 0.205;
        return 0.0817 + 0.0112 * x + 1.9 * (10.0).pow(-5) * x * x
    }

    override fun getRequirements(): MutableSet<Subsystem> = mutableSetOf()
}