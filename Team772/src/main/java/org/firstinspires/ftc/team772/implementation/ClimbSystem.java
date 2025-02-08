package org.firstinspires.ftc.team772.implementation;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class ClimbSystem extends SubsystemBase {

    public static PIDFCoefficients PID_SLIDES = new PIDFCoefficients(0, 0, 0, 0);
    public static DcMotor climbMotor1, climbMotor2;

    private double targetPosition, lastError;
    private final ElapsedTime time = new ElapsedTime();

    public ClimbSystem(HardwareMap hw) {
        climbMotor1 = hw.get(DcMotor.class, "climbMotor1");
        climbMotor2 = hw.get(DcMotor.class, "climbMotor1");
    }

    // Get the current position from the slide
    public double getSlidesPosition() {
        return Math.max(climbMotor1.getCurrentPosition(), 0) / 8192.0 * 360;
    }

    @Override
    public void periodic() {

        double error = targetPosition - this.getSlidesPosition();
        double derivative = (error - lastError) / time.seconds();

        // sum everything up
//        double PID_output = Range.clip((PID_SLIDES.p * error) + (PID_SLIDES.d * derivative))

    }




}
