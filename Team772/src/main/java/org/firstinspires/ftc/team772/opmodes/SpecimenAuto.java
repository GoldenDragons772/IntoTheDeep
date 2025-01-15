package org.firstinspires.ftc.team772.opmodes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team772.autos.SpecimenPath;
import org.firstinspires.ftc.team772.implementation.ClimbSystem;
import org.firstinspires.ftc.team772.implementation.IntakeSystem;
import org.firstinspires.ftc.team772.implementation.OuttakeSystem;
import org.firstinspires.ftc.team772.implementation.TransferSpecimenCommand;
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
        TransferSpecimenCommand transferSpecimenCommand = new TransferSpecimenCommand(intakeSystem, outtakeSystem);

        follower.setStartingPose(new Pose(7.852, 55.945, Math.toRadians(180)));

        schedule(
            new WaitUntilCommand(this::opModeIsActive),
            new RunCommand(() -> {
                follower.update();
                follower.setMaxPower(0.8);
            }),
            new SequentialCommandGroup(
                outtakeSystem.gripIt(),
                climbSystem.specHangPrep(),
                outtakeSystem.swingToTarget(),
                new FollowPathCommand(follower, SpecimenPath.startSpecimenPath),
                climbSystem.specHangAttach(),
                new WaitCommand(1000),
                outtakeSystem.unGrip(),
                new ParallelCommandGroup(
                    new FollowPathCommand(follower, SpecimenPath.knock2SpecsIntoZone).setMaxPower(0.1),
                    climbSystem.unclimb()
                ),
                intakeSystem.aim(),
                new WaitCommand(1000),
                intakeSystem.swallow(),
                new WaitCommand(750),
                transferSpecimenCommand,
                new ParallelCommandGroup(
                    new FollowPathCommand(follower, SpecimenPath.goToChamberFromZone).setMaxPower(0.4),
                    climbSystem.specHangPrep()
                ),
                //new WaitCommand(2000),
                climbSystem.specHangAttach(),
                new WaitCommand(1000),
                outtakeSystem.unGrip(),
                    new ParallelCommandGroup(
                    new FollowPathCommand(follower, SpecimenPath.goToZoneFromChamber),
                            climbSystem.unclimb()
                            ),
                    intakeSystem.aim(),
                    new WaitCommand(1000),
                    intakeSystem.swallow(),
                    new WaitCommand(750),
                    transferSpecimenCommand,
                    new ParallelCommandGroup(
                            new FollowPathCommand(follower, SpecimenPath.goToChamberFromZone).setMaxPower(0.4),
                            climbSystem.specHangPrep()
                    ),
                    climbSystem.specHangAttach()
            )
        );
    }
}
