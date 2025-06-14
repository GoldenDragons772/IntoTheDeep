package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

/**
 * Command to toggle the intake system and prepare the outtake system for transfer.
 * This command is used in the tele-op mode to set the intake and outtake systems for transferring items.
 */
class ToggleIntakeCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem) :
    SequentialCommandGroup() {
    init {
        super.addCommands(
            SequentialCommandGroup(
                outtakeSystem.clawOpen(),
                outtakeSystem.moveArmToTransferPrep(),
                intakeSystem.toggleIntake()
            )
        )
    }
}