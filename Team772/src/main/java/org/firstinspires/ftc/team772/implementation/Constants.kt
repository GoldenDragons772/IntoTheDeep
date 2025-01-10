package org.firstinspires.ftc.team772.implementation

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap

@Config
object Constants {

    //// Pure Pursuit config values
    // The initial value to use as the lookahead.
    @JvmField var LOOKAHEAD = 0.5

    // The maximum value to use as a lookahead.
    @JvmField var MAX_LOOKAHEAD = 1

    // The search step to increase the lookahead by if no intersection is found.
    @JvmField var LOOKAHEAD_STEP = 0.1

    // The distance to the end of the path that the robot needs to be in order to consider itself finished.
    @JvmField var MIN_GOAL_DISTANCE = 1

    /**
     * Values for arm.
     */
    @JvmField var ARM_HOME = 0
    @JvmField var ARM_HIGH_CLIMB = 2950 // Moved up by 150
    @JvmField var ARM_LOW_CLIMB = 1350

    /**
     * Values for the intake. Change these for tuning. (Must supply a value between 0.0 and 1.0)
     * */
    @JvmField var SLIDE_HOME = 0 //70 works well
    @JvmField var SLIDE_EDGE = 400
    @JvmField var SLIDE_RECALIBRATE = -100
    @JvmField var SLIDE_TARGET = 1500

    // Strike Position (Joint 1)
    @JvmField var STRIKE_SERVO_HOME = 0.885
    @JvmField var STRIKE_SERVO_TARGET = 0.53

    @JvmField var STRIKE_SERVO_TRANSFER = 0.635

    //Intake Wrist Positions
    @JvmField var INTAKE_WRIST_HOME = 0.07
    @JvmField var INTAKE_WRIST_PERP = 0.4

    // Claw Positions
    @JvmField var CLAW_SERVO_HOME = 0.635
    @JvmField var CLAW_SERVO_TARGET = 0.95
    @JvmField var CLAW_SERVO_CLENCH = 1.0

    // Pivot Positions (Joint 2)
    @JvmField var PIVOT_SERVO_HOME = 0.1//TODO: Swap variable names -- Home is on the ground?
    @JvmField var PIVOT_SERVO_TARGET = 1.0
    @JvmField var PIVOT_SERVO_TRANSFER = 1.0

    @JvmField var SLIDE_MOTOR_SPEED = 0.7

    /**
     * Values for the outtake. Change for tuning.
     * */
    @JvmField var SWING_SERVO_TARGET = 0.0
    @JvmField var WRIST_SERVO_HOME = 0.35

    /**
     * Claw home position.
     */
    @JvmField var UNGRIPPY = 0.6
    @JvmField var SWING_SERVO_HOME = 0.95
    @JvmField var WRIST_SERVO_TARGET = 0.25
    @JvmField var SWING_SERVO_INIT = 0.75

    /**
     * Claw target position.
     */
    @JvmField var GRIPPY = 0.0
}