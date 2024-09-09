import org.ftc772.purepursuit.Point
import org.ftc772.purepursuit.PurePursuit.Companion.pathIntersection
import org.jcodec.api.SequenceEncoder
import org.jcodec.common.Codec
import org.jcodec.common.Format
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Rational
import org.jcodec.scale.AWTUtil
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.path
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.util.color.Color
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.assertIs

class PPTest {

    // Change for debugging
    // Path that the robot will try to follow.
    val PATH = listOf(
        Point(0.0, 0.0),
        Point(2.0, 0.3),
        Point(1.7, 2),
        Point(4.4, 2.9),
        Point(5.6, .8),
        Point(3.25, -2.1),
        Point(0.25, -0.6),
    )

    // Starting position of the robot.
    val START = Point(-0.5, 0)

    // Maximum attempts to go, or maximum attempts gone once it's used
    var ITERATIONS = 600

    // Minimum distance to the end point at which the robot stops.
    val MIN_STOP_DISTANCE = 0.2

    // Speed of the robot.
    val MOVESPEED = 0.2

    @Test
    fun testMain() {
        val (points, iterationsGone) = travel(600)
        createPlot(points, PATH, iterationsGone).save("debug.png", path = "src/test/debug")
        assert(points.last().distanceTo(PATH.last()) < MIN_STOP_DISTANCE + 0.05) { "The robot did not reach the end of the path." }
    }

    private fun travel(ITERATIONS: Int = 600): Pair<MutableList<Point>, Int> {
        var maxIteration = ITERATIONS
        // Leave alone
        val debugPoints = mutableListOf<Point>()
        var pos = START
        for (i in 0..ITERATIONS) {
            // Break if the current position is within the minimum acceptable distance.
            // Update the points list.
            debugPoints += Point(pos.x, pos.y)
            if (pos.distanceTo(PATH.last()) < MIN_STOP_DISTANCE) {
                maxIteration = i
                break
            }
            var lookahead = 0.1
            // Find the goal point.
            // Expand the lookahead until a point is found or a maximum lookahead is reached.
            while (lookahead < 1) {
                val intersectedPoint = pathIntersection(lookahead, PATH, pos)
                if (intersectedPoint == null) {
                    lookahead += .05
                    continue
                }
                pos = debugMoveTowardsPoint(intersectedPoint, pos)
                break
            }
        }
        return Pair(debugPoints, maxIteration)
    }

    fun debugMoveTowardsPoint(goal: Point, pos: Point): Point {
        // Get the angle towards the goal point.
        val angle = pos.angleTo(goal)
        val correction = pid((angle - robotRotation).toFloat())
        robotRotation += correction
        // Move towards the goal point.
        pos.x += cos(robotRotation) * MOVESPEED
        pos.y += sin(robotRotation) * MOVESPEED
        return pos
    }

    var robotRotation = 0.0

    var kp = 1.4f
    var kd = -.42f


    var lastError: Float? = null

    fun pid(error: Float): Float {
        if (lastError == null) {
            lastError = error
        }
        val p = error * kp
        val d = kd * (error - lastError!!) // / deltaTime but that's always 1 because this is run every frame


        return p + d
    }


    fun createVideo(debugPoints: List<Point>) {
        val images = mutableListOf<Plot>()
        for (i in 0..debugPoints.size) {
            val debugPointSublist = debugPoints.subList(0, i)
            images += createPlot(debugPointSublist, PATH, i)
        }
        val encoder = SequenceEncoder(
            NIOUtils.writableChannel(java.io.File("src/test/debug/output.mp4")),
            Rational.R(150, 3),
            Format.MOV,
            Codec.H264,
            null
        )
        for (i in images) {
            encoder.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(i.toBufferedImage(dpi = 50)))
        }
        encoder.finish()
    }

    private fun createPlot(points: List<Point>, path: List<Point>, i: Int = ITERATIONS): Plot {

        val p = plot {
            path {
                // Path followed
                x(path.map { it.x })
                y(path.map { it.y })
                width = 1.0
                color = Color.PURPLE
            }
            path {
                // Path taken
                x(points.map { it.x })
                y(points.map { it.y })
                color(points.indices)
                width = .75
            }
            layout {
                val currentTime = LocalDateTime.now()
                title = "Pure Pursuit Test ${currentTime.year}-${currentTime.monthValue}-${currentTime.dayOfMonth} ${
                    String.format(
                        "%02d%02d", currentTime.hour, currentTime.minute
                    )
                }"
                subtitle = "MSD = $MIN_STOP_DISTANCE  t=$i"
                theme = org.jetbrains.kotlinx.kandy.letsplot.style.Theme.DARCULA
            }
            if (points.isEmpty()) {
                return@plot
            }
            points {
                // Point
                x(listOf(points.last().x))
                y(listOf(points.last().y))
                color = Color.RED
                size = 7.0
            }
        }
        return p
    }
}