package org.firstinspires.ftc.team772.autos

import org.ftc772.purepursuit.Point

/**
 * Classes to contain path scripts.
 * The first point is the initial position of the robot and each subsequent point is part of the path.
 */
class AutoPath(val start: Point, vararg elements: Point) {
    companion object {
        val XYZDUMMYAUTO = AutoPath(
            Point(-0.79, 0),
            Point(8.06, 3.49),
            Point(8.46, -2.74),
            Point(-1.06, -4.96),
            Point(-6.13, -1.81),
            Point(-7.94, -9.58),
            Point(3.82, -13.45),
            Point(14.67, -11.55),
            Point(22, -4.12)
        )
    }

    var internalPath: List<Point> = elements.asList()
        private set

}