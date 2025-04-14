package org.firstinspires.ftc.teamcode.implementation.commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem;
import org.opencv.video.FarnebackOpticalFlow;

@Config
public class AlignWithSample extends CommandBase {

    Follower follower;
    IntakeSystem intake;

    private static double lastError = 0;
    private final ElapsedTime timer = new ElapsedTime();
    public static PIDFCoefficients pidfCoefficients = new PIDFCoefficients(0, 0, 0, 0);

    Telemetry telemetry;


    public AlignWithSample(Follower follower, IntakeSystem intake){
        this.follower = follower;
        this.intake = intake;
    }

    @Override
    public void initialize() {
        //follower.breakFollowing();
        telemetry = FtcDashboard.getInstance().getTelemetry();

        follower.startTeleopDrive();

        addRequirements(intake);
    }

    @Override
    public void execute() {

        if(intake.sampleDetector.centroid != null) {
            double error = (intake.sampleDetector.centroid.x - (640 / 2));
            double derivative = (error - lastError) / timer.seconds();

            double PID_output = (pidfCoefficients.p * error) + (pidfCoefficients.d  * derivative);

            telemetry.addData("PID", PID_output);
            telemetry.update();

            lastError = error;
            timer.reset();
        }

        follower.setTeleOpMovementVectors(1, 0, 0);

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
