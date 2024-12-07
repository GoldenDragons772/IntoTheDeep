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
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.team772.implementation.ClimbSystem;
import org.firstinspires.ftc.team772.implementation.IntakeSystem;
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.roadrunner.MecanumDrive;

@Autonomous( name = "Auto")
public class BlueAuto extends LinearOpMode {

    public class scoreSomething {
        OuttakeSystem outtakeSystem;
        IntakeSystem intakeSystem;
        ClimbSystem climbSystem;


        public scoreSomething() {
            outtakeSystem = new OuttakeSystem(hardwareMap);
            intakeSystem = new IntakeSystem(hardwareMap);
            climbSystem = new ClimbSystem(hardwareMap);
        }

        public Action scoreSpecimen() {
            return telemetryPacket -> {
                ParallelCommandGroup score = new ParallelCommandGroup();
                score.addCommands(
                        outtakeSystem.unGrip()
                );
                score.schedule();
                return false;
            };
        }

        public Action initPreload(){
            return telemetryPacket -> {

                ParallelCommandGroup commandGroup = new ParallelCommandGroup();

                commandGroup.addCommands(
                    outtakeSystem.gripIt(),
                    outtakeSystem.swingToTarget(),
                    climbSystem.setArmToPos(ClimbSystem.ArmPos.HIGHCLIMB)
                );

                commandGroup.schedule();

                Log.i("ROBO", "Initialized");
                return false;
            };
        }

        public Action setArmtoSafePos() {
            return telemetryPacket -> {

                ParallelCommandGroup commandGroup = new ParallelCommandGroup();

                commandGroup.addCommands(
                        climbSystem.setArmToPos(ClimbSystem.ArmPos.HOME),
                        outtakeSystem.swingToHome()
                );

                commandGroup.schedule();

                return false;
            };
        }

        public Action suckSample() {
            return telemetryPacket -> {

                intakeSystem.aim().schedule();

                return true;
            };
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPosition = new Pose2d(0, 63, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPosition);
        scoreSomething scoreSpecimen = new scoreSomething();


        TrajectoryActionBuilder goToBucket = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                .strafeTo(new Vector2d(85, 63))
                .strafeToLinearHeading(new Vector2d(85, 50), Math.toRadians(225))
                .strafeTo(new Vector2d(100, 59));

        TrajectoryActionBuilder park = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                .strafeTo(new Vector2d(33 * 1.7, 33 * 1.7));

        //TrajectoryActionBuilder goToFirstSample = drive.actionBuilder(new Pose2d(100, 59, Math.toRadians(225)))


        waitForStart();

        Actions.runBlocking(
            new SequentialAction(
                    scoreSpecimen.initPreload(),
                    goToBucket.build(),
                    scoreSpecimen.scoreSpecimen()
//                    park.build()
//                    scoreSpecimen.setArmtoSafePos(),
//                    scoreSpecimen.suckSample()
            )
        );
    }
}
