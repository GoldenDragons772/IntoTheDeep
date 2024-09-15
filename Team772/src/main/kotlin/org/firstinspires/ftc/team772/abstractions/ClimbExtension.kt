package org.firstinspires.ftc.team772.abstractions

interface ClimbExtension {

    /**
     * Variable for whether the robot is climbed up.
     */
    var climbState: Boolean

    /**
     * Makes the robot climb up.
     */
    fun climb()

    /**
     * Makes the robot climb down.
     */
    fun unclimb(lt: Double, rt: Double)
}