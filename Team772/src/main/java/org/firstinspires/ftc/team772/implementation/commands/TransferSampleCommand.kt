package org.firstinspires.ftc.team772.implementation.commands

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem

class TransferSampleCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init {
        super.addCommands(
            SequentialCommandGroup(
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                intakeSystem.setClaw(IntakeSystem.IntakePosition.TARGET),
                outtakeSystem.clawOpen(),
                intakeSystem.moveToTransfer(),
                //WaitCommand(600),
                outtakeSystem.moveArmToTransfer(),
                WaitCommand(500),
                outtakeSystem.clawClose(),
                WaitCommand(500),
                intakeSystem.setClaw(IntakeSystem.IntakePosition.HOME),
                outtakeSystem.moveArmToScore()
            )
        )
    }

}