package org.ftc772.purepursuit

import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Stores two numbers and provides helper functions for them.
 */
class Point(var x: Double, var y: Double) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    /**
     * Finds the distance from this point to another.
     */
    fun distanceTo(other: Point): Double {
        val xDiff = x - other.x
        val yDiff = y - other.y
        return sqrt((xDiff * xDiff + yDiff * yDiff))
    }

    /**
     * Find the angle from this point to another with [atan2][kotlin.math.atan2].
     */
    fun angleTo(other: Point): Double {
        return atan2(other.y - this.y, other.x - this.x)
    }

    override fun toString(): String {
        return String.format("(%2.1f, %2.1f)", this.x, this.y)
    }
}

