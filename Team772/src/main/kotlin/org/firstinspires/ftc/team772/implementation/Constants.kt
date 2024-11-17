package org.firstinspires.ftc.team772.implementation

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap

@Config
class Constants {
    companion object {
        //// Pure Pursuit config values
        // The initial value to use as the lookahead.
        @JvmField
        val LOOKAHEAD = 0.5

        // The maximum value to use as a lookahead.
        @JvmField
        val MAX_LOOKAHEAD = 1

        // The search step to increase the lookahead by if no intersection is found.
        @JvmField
        val LOOKAHEAD_STEP = 0.1

        // The distance to the end of the path that the robot needs to be in order to consider itself finished.
        @JvmField
        val MIN_GOAL_DISTANCE = 1

        /**
         * Values for arm.
         */
        @JvmField
        val ARM_HOME = 0
        @JvmField
        val ARM_HIGH_CLIMB = 2700
        @JvmField
        val ARM_LOW_CLIMB = 1350

        /**
         * Values for the intake. Change these for tuning. (Must supply a value between 0.0 and 1.0)
         * */
        @JvmField
        val SLIDE_HOME = 75 //70 works well
        @JvmField
        val SLIDE_EDGE = 400
        @JvmField
        val SLIDE_SERVO_HOME = 0.1
        @JvmField
        val PIVOT_SERVO_HOME = 0.47//TODO: Swap variable names
        @JvmField
        val CLAW_SERVO_HOME = 0.0

        @JvmField
        val SLIDE_TARGET = 1150
        @JvmField
        val SLIDE_SERVO_TARGET = 0.3
        @JvmField
        val PIVOT_SERVO_TARGET = 1.0
        @JvmField
        val CLAW_SERVO_TARGET = 0.0

        @JvmField
        val SLIDE_MOTOR_SPEED = 1.0

        /**
         * Values for the outtake. Change for tuning.
         * */
        @JvmField
        val SWING_SERVO_TARGET = 0.0
        @JvmField
        val WRIST_SERVO_HOME = 0.0

        /**
         * Claw home position.
         */
        @JvmField
        val UNGRIPPY = 0.6

        @JvmField
        val SWING_SERVO_HOME = 1.0
        @JvmField
        val WRIST_SERVO_TARGET = 0.25

        /**
         * Claw target position.
         */
        @JvmField
        val GRIPPY = 0.2

    }
}