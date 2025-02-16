package org.firstinspires.ftc.team772.implementation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
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
        SPEC_HANG(400);

        public final double position;

        ClimbState(double position) {
            this.position = position;
        }
    }

    public static PIDFCoefficients PID_SLIDES = new PIDFCoefficients(0.03, 0, 0.00003, 0.05);

    private final DcMotorEx climbMotor1;
    private final DcMotorEx climbMotor2;

    public static double targetPosition = ClimbState.HOME.position, lastError;
    private final ElapsedTime timer = new ElapsedTime();

    public ClimbSystem(HardwareMap hw) {
        this.climbMotor1 = hw.get(DcMotorEx.class, "climbMotorUp");
        this.climbMotor2 = hw.get(DcMotorEx.class, "climbMotorDown");

        climbMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        //climbMotor2.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    // Get the current position from the slide
    public double getSlidesPosition() {
        return Math.max(climbMotor2.getCurrentPosition(), 0) / 8192.0 * 360;
    }

    @Override
    public void periodic() {

        double error = targetPosition - this.getSlidesPosition();
        double derivative = (error - lastError) / timer.seconds();

        // sum everything up
        double PID_output = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative);

//        Log.i("Climb", String.valueOf(PID_output));

        climbMotor1.setPower(PID_output);
        climbMotor2.setPower(PID_output);

        lastError = error;
        timer.reset();
    }

    public Command setTargetPosition(ClimbState climbState) {

        return new InstantCommand(() -> {this.targetPosition = climbState.position;});
    }

}
