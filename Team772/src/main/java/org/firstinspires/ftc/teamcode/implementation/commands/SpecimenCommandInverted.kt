package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

class SpecimenCommandInverted(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init {

        var prevState = IntakeSystem.LinkagePosition.HOME

        super.addCommands(
            SequentialCommandGroup(
                InstantCommand({prevState = intakeSystem.linkagePos}),
                intakeSystem.moveToHome(),
                ConditionalCommand(WaitCommand(500), InstantCommand(), {prevState == IntakeSystem.LinkagePosition.FULL}),
                ConditionalCommand(
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HIGH_CHAMBER_INVERTED),
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME)
                ) { outtakeSystem.getSpecState() },
                WaitCommand(500),
                outtakeSystem.toggleArmSpecInv()
            )
        )
    }
}