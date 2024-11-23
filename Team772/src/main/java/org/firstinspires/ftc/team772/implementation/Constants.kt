package org.firstinspires.ftc.team772.implementation

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap

@Config
object Constants {
    //// Pure Pursuit config varues
    // The initial varue to use as the lookahead.
    @JvmField var LOOKAHEAD = 0.5

    // The maximum varue to use as a lookahead.
    @JvmField var MAX_LOOKAHEAD = 1

    // The search step to increase the lookahead by if no intersection is found.
    @JvmField var LOOKAHEAD_STEP = 0.1

    // The distance to the end of the path that the robot needs to be in order to consider itself finished.
    @JvmField var MIN_GOAL_DISTANCE = 1

    /**
     * Values for arm.
     */
    @JvmField var ARM_HOME = 0
    @JvmField var ARM_HIGH_CLIMB = 2850 // Moved up by 150
    @JvmField var ARM_LOW_CLIMB = 1350

    /**
     * Values for the intake. Change these for tuning. (Must supply a varue between 0.0 and 1.0)
     * */
    @JvmField var SLIDE_HOME = 60 //70 works well
    @JvmField var SLIDE_EDGE = 400
    @JvmField var SLIDE_SERVO_HOME = 0.1
    @JvmField var PIVOT_SERVO_HOME = 0.47//TODO: Swap variable names
    @JvmField var CLAW_SERVO_HOME = 0.0
    @JvmField var SLIDE_TARGET = 1050
    @JvmField var SLIDE_SERVO_TARGET = 0.3
    @JvmField var PIVOT_SERVO_TARGET = 1.0
    @JvmField var CLAW_SERVO_TARGET = 0.0
    @JvmField var SLIDE_MOTOR_SPEED = 0.7

    /**
     * Values for the outtake. Change for tuning.
     * */
    @JvmField var SWING_SERVO_TARGET = 0.0
    @JvmField var WRIST_SERVO_HOME = 0.0

    /**
     * Claw home position.
     */
    @JvmField var UNGRIPPY = 0.6
    @JvmField var SWING_SERVO_HOME = 0.95
    @JvmField var WRIST_SERVO_TARGET = 0.25

    /**
     * Claw target position.
     */
    @JvmField var GRIPPY = 0.2
}