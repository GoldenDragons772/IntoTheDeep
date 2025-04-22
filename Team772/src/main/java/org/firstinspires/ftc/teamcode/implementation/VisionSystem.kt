package org.firstinspires.ftc.teamcode.implementation

import android.util.Log
import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.auto.AlignTranslationalPath
import org.firstinspires.ftc.teamcode.helpers.Util.blockPath
import org.firstinspires.ftc.teamcode.vision.SampleDetection
import org.opencv.core.Point
import kotlin.math.pow

class VisionSystem(private val root: RootSystem) {
    private var foundSample: Point? = null
    private var done = false
    private var firstGo = true
    private var running = false

    init {
        Log.i("Grab Command", "Grab Command Init")

//        sampleDetector = SampleDetection(root.telemetry, false)
//        val webcamName = root.hw.get(WebcamName::class.java, "GDVision")
//        root.intake.camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName)
    }

    suspend fun periodic() {
        if (done || !running) return
        Log.i("Vision", root.intake.linkage.extension.toString())
        if (foundSample != null) {
            Log.i("Vision", "found sample")
            // Claw is initially at an extreme x value, and when the position of the found sample is saved it needs to be able to move closer to the sample
            // Right now we're freezing a snapshot of camera space, which needs to be converted into linkage space in order to give the difference to the linkages
            // We also need to convert the y coordinates to the x (?) space of the robot
            // All operations have to be operated on a single frame because we can't be sure that a sample is the same sample between frames.
            val xPosInches = corelation(SampleDetection.WIDTH - foundSample!!.x)
            val xDiff = Constants.CAMERA_BOTTOM_OFFSET - xPosInches // inches
            if (xDiff > Constants.VISION_MAX_HEIGHT || xDiff < 0) { // Discard if it's too high up the screen and keep looking for values further down
                val linkageValue = root.intake.linkage.cachedPos - Constants.VISION_LONG_SEARCH_SPEED
                foundSample = null
                if (linkageValue > 0) {
                    root.intake.linkage.set(linkageValue)
                }
                return
            }
            // Convert the difference (inches) to an output (servo space) to be used with the horizontal slides.

            // horizontalSlideExtension seems to not be updating.
            assert(root.intake.linkage.extension > 0) { root.intake.linkage.extension }
            assert(root.intake.linkage.extension > xDiff) { "$xDiff $xPosInches" }
            Log.i("Vision", "$xDiff $xPosInches")
//            val outputValue =
//                root.intake.linkage.setLength(root.intake.linkage.extension - xDiff)
            // Throwing an error if the value is out of the range makes debugging easier than coercing the values
            // into an acceptable range because we see if the values are actually insane.
//            assert(outputValue in 0.0..IntakeSystem.PIVOT_TARGET) { outputValue }

            val yDiff = ((SampleDetection.SUBHEIGHT / 2) - foundSample!!.y) * Constants.INCHES_PER_CAMERA_Y
//            val sign = if (root.follower.pose.heading in Math.PI..2 * Math.PI) -1 else 1;
            Log.i("VisionH", root.follower.pose.heading.toString())
            Log.i("VisionH", "$yDiff yDiff")
//            Log.i("Vision", outputValue.toString())
            // Set the linkage to the position, then perform the vision wrist stuff, then strike, grab, and hover again.

            root.intake.linkage.extension -= xDiff
            delay(500)
            root.intake.wrist.visionWristRotation()
            delay(250)
//                           ` .andThen(HoldPointCommand(root.follower, Pose(root.follower.pose.x, root.follower.pose.y - (yDiff)))).withTimeout(1000)
            root.follower.blockPath(
                AlignTranslationalPath.alignLatitudinal(
                    root.follower.pose,
                    root.follower.pose.x - yDiff
                ), 0.8, false
            ).join()
            root.intake.strikeIntake()
            delay(250)
            root.intake.setClaw(ClawState.CLOSED)
            delay(250)
            root.intake.hoverIntake()

            Log.i("Sample Auto", "Command Finished")
            done = true
        }
        try {
            if (root.intake.sampleDetector.centroid.get() != null) {
                // TODO: Move a little bit farther and then find the centroid
                if (firstGo) {
//                    root.intake.linkage.set(root.intake.linkage.horizontalSlideExtensionConversion(root.intake.linkage.horizontalSlideExtension - 0.5))
//                        .schedule()
                    firstGo = false
                    return
                }

                foundSample = root.intake.sampleDetector.centroid.get()

            } else {
                val linkageValue = root.intake.linkage.cachedPos - Constants.VISION_LONG_SEARCH_SPEED
                root.telemetry.addData("linkageValue", linkageValue)
                if (linkageValue < 0) { // TODO: get a better lower bound than 0
                    // Move to the left and start searching again if nothing is found.
                    root.intake.linkage.set(LinkageState.FULL)

                    root.follower.followPath(
                        AlignTranslationalPath.alignLatitudinal(
                            root.follower.pose,
                            root.follower.pose.y - Constants.VISION_LAT_SEARCH_SPEED
                        )
                    )

                    Log.i("Sample Auto", "Move to Translational.")
                } else {
                    root.intake.linkage.set(linkageValue)
                }
            }
        } catch (e: NullPointerException) {
            Log.i("CAMERA", "ong dumbahh error") // I don't know why this line is necessary
        }
    }

    fun corelation(x: Double): Double { // input pixels to output inches
//        return 0.842 * exp(x* 0.00594)
//        return 0.0176 * x - 0.205;
        return 0.0817 + 0.0112 * x + 1.9 * (10.0).pow(-5) * x * x
    }
    fun enable() {
        this.running = true
        this.done = false
    }
    fun disable(){
        this.running = false
        this.done = true
    }
}
