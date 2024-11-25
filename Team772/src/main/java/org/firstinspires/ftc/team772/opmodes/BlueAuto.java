package org.firstinspires.ftc.team772.opmodes;

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
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.roadrunner.MecanumDrive;

@Autonomous( name = "Auto")
public class BlueAuto extends LinearOpMode {

    public class scoreSomething {

        OuttakeSystem outtakeSystem;

        public scoreSomething() {
            outtakeSystem = new OuttakeSystem(hardwareMap);
        }

        public class scoreSpecimen implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                SequentialCommandGroup score = new SequentialCommandGroup();
                score.addCommands(
                        outtakeSystem.gripIt(),
                        outtakeSystem.swingToTarget(),
                        outtakeSystem.wristTurn()
                );

                score.schedule();
                return true;
            }
        }

        public Action scoreSpecimen() {
            return new scoreSpecimen();
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPosition = new Pose2d(0, 63, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPosition);
        scoreSomething scoreSpecimen = new scoreSomething();


        TrajectoryActionBuilder park = drive.actionBuilder(new Pose2d(0, 63, Math.toRadians(180)))
                    .strafeTo(new Vector2d(-58, 63)
        );


        waitForStart();

        Actions.runBlocking(
            new SequentialAction(
                    park.build()
//                    scoreSpecimen.scoreSpecimen(),
//                    park.build()
            )
        );
    }
}
