package org.firstinspires.ftc.team772.implementation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import kotlin.NotImplementedError;

/**
 * Controlling class for the components of the Intake.
 */
@Config
public class IntakeSubsystem {

    // Set Positions for Linkage
    public static double LEFT_LINKAGE_HOME = 0, LEFT_LINKAGE_TARGET = 0.45, LEFT_LINKAGE_TRANSFER = 0.5;
    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_TRANSFER = 0.5;

    // Set Positions for Strike Servos
    public static double LEFT_PIVOT_HOME = 1.0, LEFT_PIVOT_TARGET = 0.25, LEFT_PIVOT_TRANSFER = 0.5;
    public static double RIGHT_PIVOT_HOME = 1.0, RIGHT_PIVOT_TARGET = 0.25, RIGHT_PIVOT_TRANSFER = 0.5;

    // Set Positions for main pivot
    public static double PIVOT_HOME = 0.0, PIVOT_TARGET = 0.5, PIVOT_TRANSFER = 0.5;

    // Set Positions for Wrist
    public static double WRIST_HOME = 0.67, WRIST_TARGET = 0.32;

    // Set Positions for claw
    public static double CLAW_HOME = 0.2, CLAW_TARGET = 0.46, CLAW_STROKE = 0.5;

    //State stuff
    boolean pivotState = false;
    boolean clawState = false;
    boolean wristState = false;

    // positions

    /**
     * Enum of positions to pass into intake functions.
     */
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

        moveToHome();
        setClaw(IntakePosition.HOME);

    }

    /**
     * Sets the linkage to an IntakePosition.
     * @param pos the intake position
     * @return a command to be executed later.
     */
    public Command setLinkage(IntakePosition pos) {
        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                    leftLinkageServo.setPosition(LEFT_LINKAGE_HOME);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_HOME);
                });
            case TARGET -> new InstantCommand(() -> {
                    leftLinkageServo.setPosition(LEFT_LINKAGE_TARGET);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_TARGET);
                });
            case TRANSFER -> new InstantCommand(() -> {
                    leftLinkageServo.setPosition(LEFT_LINKAGE_TRANSFER);
                    rightLinkageServo.setPosition(RIGHT_LINKAGE_TRANSFER);
                });
        };
    }

    /**
     * Sets the strike servos to an IntakePosition.
     * @param pos an IntakePosition.
     * @return A command to be executed later.
     */
    public Command setStrike(IntakePosition pos) {

        return switch (pos) {
            case HOME -> {
                pivotState = true;
                yield new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);
                });
            }
            case TARGET -> {
                pivotState = false;
                yield new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_TARGET);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_TARGET);
                });
            }
            case TRANSFER -> new InstantCommand(() -> {
                    leftStrikeServo.setPosition(LEFT_PIVOT_TRANSFER);
                    rightStrikeServo.setPosition(RIGHT_PIVOT_TRANSFER);
                });
        };

    }

    /**
     * Sets the pivot servos to an IntakePosition.
     * @param pos an IntakePosition.
     * @return A command to be executed later.
     */
    public Command setPivot(IntakePosition pos) {
        return switch (pos) {
            case HOME -> {
                pivotState = true;
                yield new InstantCommand(() -> pivotServo.setPosition(PIVOT_HOME));
            }
            case TARGET -> {
                pivotState = false;
                yield new InstantCommand(() -> pivotServo.setPosition(PIVOT_TARGET));
            }
            case TRANSFER -> new InstantCommand(() -> pivotServo.setPosition(PIVOT_TRANSFER));
        };

    }

    /**
     * Sets the wrist servo to the IntakePosition.
     * @param pos an IntakePosition.
     * @return A command to be executed later.
     */
    public Command setWrist(IntakePosition pos) {
        return switch (pos) {
            case HOME -> {
                wristState = false;
                yield new InstantCommand(() -> wristServo.setPosition(WRIST_HOME));
            }
            case TARGET -> {
                wristState = true;
                yield new InstantCommand(() -> wristServo.setPosition(WRIST_TARGET));
            }
            case TRANSFER -> throw new NotImplementedError("TRANSFER is unimplemented for setWrist()"); // TODO: Create a case for transfer wrist position.
        };
    }

    /**
     * Sets the claw servo to an IntakePosition.
     * @param pos an IntakePosition.
     * @return A command to be executed later.
     */
    public Command setClaw(IntakePosition pos) {
        return switch (pos) {
            case HOME -> {
                clawState = false;
                yield new InstantCommand(() -> clawServo.setPosition(CLAW_HOME));
            }
            case TARGET -> {
                clawState = true;
                yield new InstantCommand(() -> clawServo.setPosition(CLAW_TARGET));
            }
            case TRANSFER -> new InstantCommand(() -> clawServo.setPosition(CLAW_STROKE));
        };

    }

    /**
     * Moves the intake system to the home position.
     * @return A command to be executed later.
     */
    public Command moveToHome() {
        return new SequentialCommandGroup(
                setWrist(IntakePosition.HOME),
                setLinkage(IntakePosition.HOME),
                setStrike(IntakePosition.HOME),
                setPivot(IntakePosition.HOME)
        );
    }

    /**
     * Moves the intake system to the transfer position.
     * @return A command to be executed later.
     */
    public Command moveToTransfer() {
        return new SequentialCommandGroup(
                setWrist(IntakePosition.HOME),
                setLinkage(IntakePosition.HOME),
                setStrike(IntakePosition.TRANSFER),
                setPivot(IntakePosition.TRANSFER)
        );
    }

    /**
     * Move the intake system to the target position.
     * @return A command to be executed later.
     */
    public Command moveToTarget() {
        return new SequentialCommandGroup(
                setLinkage(IntakePosition.TARGET),
                setClaw(IntakePosition.HOME),
                setStrike(IntakePosition.TARGET),
                setPivot(IntakePosition.TARGET)
        );
    }

    /**
     * Toggle the intake between the home and target positions.
     * @return A command to be executed later.
     */
    public ConditionalCommand toggleIntake() {
        return new ConditionalCommand(
                moveToHome(),
                moveToTarget(),
                () -> {
                    pivotState = !pivotState;
                    return pivotState;
                }
        );
    }

    /**
     * Toggles the claw between the closed and open positions.
     * @return A command to be executed later.
     */
    public ConditionalCommand toggleClaw() {
        return new ConditionalCommand(
                setClaw(IntakePosition.HOME),
                setClaw(IntakePosition.TARGET),
                () -> {
                    clawState = !clawState;
                    return clawState;
                }
        );
    }

    /**
     * Toggles the wrist between the home and target positions.
     * @return A command to be executed later.
     */
    public ConditionalCommand toggleWrist() {
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
