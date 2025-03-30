package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.ParallelRaceGroup
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

class TransferSampleCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init {
        super.addCommands(
            SequentialCommandGroup(
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                intakeSystem.setClaw(IntakeSystem.IntakePosition.TARGET),
                outtakeSystem.clawOpen(),
                outtakeSystem.moveArmToTransfer(),
                intakeSystem.moveToTransfer(),
               // ParallelRaceGroup(WaitUntilCommand { outtakeSystem.getClawButtonState() }, WaitCommand(1500)),
                WaitCommand(1000),
                outtakeSystem.clawClose(),
                WaitCommand(500),
                intakeSystem.setClaw(IntakeSystem.IntakePosition.HOME),
                outtakeSystem.moveArmToScore()
            )
        )
    }

}