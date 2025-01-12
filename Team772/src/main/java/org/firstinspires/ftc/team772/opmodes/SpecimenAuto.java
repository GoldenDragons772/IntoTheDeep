package org.firstinspires.ftc.team772.opmodes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team772.autos.SpecimenPath;
import org.firstinspires.ftc.team772.implementation.ClimbSystem;
import org.firstinspires.ftc.team772.implementation.IntakeSystem;
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.implementation.commands.FollowPathCommand;
import org.firstinspires.ftc.team772.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.team772.pedroPathing.constants.LConstants;

@Autonomous(name = "Specimen Auto", group = "Pedro")
public class SpecimenAuto extends CommandOpMode {

    Follower follower;

    @Override
    public void initialize() {

        Constants.setConstants(FConstants.class, LConstants.class);

        follower = new Follower(hardwareMap);
        IntakeSystem intakeSystem = new IntakeSystem(hardwareMap);
        OuttakeSystem outtakeSystem = new OuttakeSystem(hardwareMap);
        ClimbSystem climbSystem = new ClimbSystem(hardwareMap);

        follower.setStartingPose(new Pose(7.852, 55.945, Math.toRadians(180)));

        follower.setMaxPower(0.4);

        schedule(
            new WaitUntilCommand(this::opModeIsActive),
            new RunCommand(() -> {
                follower.update();
            }),
            new SequentialCommandGroup(
                climbSystem.specHangPrep(),
                outtakeSystem.swingToTarget(),
                outtakeSystem.gripIt(),
                new FollowPathCommand(follower, SpecimenPath.startSpecimenPath),
                climbSystem.lowclimb(),
                new WaitCommand(2000),
                outtakeSystem.unGrip()
            )
        );
    }
}
