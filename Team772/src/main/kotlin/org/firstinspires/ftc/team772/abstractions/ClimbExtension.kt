package org.firstinspires.ftc.team772.abstractions

interface ClimbExtension {

    /**
     * Variable for whether the robot is climbed up.
     */
    var climbState: Boolean

    /**
     * Makes the robot climb up to the upper bar.
     */
    fun lowclimb()

    /**
     * Makes the robot climb up to the upper bar.
     */
    fun highclimb()

    /**
     * Makes the robot climb down.
     */
    fun unclimb()
}