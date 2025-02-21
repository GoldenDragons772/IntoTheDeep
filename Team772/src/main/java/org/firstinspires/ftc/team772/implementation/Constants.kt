package org.firstinspires.ftc.team772.implementation

import com.acmerobotics.dashboard.config.Config

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
     * PedroPathing Constants (So I don't go insane
     */
    @JvmField var BLUE_CHAMBER_XDISTANCE = 35.000
    @JvmField var BLUE_SPEC_PICKUP_XDISTANCE = 15.000

    /**
     * Values for arm.
     */
    @JvmField var ARM_HOME = 0
    @JvmField var ARM_HIGH_CLIMB = 3100 // Moved up by 150
    @JvmField var ARM_LOW_CLIMB = 200
    @JvmField var SPEC_HANG_PREP = 750
    @JvmField var SPEC_HANG = 1300

    /**
     * Values for the intake. Change these for tuning. (Must supply a value between 0.0 and 1.0)
     * */
    @JvmField var SLIDE_HOME = 0 //70 works well
    @JvmField var SLIDE_RECALIBRATE = -100
    @JvmField var SLIDE_TARGET = 1800

    // Strike Position (Joint 1)
    @JvmField var STRIKE_SERVO_HOME = 0.885
    @JvmField var STRIKE_SERVO_TARGET = 0.42
    @JvmField var STRIKE_SERVO_SMACK = 0.65

    @JvmField var STRIKE_SERVO_TRANSFER = 0.55
    @JvmField var STRIKE_SERVO_TRANSFER_SPEC = 0.8

    //Intake Wrist Positions
    @JvmField var INTAKE_WRIST_HOME = 0.0
    @JvmField var INTAKE_WRIST_PERP = 0.45
    @JvmField var INTAKE_WRIST_SPEC = 1.0
    @JvmField var INTAKE_WRIST_AUTO_PREP = 0.8

    // Claw Positions
    @JvmField var CLAW_SERVO_HOME = 0.4
    @JvmField var CLAW_SERVO_TARGET = 0.0
    @JvmField var CLAW_SERVO_CLENCH = 1.0

    // Pivot Positions (Joint 2)
    @JvmField var PIVOT_SERVO_HOME = 0.95
    @JvmField var PIVOT_SERVO_SCORE = 0.9
    @JvmField var PIVOT_SERVO_SPEC = 0.2
    @JvmField var PIVOT_SERVO_TRANSFER = 0.0
    @JvmField var PIVOT_SERVO_SAFE = 0.4


    @JvmField var SLIDE_MOTOR_SPEED = 0.7

    /**
     * Values for stage 1 of the outtake
     */
    @JvmField var OUT_STRIKE_R_HOME = 0.05
    @JvmField var OUT_STRIKE_L_HOME = 0.05
    @JvmField var OUT_STRIKE_R_SCORE = 0.6
    @JvmField var OUT_STRIKE_L_SCORE = 0.6
    @JvmField var OUT_STRIKE_R_TRANSFER = 0.32
    @JvmField var OUT_STRIKE_L_TRANSFER = 0.32
    @JvmField var OUT_STRIKE_R_SPEC = 0.85
    @JvmField var OUT_STRIKE_L_SPEC = 0.85


    /**
     * Values for stage 2 of the outtake. Change for tuning.
     * */
    @JvmField var SWING_SERVO_TARGET = 0.0
    @JvmField var SWING_SERVO_HOME = 0.95
    @JvmField var SWING_SERVO_INIT = 0.75

    /**
     * Wrist Positions
     */
    @JvmField var WRIST_SERVO_HOME = 0.3
    @JvmField var WRIST_SERVO_TARGET = 0.97

    /**
     * Claw Positions.
     */
    @JvmField var UNGRIPPY = 0.3
    @JvmField var GRIPPY = 0.8
}