package org.firstinspires.ftc.team772.implementation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Config
public class ClimbSystem extends SubsystemBase {

    public enum CLIMB_STATE {
        HOME(0),
        LOW_CHAMBER(100),
        LOW_BASKET(150),
        HIGH_CHAMBER(200),
        HIGH_BASKET(250);

        public final double position;

        CLIMB_STATE(double position) {
            this.position = position;
        }
    }

    public static PIDFCoefficients PID_SLIDES = new PIDFCoefficients(0.005, 0, 0.00003, 0.05);
    private final DcMotor climbMotor1, climbMotor2;

    public static double targetPosition = CLIMB_STATE.HOME.position, lastError;
    private final ElapsedTime timer = new ElapsedTime();

    public ClimbSystem(HardwareMap hw) {
        climbMotor1 = hw.get(DcMotor.class, "climbMotor1");
        climbMotor2 = hw.get(DcMotor.class, "climbMotor2");

        climbMotor2.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    // Get the current position from the slide
    public double getSlidesPosition() {
        return Math.max(climbMotor1.getCurrentPosition(), 0) / 8192.0 * 360;
    }

    @Override
    public void periodic() {

        double error = targetPosition - this.getSlidesPosition();
        double derivative = (error - lastError) / timer.seconds();

        // sum everything up
        double PID_output = (PID_SLIDES.p * error) + (PID_SLIDES.d * derivative);

        climbMotor1.setPower(PID_output);
        climbMotor2.setPower(PID_output);

        this.lastError = error;
        timer.reset();
    }

    public Command setTargetPosition(CLIMB_STATE climbState) {

        this.targetPosition = climbState.position;

        return new InstantCommand(() -> {/* do nothing*/});
    }

}
