package org.firstinspires.ftc.teamcode.implementation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.helpers.LogState;
import org.jetbrains.annotations.NotNull;


/**
 * ClimbSystem is a subsystem that manages the climbing mechanism of the robot.
 * It controls the climb motors and provides methods to set target positions for climbing.
 * The system uses PID control to adjust the motor power based on the current position of the climb slides.
 */
@Config
public class ClimbSystem extends SubsystemBase implements LogState {
    private final RootSystem root;

    @Override
    @NotNull
    public String stateString() {
        return String.format("CLIMBSYSTEM TargetPosition: %s Position: %s", targetPosition, position.name());
    }

    /**
     * Enum representing the different states of the climb system.
     * Each state corresponds to a specific target position for the climb slides.
     */
    public enum ClimbState {
        HOME(0),
        LOW_CHAMBER(100),
        LOW_BASKET(1100),
        HIGH_CHAMBER(420),
        HIGH_CHAMBER_INVERTED(1350),
        HIGH_BASKET(2200);

        public final double position;

        ClimbState(double position) {
            this.position = position;
        }
    }

    // PID coefficients for the climb slides.
    public static PIDFCoefficients PID_SLIDES = new PIDFCoefficients(0.007, 0.00, 0.0001, 0.5);

    // Motors for the climbing mechanism.
    private final DcMotorEx climbMotor1, climbMotor2, climbMotor3;

    // Target position for the climb slides, initialized to the HOME position.
    public static double targetPosition = ClimbState.HOME.position, lastError;
    public ClimbState position = ClimbState.HOME;

    // Timer for tracking elapsed time during PID calculations.
    private final ElapsedTime timer = new ElapsedTime();

    public ClimbSystem(RootSystem root, boolean isAuto) {
        this.root = root;
        this.climbMotor1 = root.getHw().get(DcMotorEx.class, "climbMotorUp");
        this.climbMotor2 = root.getHw().get(DcMotorEx.class, "climbMotorDown");
        this.climbMotor3 = root.getHw().get(DcMotorEx.class, "climbMotor3");

        if(isAuto) {
            resetEncoder();
        }

        climbMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        climbMotor3.setDirection(DcMotorSimple.Direction.FORWARD);
        climbMotor2.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    /** Resets the encoder of climbMotor2 and sets it to run without an encoder.
     * This is typically used to reset the position of the climb slides.
     */
    public void resetEncoder() {
        climbMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        climbMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /** Gets the current position of the climb slides in degrees.
     * The position is calculated based on the encoder value of climbMotor2.
     * @return The current position of the climb slides in degrees.
     */
    public double getSlidesPosition() {
        return (Math.max((climbMotor2.getCurrentPosition() * -1), 0) / 8192.0 * 360);
    }


    @Override
    public void periodic() {
        double error = targetPosition - this.getSlidesPosition();
        double derivative = (error - lastError) / timer.seconds();

        // sum everything up
        double PID_output = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative) + PID_SLIDES.f;

// Debug information
//        Log.i("Climb", String.valueOf(this.getSlidesPosition()));
//        Log.i("Climb", String.valueOf(position));

        root.getTelemetry().addData("Slide Position", this.getSlidesPosition());

        //Make sure to stop PIDing when we're home
        if(position == ClimbState.HOME && this.getSlidesPosition() < 75){
            climbMotor1.setPower(0);
            climbMotor2.setPower(0);
            climbMotor3.setPower(0);
        }else if (position == null){
            //Don't pid
        }
        else{
            climbMotor1.setPower(PID_output);
            climbMotor2.setPower(PID_output);
            climbMotor3.setPower(PID_output);
        }

        lastError = error;
        timer.reset();
    }

    /** Sets the target position for the climb motors.
     * @param climbState The desired climb state to set the target position to.
     * @return A command that sets the target position of the climb motors.
     */
    public Command setTargetPosition(ClimbState climbState) {
        return new InstantCommand(() -> {
            this.position = climbState;
            targetPosition = climbState.position;
        });
    }

    /** Sends the climb motors to a specific speed, ignoring the position.
     * @param speed The speed to set the motors to.
     * @return A command that sets the motors to the specified speed.
     */
    public Command sendRawMotors(double speed){
        return new InstantCommand(() -> {
            position = null;
            climbMotor1.setPower(speed);
            climbMotor2.setPower(speed);
            climbMotor3.setPower(speed);
        });
    }
}