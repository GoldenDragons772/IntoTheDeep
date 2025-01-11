package org.firstinspires.ftc.team772.opmodes;

import android.util.Log;
import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.sun.tools.javac.comp.Todo;

import org.firstinspires.ftc.team772.implementation.ClimbSystem;
import org.firstinspires.ftc.team772.implementation.IntakeSystem;
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.roadrunner.MecanumDrive;

import java.util.function.ToDoubleBiFunction;

@Autonomous( name = "Auto")
public class BlueAuto extends LinearOpMode { //Main class, Why does it extend LinearOmMode?

    /**
     * Main class that handles pathing and scoring in auto
     */
    public class scoreSomething {

        //Create objects for the robot subsystems
        OuttakeSystem outtakeSystem;
        IntakeSystem intakeSystem;
        ClimbSystem climbSystem;

        /**
         * Primary constructor for initializing the class and substructures.
         */
        public scoreSomething() {
            outtakeSystem = new OuttakeSystem(hardwareMap);
            intakeSystem = new IntakeSystem(hardwareMap);
            climbSystem = new ClimbSystem(hardwareMap);
        }

        /**
         * This scores a sample/specimen
         * @return Completed score action
         */
        public Action scoreSpecimen() {
            // A telemetry packet is returned to function with the Action scheduler
            return telemetryPacket -> {
                ParallelCommandGroup score = new ParallelCommandGroup(); //Use parallel command group to run all tasks at once
                score.addCommands(
                        outtakeSystem.unGrip()
                );
                score.schedule(); //Runs the score command
                return false; //Return false so that the action runs once!
            };
        }

        /**
         * Function that runs at the beginning of auto and initializes a preload
         * @return Initialized preload action
         */
        public Action initPreload(){
            return telemetryPacket -> {

                ParallelCommandGroup commandGroup = new ParallelCommandGroup();

                commandGroup.addCommands(
                    outtakeSystem.gripIt(),
                    outtakeSystem.swingToTarget(),
                    climbSystem.setArmToPos(ClimbSystem.ArmPos.HIGHCLIMB)
                );

                commandGroup.schedule();

                Log.i("ROBO", "Initialized"); //Debug Stuff
                return false;
            };
        }

        /**
         * Runs the climb extensions and outtake arm to a safe position after the match is done.
         * @return Completed Action
         */
        public Action setArmtoSafePos() {
            return telemetryPacket -> {

                SequentialCommandGroup commandGroup = new SequentialCommandGroup();

                commandGroup.addCommands(
                            climbSystem.setArmToPos(ClimbSystem.ArmPos.HOME),
                            outtakeSystem.swingToHome()
                );
                commandGroup.schedule();
                return false;
            };
        }

        /**
         * Function that runs last to prevent the autonomous program from ending before all tasks are done.
         * @return Nothing (It runs forever)
         */
        public Action finish() {
            return telemetryPacket -> {
                return true; //Return true runs forever (last command)
            };
        }

        /**
         * Function that Sucks a sample into the robot (Incomplete!)
         * @return Finished action
         */
        public Action suckSample() {
            return telemetryPacket -> {

                //One day code will inhabit this space

                return true;
            };
        }
    }

    /**
     * Main function that contains the main code that runs when the autonomous program is started.
     * @throws InterruptedException Catches an interruped exception if another part of the robot cuts it off (Ugh).
     */
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPosition = new Pose2d(0, 63, Math.toRadians(180)); //Creates the initial position for where the robit starts in auto (In the middle up against the wall with the grippers facing the loading zone)
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPosition); //Creates a mechanum drive object for RoadRunner using our initial position
        scoreSomething scoreSpecimen = new scoreSomething(); //Creates an object of our interface class. (Maybe change the name to something that makes more senes?)


        //This RoadRunner command will drive the robot to the bucket
        TrajectoryActionBuilder goToBucket = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                .strafeTo(new Vector2d(85, 63))
                .strafeToLinearHeading(new Vector2d(85, 50), Math.toRadians(225))
                .strafeTo(new Vector2d(100, 59));

        //This RoadRunner command will drive the robot a little bit a way from the bucket after scoring.
        TrajectoryActionBuilder park = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                .strafeTo(new Vector2d(-33 * 1.7, 33 * 1.7));

        //This might send the robot to the first sample.
        //TrajectoryActionBuilder goToFirstSample = drive.actionBuilder(new Pose2d(100, 59, Math.toRadians(225)))

        waitForStart();

        //This is where roadrunner runs the actions that are created
        Actions.runBlocking(
            new SequentialAction( //They will be run in a sequential order.
                    scoreSpecimen.initPreload(), //Grab the preload and lift the arms up
                    goToBucket.build(), //Go to the bucket
                    scoreSpecimen.scoreSpecimen(), //Score the sample
                    park.build(), //Drive the robot back a lil bit.
                    scoreSpecimen.setArmtoSafePos(), //Send the arms back for teleop.
                    scoreSpecimen.finish() //We're done!
            )
        );
    }
}
