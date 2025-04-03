package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

class ToggleIntakeCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem) :
    SequentialCommandGroup() {
    init {
        super.addCommands(
            SequentialCommandGroup(
                intakeSystem.toggleIntake(),
                //outtakeSystem.clawOpen(),
                outtakeSystem.moveArmToTransferPrep()
            )
        )
    }

}