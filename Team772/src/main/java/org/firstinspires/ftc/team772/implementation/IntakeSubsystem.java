package org.firstinspires.ftc.team772.implementation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class IntakeSubsystem {

    // Set Positions for Linkage
    public static double LEFT_LINKAGE_HOME = 0, LEFT_LINKAGE_TARGET = 0.45, LEFT_LINKAGE_TRANSFER = 0.5;
    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_TRANSFER = 0.5;

    // Set Positions for Strike Servos
    public static double LEFT_PIVOT_HOME = 1.0, LEFT_PIVOT_TARGET = 0.45, LEFT_PIVOT_TRANSFER = 0.5;
    public static double RIGHT_PIVOT_HOME = 1.0, RIGHT_PIVOT_TARGET = 0.45, RIGHT_PIVOT_TRANSFER = 0.5;

    // Set Positions for main pivot
    public static double PIVOT_HOME = 0.0, PIVOT_TARGET = 0, PIVOT_TRANSFER = 1;

    // Set Positions for Wrist
    public static double WRIST_HOME = 0.67, WRIST_TARGET = 0.32;

    // Set Positions for claw
    public static double CLAW_HOME = 0.2, CLAW_TARGET = 0.46, CLAW_STROKE = 0.5;

    //State stuff
    boolean pivotState = false;
    boolean clawState = false;
    boolean wristState = false;

    // positions
    public enum IntakePosition {
        HOME,
        TARGET,
        TRANSFER
    }

    // Linkage Servo
    Servo leftLinkageServo, rightLinkageServo;
    // Strike Servo
    Servo leftStrikeServo, rightStrikeServo;
    // Pivot Servo
    Servo pivotServo;
    //Wrist Servo
    Servo wristServo;
    // Claw Servo
    Servo clawServo;

    public IntakeSubsystem(HardwareMap hw) {
        // Linkage Servo
        leftLinkageServo = hw.get(Servo.class, "lLinkageServo");
        rightLinkageServo = hw.get(Servo.class, "rLinkageServo");

        // strike servo
        leftStrikeServo = hw.get(Servo.class, "hLeftStrike");
        rightStrikeServo = hw.get(Servo.class, "hRightStrike");

        // Pivot Servo
        pivotServo = hw.get(Servo.class, "hPivot");

        //Wrist Servo
        wristServo = hw.get(Servo.class, "hSwivelServo");

        //Claw Servo
        clawServo = hw.get(Servo.class, "intakeClawServo");

        rightLinkageServo.setDirection(Servo.Direction.REVERSE);
        rightStrikeServo.setDirection(Servo.Direction.REVERSE);

//        pivotServo.setDirection(Servo.Direction.REVERSE);

        moveToHome();
        setClaw(IntakePosition.HOME);

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

    public Command setStrike(IntakePosition pos)  {

        switch (pos) {
            case HOME -> {
                pivotState = true;
                return new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);
                });
            }
            case TARGET -> {
                pivotState = false;
                return new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_TARGET);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_TARGET);
                });
            }
            case TRANSFER -> {
                pivotState = true;
                return new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_TRANSFER);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_TRANSFER);
                });
            }
        }

        return null; // return null if nothing works out
    }

    public Command setPivot(IntakePosition pos){
        switch (pos){
            case HOME -> {
                pivotState = true;
                return new InstantCommand(() ->{
                    pivotServo.setPosition(PIVOT_HOME);
                });
            }
            case TARGET -> {
                pivotState = false;
                return new InstantCommand(() ->{
                    pivotServo.setPosition(PIVOT_TARGET);
                });
            }
            case TRANSFER -> {
                pivotState = true;
                return new InstantCommand(() ->{
                    pivotServo.setPosition(PIVOT_TRANSFER);
                });
            }
        }

        return null;
    }

    public Command setWrist(IntakePosition pos){
        switch (pos){
            case HOME -> {
                wristState = false;
                return new InstantCommand(() ->{
                    wristServo.setPosition(WRIST_HOME);
                });
            }
            case TARGET -> {
                wristState = true;
                return new InstantCommand(() ->{
                    wristServo.setPosition(WRIST_TARGET);
                });
            }
        }

        return null;
    }

    public Command setClaw(IntakePosition pos){
        switch (pos){
            case HOME -> {
                clawState = false;
                return new InstantCommand(() ->{
                    clawServo.setPosition(CLAW_HOME);
                });
            }
            case TARGET -> {
                clawState = true;
                return new InstantCommand(() ->{
                    clawServo.setPosition(CLAW_TARGET);
                });
            }
            case TRANSFER -> {
                clawState = false;
                return new InstantCommand(() ->{
                    clawServo.setPosition(CLAW_STROKE);
                });
            }
        }

        return null;
    }

    public Command moveToHome() {
        return new SequentialCommandGroup(
            setWrist(IntakePosition.HOME),
            setLinkage(IntakePosition.HOME),
            setStrike(IntakePosition.HOME),
            setPivot(IntakePosition.HOME)
        );
    }

    public Command moveToTransfer() {
        return new SequentialCommandGroup(
                setWrist(IntakePosition.HOME),
                setLinkage(IntakePosition.HOME),
                setStrike(IntakePosition.TRANSFER),
                setPivot(IntakePosition.TRANSFER)
        );
    }

    public Command moveToTarget(){
        return new SequentialCommandGroup(
                setLinkage(IntakePosition.TARGET),
                setClaw(IntakePosition.HOME),
                setStrike(IntakePosition.TARGET),
                setPivot(IntakePosition.TARGET)
        );
    }

    public ConditionalCommand toggleIntake(){
        return new ConditionalCommand(
                moveToTransfer(),
                moveToTarget(),
                () -> {
                    pivotState = !pivotState;
                    return pivotState;
                }
        );
    }

    public ConditionalCommand toggleClaw(){
        return new ConditionalCommand(
                setClaw(IntakePosition.HOME),
                setClaw(IntakePosition.TARGET),
                () -> {
                    clawState = !clawState;
                    return clawState;
                }
        );
    }

    public ConditionalCommand toggleWrist(){
        return new ConditionalCommand(
                setWrist(IntakePosition.HOME),
                setWrist(IntakePosition.TARGET),
                () -> {
                    wristState = !wristState;
                    return wristState;
                }
        );
    }

//    public Command setPivotPosition(IntakePosition pos) {
//
//    }
}
