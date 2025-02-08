package org.firstinspires.ftc.team772.implementation;

import android.os.UserManager;

import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeSubsystem {

    // Set Positions for Linkage
    public static double LEFT_LINKAGE_HOME = 0, LEFT_LINKAGE_TARGET = 1.0, LEFT_LINKAGE_TRANSFER = 0.5;
    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 1.0, RIGHT_LINKAGE_TRANSFER = 0.5;

    // Set Positions for Pivot
    public static double LEFT_PIVOT_HOME = 0, LEFT_PIVOT_TARGET = 1.0, LEFT_PIVOT_TRANSFER = 0.5;
    public static double RIGHT_PIVOT_HOME = 0, RIGHT_PIVOT_TARGET = 1.0, RIGHT_PIVOT_TRANSFER = 0.5;

    // positions
    public enum IntakePosition {
        HOME,
        TARGET,
        TRANSFER
    }

    // Linkage Servo
    Servo leftLinkageServo, rightLinkageServo;
    // Pivot Servo
    Servo leftPivotServo, rightPivotServo;

    public IntakeSubsystem(HardwareMap hw) {
        // Linkage Servo
        leftLinkageServo = hw.get(Servo.class, "lLinkageServo");
        rightLinkageServo = hw.get(Servo.class, "rLinkageServo");

        // pivot servo
        leftPivotServo = hw.get(Servo.class, "hLeftPivot");
        rightPivotServo = hw.get(Servo.class, "hRightPivot");

    }

    public Command setLinkage(IntakePosition pos)  {

        switch (pos) {
            case HOME -> {
                return new InstantCommand(() -> {
                    leftLinkageServo.setPosition(LEFT_LINKAGE_HOME);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_HOME);
                });
            }
            case TARGET -> {
                return new InstantCommand(() -> {
                    leftLinkageServo.setPosition(LEFT_LINKAGE_TARGET);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_TARGET);
                });
            }
            case TRANSFER -> {
                    leftLinkageServo.setPosition(LEFT_LINKAGE_TRANSFER);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_TRANSFER);
            }
       }

        return null; // return null if nothing works out
    }

    public Command setPivot(IntakePosition pos)  {

        switch (pos) {
            case HOME -> {
                return new InstantCommand(() -> {
                    leftPivotServo.setPosition(LEFT_PIVOT_HOME);
                    rightPivotServo.setPosition(RIGHT_PIVOT_HOME);
                });
            }
            case TARGET -> {
                return new InstantCommand(() -> {
                    leftPivotServo.setPosition(LEFT_PIVOT_TARGET);
                    rightPivotServo.setPosition(RIGHT_PIVOT_TARGET);
                });
            }
            case TRANSFER -> {
                return new InstantCommand(() -> {
                    leftPivotServo.setPosition(LEFT_PIVOT_TRANSFER);
                    rightPivotServo.setPosition(RIGHT_PIVOT_TRANSFER);
                });
            }
        }

        return null; // return null if nothing works out
    }

//    public Command setPivotPosition(IntakePosition pos) {
//
//    }
}
