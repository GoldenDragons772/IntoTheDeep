package org.firstinspires.ftc.teamcode.implementation;

import android.util.Log;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;


@Config
public class ClimbSystem extends SubsystemBase {
    private final RootSystem root;

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

    public static PIDFCoefficients PID_SLIDES = new PIDFCoefficients(0.007, 0.00, 0.0001, 0.05);

    private final DcMotorEx climbMotor1, climbMotor2, climbMotor3;


    public static double targetPosition = ClimbState.HOME.position, lastError;
    public ClimbState position = ClimbState.HOME;
    private final ElapsedTime timer = new ElapsedTime();
    private int initialPosition = 0;

    public ClimbSystem(RootSystem root, boolean isAuto) {
        this.root = root;


        this.climbMotor1 = root.getHw().get(DcMotorEx.class, "climbMotorUp");
        this.climbMotor2 = root.getHw().get(DcMotorEx.class, "climbMotorDown");
        this.climbMotor3 = root.getHw().get(DcMotorEx.class, "climbMotor3");

        if(isAuto) {
            climbMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            climbMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        climbMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        climbMotor3.setDirection(DcMotorSimple.Direction.FORWARD);
        climbMotor2.setDirection(DcMotorSimple.Direction.FORWARD);

        initialPosition = climbMotor2.getCurrentPosition();
    }

//    public void resetEncoders() {
//        climbMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        climbMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//    }

    // Get the current position from the slide
    public double getSlidesPosition() {
        return (Math.max((climbMotor2.getCurrentPosition() * -1) - initialPosition, 0) / 8192.0 * 360);
    }

    @Override
    public void periodic() {
        double error = targetPosition - this.getSlidesPosition();
        double derivative = (error - lastError) / timer.seconds();

        // sum everything up
        double PID_output = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative) + PID_SLIDES.f;

//        Log.i("Climb", String.valueOf(this.getSlidesPosition()));
//        Log.i("Climb", String.valueOf(position));

        root.getTelemetry().addData("Slide Position", this.getSlidesPosition());

        //Make sure to stop PIDing when we're home
        if(position == ClimbState.HOME && this.getSlidesPosition() < 75){
            climbMotor1.setPower(0);
            climbMotor2.setPower(0);
            climbMotor3.setPower(0);

//            climbMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            climbMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        }else if(position == null){
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

    public Command setTargetPosition(ClimbState climbState) {
        return new InstantCommand(() -> {
            this.position = climbState;
            targetPosition = climbState.position;
        });
    }

    public Command sendRawMotors(double speed){
        return new InstantCommand(() -> {
            position = null;
            climbMotor1.setPower(speed);
            climbMotor2.setPower(speed);
            climbMotor3.setPower(speed);
        });
    }
}
