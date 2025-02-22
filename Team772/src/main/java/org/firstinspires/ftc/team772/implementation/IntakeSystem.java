package org.firstinspires.ftc.team772.implementation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SelectCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

@Config
public class IntakeSystem extends SubsystemBase {

    // Set Positions for Linkage
    public static double LEFT_LINKAGE_HOME = 0, LEFT_LINKAGE_TARGET = 0.46, LEFT_LINKAGE_TRANSFER = 0.5;
    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_TRANSFER = 0.5;

    // Set Positions for Strike Servos
    public static double LEFT_PIVOT_HOME = 1.0, LEFT_PIVOT_TARGET = 0.08, LEFT_PIVOT_TRANSFER = 0.24;
    public static double RIGHT_PIVOT_HOME = 1.0, RIGHT_PIVOT_TARGET = 0.08, RIGHT_PIVOT_TRANSFER = 0.205;

    static WristPosition wristState = WristPosition.HOME;
    // Set Positions for main pivot
    public static double PIVOT_HOME = 0.5, PIVOT_TARGET = 0.05, PIVOT_TRANSFER = 0.9;

    // Set Positions for Wrist
    public static double WRIST_HOME = 0.67, WRIST_TARGET = 0.32, WRIST_ANGLE = 0.495;

    // Set Positions for claw
    public static double CLAW_HOME = 0.15, CLAW_TARGET = 0.445, CLAW_STROKE = 0.5;

    //State stuff
    static IntakePosition extendState = IntakePosition.HOME;
    static boolean clawState = false;

    // positions
    public enum IntakePosition {
        HOME,
        TARGET,
        TRANSFER
    }

    public enum WristPosition {
        HOME,
        TARGET,
        ANGLE
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

    public IntakeSystem(HardwareMap hw) {
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

        // Set Default positions:
        leftLinkageServo.setPosition(LEFT_LINKAGE_HOME);
        rightLinkageServo.setPosition(RIGHT_LINKAGE_HOME);
        clawServo.setPosition(CLAW_HOME);
        leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
        rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);
        pivotServo.setPosition(PIVOT_HOME);
        wristServo.setPosition(WRIST_HOME);

    }

    public WristPosition getWristPos(){
        return wristState;
    }

    public IntakePosition getIntakePos(){
        return extendState;
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
            case TRANSFER -> { //Never gets called
                    leftLinkageServo.setPosition(LEFT_LINKAGE_TRANSFER);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_TRANSFER);
            }
       }

        return null; // return null if nothing works out
    }

    public Command setStrike(IntakePosition pos)  {

        switch (pos) {
            case HOME -> {
                return new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);
                });
            }
            case TARGET -> {
                return new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_TARGET);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_TARGET);
                });
            }
            case TRANSFER -> {
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
                return new InstantCommand(() ->{
                    pivotServo.setPosition(PIVOT_HOME);
                });
            }
            case TARGET -> {
                return new InstantCommand(() ->{
                    pivotServo.setPosition(PIVOT_TARGET);
                });
            }
            case TRANSFER -> {
                return new InstantCommand(() ->{
                    pivotServo.setPosition(PIVOT_TRANSFER);
                });
            }
        }

        return null;
    }

    public Command setWrist(WristPosition pos){
        switch (pos){
            case HOME -> {
                return new InstantCommand(() ->{
                    wristServo.setPosition(WRIST_HOME);
                    wristState = WristPosition.HOME;
                });
            }
            case TARGET -> {
                return new InstantCommand(() ->{
                    wristState = WristPosition.TARGET;
                    wristServo.setPosition(WRIST_TARGET);
                });
            }
            case ANGLE -> {
                return new InstantCommand(() ->{
                    wristState = WristPosition.ANGLE;
                    wristServo.setPosition(WRIST_ANGLE);
                });
            }
        }

        return null;
    }

    public Command setClaw(IntakePosition pos){
        switch (pos){
            case HOME -> {
                return new InstantCommand(() ->{
                    clawState = false;
                    clawServo.setPosition(CLAW_HOME);
                });
            }
            case TARGET -> {
                return new InstantCommand(() ->{
                    clawState = true;
                    clawServo.setPosition(CLAW_TARGET);
                });
            }
            case TRANSFER -> {
                return new InstantCommand(() ->{
                    clawState = false;
                    clawServo.setPosition(CLAW_STROKE);
                });
            }
        }

        return null;
    }

    public Command moveToHome() {
        return new SequentialCommandGroup(
            new InstantCommand(() -> {extendState = IntakePosition.HOME;}),
            setWrist(WristPosition.HOME),
            setLinkage(IntakePosition.HOME),
            setStrike(IntakePosition.HOME),
            setPivot(IntakePosition.HOME)
        );
    }

    public Command moveToTransfer() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {extendState = IntakePosition.TRANSFER;}),
                setWrist(WristPosition.HOME),
                setLinkage(IntakePosition.HOME),
                setStrike(IntakePosition.TRANSFER),
                setPivot(IntakePosition.TRANSFER)
        );
    }

    public Command moveToTarget(){
        return new SequentialCommandGroup(
                new InstantCommand(() -> {extendState = IntakePosition.TARGET;}),
                setLinkage(IntakePosition.TARGET),
                setClaw(IntakePosition.HOME),
                setStrike(IntakePosition.TARGET),
                setPivot(IntakePosition.TARGET)
        );
    }

    /*
    public ConditionalCommand toggleIntake(){
        return new ConditionalCommand(
                moveToTransfer(),
                moveToTarget(),
                () -> {
                    return extendState; // Return original val
                }
        );
    }
     */

    public Command toggleIntake(){
        return new SelectCommand(
                new HashMap<>() {{
                    put(IntakePosition.TRANSFER, moveToTarget());
                    put(IntakePosition.TARGET, moveToTransfer());
                }},
                this::getIntakePos
        );
    }

    public ConditionalCommand toggleClaw(){
        return new ConditionalCommand(
                setClaw(IntakePosition.HOME),
                setClaw(IntakePosition.TARGET),
                () -> {
                    clawState = !clawState;
                    return !clawState; // Return original val
                }
        );
    }

    public Command toggleWrist(){
        return new SelectCommand(
                new HashMap<>() {{
                    put(WristPosition.HOME, setWrist(WristPosition.ANGLE));
                    put(WristPosition.ANGLE, setWrist(WristPosition.TARGET)); //Each state must trigger the next one
                    put(WristPosition.TARGET, setWrist(WristPosition.HOME));
                }},
                this::getWristPos
        );//.andThen(new InstantCommand({{Log.i("IntakeSystem", wristState.toString())}}));
    }

//    public Command setPivotPosition(IntakePosition pos) {
//
//    }
}
