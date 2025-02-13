package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand

class TransferSampleCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem): SequentialCommandGroup() {



    init {
        super.addCommands(
            SequentialCommandGroup(
                intakeSystem.setClaw(IntakeSystem.IntakePosition.TARGET),
                intakeSystem.moveToTransfer(),
                WaitCommand(1000),
                outtakeSystem.moveArmToTransfer(),
                WaitCommand(500),
                outtakeSystem.clawClose(),
                intakeSystem.setClaw(IntakeSystem.IntakePosition.HOME),
                outtakeSystem.moveArmToScore()
            )
        )
    }

}