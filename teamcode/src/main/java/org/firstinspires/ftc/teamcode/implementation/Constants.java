package org.firstinspires.ftc.teamcode.implementation;

import com.acmerobotics.dashboard.config.Config;
import org.firstinspires.ftc.teamcode.vision.SampleDetection;

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

    // Camera stuff
    public static double GOOFY_AHH_SERVO_OFFSET = 0.66;
    public static double GOOFY_AHH_CAMERA_HST = 0.5; // horizontal space transformation
    public static double CAMERA_BOTTOM_OFFSET = 4.3; // inches
    public static double INCHES_PER_LINKAGE = 15.0 / IntakeSystem.LINKAGE_TARGET;
//    public static double INCHES_PER_CAMERA_X = 24.0 / SampleDetection.WIDTH; // Doesn't work because it's not a linear transformation
    public static double INCHES_PER_CAMERA_Y = 7.0 / SampleDetection.SUBHEIGHT;
    public static double LINKAGE_HOME_ANGLE = 78.0 * Math.PI/180; // radians
    public static double LINKAGE_TARGET_ANGLE = 13.0 * Math.PI/180; // radians
    public static double VISION_LONG_SEARCH_SPEED = 0.006;
    public static double VISION_LAT_SEARCH_SPEED = 3; // inches
    public static double VISION_MAX_HEIGHT = 50; // inches -- maximum height from the bottom of the screen to find samples in. (arbitrarily large for now)
    public static double LINKAGE_LENGTH = 240/25.4; // mm to inches
    public static double VISION_SERVO_MULTIPLIER = 0.66;

    // Pivot Positions (Joint 2)
    public static double PIVOT_SERVO_HOME = 0.55;
    public static double PIVOT_SERVO_SCORE = 0.6;
    public static double PIVOT_SERVO_SPEC = 0.9;
    public static double PIVOT_SERVO_SPEC_INV = 0.55;
    public static double PIVOT_SERVO_TRANSFER = 0.15;
    public static double PIVOT_SERVO_SAFE = 0.65;
    public static double PIVOT_SERVO_PRELOAD = 0.0;

    /**
     * Values for stage 1 of the outtake
     */

    public static double OUT_STRIKE_HOME = 0.77;
    public static double OUT_STRIKE_SCORE = 0.2;
    public static double OUT_STRIKE_TRANSFER_PREP = 0.42;
    public static double OUT_STRIKE_TRANSFER = 0.58;
    public static double OUT_STRIKE_SPEC = 0.06;
    public static double OUT_STRIKE_SPEC_INV = 0.0;
    public static double OUT_STRIKE_SAFE = 0.8;


    /**
     * Wrist Positions
     */
    public static double WRIST_SERVO_HOME = 0.685;
    public static double WRIST_SERVO_TARGET = 0.0;

    /**
     * Vision
     */
    public static double VISION_MIN_AREA = 3300;
    public static double MIN_RED_SAMPLE_HUE = 170;
    public static double MAX_RED_SAMPLE_HUE = 180;
}
