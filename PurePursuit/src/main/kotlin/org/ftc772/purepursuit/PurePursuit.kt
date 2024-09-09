package org.ftc772.purepursuit

import kotlin.math.*

class PurePursuit {
    companion object {
        /**
         * Gets the point distance along path.
         */
        fun getPointOnPath(path: List<Point>, distance: Double): Point {
            var totalDistance = 0.0
            if (distance == 0.0) {
                return path[0]
            }
            // Iterate through all points and add their distances up
            for (i in path.indices) {
                // If it's not the first index, add the distance from the last point to the total distance.
                if (i != 0) totalDistance += (path[i - 1].distanceTo(path[i]))
                if (distance > totalDistance) continue
                // From the point we know before the point we're trying to find, translate to the origin, find the new point in relation to the next point, and then translate back.
                val point = path[i - 1]
                val nextPoint = path[i]
                val distanceToNextPoint = point.distanceTo(nextPoint)
                val remainingDistance = totalDistance - (distance)
                val newPoint = Point(
                    remainingDistance / distanceToNextPoint * (nextPoint.x - point.x) + point.x,
                    remainingDistance / distanceToNextPoint * (nextPoint.y - point.y) + point.y
                )
                return newPoint
            }
            return Point(0.0, 0.0)
        }

        /**
         * Math!
         * @see <a href="https://wiki.purduesigbots.com/~gitbook/image?url=https%3A%2F%2Fdrive.google.com%2Fuc%3Fexport%3Dview%26id%3D182XfvWoYVHroI1VItT317mg1F3445JMs&width=768&dpr=1&quality=100&sign=821c21aeea1082c904e765e599b3e25bf9936f2bb3ca2cb86b74b75d940becda">Line Circle Intersection</a>
         */
        fun intersection(lookahead: Double, p1: Point, p2: Point, center: Point): List<Point>? {
            val p1Offset = Point(p1.x - center.x, p1.y - center.y)
            val p2Offset = Point(p2.x - center.x, p2.y - center.y)
            val deltaPoint = Point(p2Offset.x - p1Offset.x, p2Offset.y - p1Offset.y)
            val deltaDistance = sqrt(deltaPoint.x.pow(2) + deltaPoint.y.pow(2))
            val bigD = p1Offset.x * p2Offset.y - p2Offset.x * p1Offset.y
            val discrim = lookahead.pow(2) * deltaDistance.pow(2) - bigD.pow(2)
            if (discrim < 0) return null

            var dySign = deltaPoint.y.sign
            if (dySign == 0.0) dySign = 1.0

            val points = mutableListOf<Point>()

            val x1 = (bigD * deltaPoint.y + dySign * deltaPoint.x * sqrt(discrim)) / (deltaDistance.pow(2)) + deltaPoint.x
            val x2 = (bigD * deltaPoint.y - dySign * deltaPoint.x * sqrt(discrim)) / (deltaDistance.pow(2)) + deltaPoint.x

            val y1 = (-bigD * deltaPoint.x + abs(deltaPoint.y) * sqrt(discrim)) / (deltaDistance.pow(2)) + deltaPoint.y
            val y2 = (-bigD * deltaPoint.x - abs(deltaPoint.y) * sqrt(discrim)) / (deltaDistance.pow(2)) + deltaPoint.y

            val minX = min(p1Offset.x, p2Offset.x)
            val minY = min(p1Offset.y, p2Offset.y)

            val maxX = max(p1Offset.x, p2Offset.x)
            val maxY = max(p1Offset.y, p2Offset.y)

            if (x1 in minX..maxX && y1 in minY..maxY) points += Point(x1, y1)
            if (x2 in minX..maxX && y2 in minY..maxY && (x1 != x2 && y1 != y2)) points += Point(x2, y2)
            if (points.size == 0) return null
            return points.map { Point(it.x + center.x, it.y + center.y) }
        }

        /**
         * Finds the farthest intersection of a circle along the path.
         */
        fun pathIntersection(lookahead: Double, path: List<Point>, pos: Point): Point? {
            val points = mutableListOf<Point>()
            // Iterate through edges and check the circle against each one.
            for (i in path) {
                // We don't care about the first point because a path has points-1 edges.
                if (path.indexOf(i) == 0) continue
                // The last point and the current point make up the current edge.
                val lastPoint = path[path.indexOf(i) - 1]
                // Find the zero, one, or two points on the edge that intersect the circle.
                val intersectionPoints =
                    intersection(
                        lookahead,
                        Point(lastPoint.x, lastPoint.y),
                        Point(i.x, i.y),
                        pos
                    )
                // If there are no intersection points, move on to the next edge.
                if (intersectionPoints == null) {
                    continue
                }
                // If there is only one point, add it and move on.
                if (intersectionPoints.size == 1) {
                    points += intersectionPoints[0]
                    continue
                }
                // If there are two points, find the point that is the closest to the second point.
                if (intersectionPoints[0].distanceTo(i) < intersectionPoints[1].distanceTo(i)) {
                    points += intersectionPoints[0]
                    continue
                }
                points += intersectionPoints[1]
                continue
            }
            // If there are no intersections, scream.
            if (points.isEmpty()) return null
            // Else return the last point in the list.
            // This assumes that the path is in order from start to finish, which it in most cases should be.
            return points[points.size - 1]
        }
    }
}

