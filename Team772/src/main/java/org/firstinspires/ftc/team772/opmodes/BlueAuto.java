package org.firstinspires.ftc.team772.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous( name = "Auto")
public class BlueAuto extends LinearOpMode { //Main class, Why does it extend LinearOmMode?

    /**
     * Main function that contains the main code that runs when the autonomous program is started.
     * @throws InterruptedException Catches an interruped exception if another part of the robot cuts it off (Ugh).
     */
    @Override
    public void runOpMode() throws InterruptedException {


        //This might send the robot to the first sample.
        //TrajectoryActionBuilder goToFirstSample = drive.actionBuilder(new Pose2d(100, 59, Math.toRadians(225)))

        waitForStart();

        //This is where roadrunner runs the actions that are created
    }
}
