package org.firstinspires.ftc.team772.implementation

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.HardwareMap

@Config
class Constants {
    companion object {
        // Example values
        const val FUN_NUMBER = 1

        // value that points to currently used implementation's constructor function.

        /*
            ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            CHANGE THIS FOR OTHER DRIVETRAIN
            ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
         */
        val CURRENT_IMPLEMENTATION = fun(hw: HardwareMap): ParallelPlateDrivesystem {return ParallelPlateDrivesystem(hw) }

        //// Pure Pursuit config values
        // The initial value to use as the lookahead.
        const val LOOKAHEAD = 0.5

        // The maximum value to use as a lookahead.
        const val MAX_LOOKAHEAD = 1

        // The search step to increase the lookahead by if no intersection is found.
        const val LOOKAHEAD_STEP = 0.1

        // The distance to the end of the path that the robot needs to be in order to consider itself finished.
        const val MIN_GOAL_DISTANCE = 1

        /**
         * Values for the intake. Change these for tuning. (Must supply a value between 0.0 and 1.0)
         * */
        const val SLIDE_SERVO_HOME = 0.0
        const val PIVOT_SERVO_HOME = 0.0
        const val CLAW_SERVO_HOME = 0.0

        const val SLIDE_SERVO_TARGET = 0.4
        const val PIVOT_SERVO_TARGET = 1.0
        const val CLAW_SERVO_TARGET = 0.0

    }
}