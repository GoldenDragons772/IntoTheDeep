package org.firstinspires.ftc.team772.abstractions

import com.arcrobotics.ftclib.command.InstantCommand

interface ClimbExtension {

    /**
     * Variable for whether the robot is climbed up.
     */
    var climbState: Boolean

    /**
     * Makes the robot climb up to the upper bar.
     */
    fun lowclimb(): InstantCommand

    /**
     * Makes the robot climb up to the upper bar.
     */
    fun highclimb(): InstantCommand

    /**
     * Makes the robot climb down.
     */
    fun unclimb(): InstantCommand
}