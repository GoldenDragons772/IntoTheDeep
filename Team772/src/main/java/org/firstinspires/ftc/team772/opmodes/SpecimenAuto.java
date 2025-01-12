package org.firstinspires.ftc.team772.opmodes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.team772.implementation.ClimbSystem;
import org.firstinspires.ftc.team772.implementation.IntakeSystem;
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem;

public class SpecimenAuto extends CommandOpMode {
    @Override
    public void initialize() {

        Follower follower = new Follower(hardwareMap);
        IntakeSystem intakeSystem = new IntakeSystem(hardwareMap);
        OuttakeSystem outtakeSystem = new OuttakeSystem(hardwareMap);
        ClimbSystem climbSystem = new ClimbSystem(hardwareMap);

        register(intakeSystem, outtakeSystem, climbSystem);

        schedule(
            new SequentialCommandGroup(

            )
        );
    }
}
