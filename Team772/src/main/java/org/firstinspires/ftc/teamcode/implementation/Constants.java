package org.firstinspires.ftc.teamcode.implementation;

import com.acmerobotics.dashboard.config.Config;
@Config
public class Constants {

         public static double NOMINAL_BATTERY_VOLTAGE = 13.5;


        /**
         * PedroPathing Constants (So I don't go insane
         */
         public static double BLUE_CHAMBER_XDISTANCE = 35.000;
         public static double BLUE_SPEC_PICKUP_XDISTANCE = 15.000;

        // Claw Positions
         public static double CLAW_SERVO_HOME = 0.3;
         public static double CLAW_SERVO_TARGET = 0.04;
         public static double GOOFY_AHH_SERVO_OFFSET = 0.66;
         // The conversion ratio from length in the camera length in the real world. Probably not the best.
         public static double GOOFY_AHH_VERTICAL_SEGMENT = 0.001;

        // Pivot Positions (Joint 2)
         static double PIVOT_SERVO_HOME = 0.53;
         public static double PIVOT_SERVO_SCORE = 0.5;
         public static double PIVOT_SERVO_SPEC = 0.95;
         public static double PIVOT_SERVO_TRANSFER = 1.0;
         public static double PIVOT_SERVO_SAFE = 0.0;


        /**
         * Values for stage 1 of the outtake
         */
         public static double OUT_STRIKE_R_HOME = 0.83;
         public static double OUT_STRIKE_L_HOME = 0.83;
         public static double OUT_STRIKE_R_SCORE = 0.3;
         public static double OUT_STRIKE_L_SCORE = 0.3;
         public static double OUT_STRIKE_R_TRANSFER = 0.52;
         public static double OUT_STRIKE_L_TRANSFER = 0.52;
         public static double OUT_STRIKE_R_SPEC = 0.05;
         public static double OUT_STRIKE_L_SPEC = 0.05;
         public static double OUT_STRIKE_R_SAFE = 0.8;
         public static double OUT_STRIKE_L_SAFE = 0.8;



        /**
         * Values for stage 2 of the outtake. Change for tuning.
         * */
         public static double SWING_SERVO_TARGET = 0.0;
         public static double SWING_SERVO_HOME = 0.95;
         public static double SWING_SERVO_INIT = 0.75;

        /**
         * Wrist Positions
         */
         public static double WRIST_SERVO_HOME = 0.265;
         public static double WRIST_SERVO_TARGET = 0.935;

        /**
         * Claw Positions.
         */
         public static double UNGRIPPY = 0.3;
         public static double GRIPPY = 0.8;

        /**
         * Vision
         */
         public static double VISION_MIN_AREA = 30000;
         public static double MIN_RED_SAMPLE_HUE = 170;
         public static double MAX_RED_SAMPLE_HUE = 180;
}
