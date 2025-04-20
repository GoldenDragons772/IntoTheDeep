package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

class SpecimenCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init {
        super.addCommands(
            SequentialCommandGroup(
                ConditionalCommand(
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HIGH_CHAMBER),
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME)
                ) { outtakeSystem.getSpecState() },
                intakeSystem.moveToHome(),
                WaitCommand(2000).andThen(outtakeSystem.toggleArmSpec())
            )
        )
    }
}