package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand

class TransferSpecimenCommand(private val intake: IntakeSystem, private val outtake: OuttakeSystem) : SequentialCommandGroup() {

    init{

        super.addCommands(

            SequentialCommandGroup(
                outtake.unGrip(),
                //WaitCommand(1000),
                intake.wristToSpecPos(),
                intake.goHome(),
                WaitCommand(700),
                outtake.swingToHome(),
                WaitCommand(500),
                outtake.gripIt(),
                WaitCommand(300),
                intake.spit(),
                intake.joint1SpecPose(),
                WaitCommand(100),
                outtake.swingToTarget(),
                WaitCommand(500)
            )
        )
        addRequirements(intake, outtake)
    }

}