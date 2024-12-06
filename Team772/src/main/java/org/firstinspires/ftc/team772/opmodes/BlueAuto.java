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
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team772.implementation.IntakeSystem;
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem;
import org.firstinspires.ftc.team772.pedroPathing.localization.Pose;
import org.firstinspires.ftc.team772.roadrunner.MecanumDrive;

@Autonomous( name = "Auto")
public class BlueAuto extends LinearOpMode {

    public class scoreSomething {
        OuttakeSystem outtakeSystem;
        IntakeSystem intakeSystem;

        public scoreSomething() {
            outtakeSystem = new OuttakeSystem(hardwareMap);
            intakeSystem = new IntakeSystem(hardwareMap);
        }

        public class scoreSpecimen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                SequentialCommandGroup score = new SequentialCommandGroup();
                score.addCommands(
                        //outtakeSystem.gripIt(),
                        //outtakeSystem.swingToTarget(),
                        //outtakeSystem.wristTurn()
                );

                score.schedule();
                return true;
            }
        }

        public class initPreload implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                SequentialCommandGroup prep = new SequentialCommandGroup();
                prep.addCommands(
                outtakeSystem.gripIt(),
                outtakeSystem.swingToTarget()
                );
                prep.schedule();
                Log.i("ROBO", "Initialized");
                return false;
            }
        }

        public Action scoreSpecimen() {
            return new scoreSpecimen();
        }

        public Action initPreload(){
            return new initPreload();
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPosition = new Pose2d(0, 63, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPosition);
        scoreSomething scoreSpecimen = new scoreSomething();
        scoreSomething initPreload = new scoreSomething();

        //                .strafeTo(new Vector2d(85, 63))
        //                .waitSeconds(1)
        //                .strafeTo(new Vector2d(-65, 63));

        TrajectoryActionBuilder park = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                .strafeTo(new Vector2d(-65, 63));

        TrajectoryActionBuilder goToBucket = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                        .strafeTo(new Vector2d(85, 63));


        waitForStart();

        Actions.runBlocking(
            new SequentialAction(
                    scoreSpecimen.initPreload(),
                    goToBucket.build()
                    //scoreSpecimen.scoreSpecimen()
//                  park.build()
            )
        );
    }
}
