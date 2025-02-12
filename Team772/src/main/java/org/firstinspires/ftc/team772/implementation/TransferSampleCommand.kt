package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand

class TransferSampleCommand(private val intakeSystem: IntakeSubsystem, private val outtakeSystem: OuttakeSystem): SequentialCommandGroup() {



    init {
        super.addCommands(
            SequentialCommandGroup(
                intakeSystem.setClaw(IntakeSubsystem.IntakePosition.TARGET),
                intakeSystem.moveToTransfer(),
                WaitCommand(1000),
                outtakeSystem.moveArmToTransfer(),
                WaitCommand(500),
                outtakeSystem.clawClose(),
                intakeSystem.setClaw(IntakeSubsystem.IntakePosition.HOME),
                outtakeSystem.moveArmToScore()
            )
        )
    }

}