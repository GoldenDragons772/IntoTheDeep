package org.firstinspires.ftc.team772.implementation;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class ClimbSystem extends SubsystemBase {

    public enum ClimbState {
        HOME(0),
        LOW_CHAMBER(100),
        LOW_BASKET(1100),
        HIGH_CHAMBER(450),
        HIGH_BASKET(2200),
        SPEC_HANG(750);

        public final double position;

        ClimbState(double position) {
            this.position = position;
        }
    }

    public static PIDFCoefficients PID_SLIDES = new PIDFCoefficients(0.03, 0, 0.00003, 0.05);
    private final DcMotor climbMotor1, climbMotor2;

    public static double targetPosition = ClimbState.HOME.position, lastError;
    private final ElapsedTime timer = new ElapsedTime();

    public ClimbSystem(HardwareMap hw) {
        climbMotor1 = hw.get(DcMotor.class, "climbMotor1");
        climbMotor2 = hw.get(DcMotor.class, "climbMotor2");

        climbMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        climbMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        climbMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // Get the current position from the slide
    public double getSlidesPosition() {
        return Math.max(climbMotor1.getCurrentPosition(), 0) / 8192.0 * 360;
        //return climbMotor1.getCurrentPosition();
    }

    @Override
    public void periodic() {

        double error = targetPosition - this.getSlidesPosition();
        double derivative = (error - lastError) / timer.seconds();

        // sum everything up
        double PID_output = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative);

        Log.i("Climb", String.valueOf(PID_output));

        climbMotor1.setPower(PID_output);
        climbMotor2.setPower(PID_output);

        lastError = error;
        timer.reset();
    }

    public Command setTargetPosition(ClimbState climbState) {

        return new InstantCommand(() -> {this.targetPosition = climbState.position;});
    }

}
