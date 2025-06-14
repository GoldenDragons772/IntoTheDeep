package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

/**
 * Command to score the specimen by moving the intake system to home position,
 * toggling the outtake arm, and setting the climb system to high chamber or home position based on the outtake state.
 * This command can be used both in teleop and auto to set the servos in specimen scoring or picking positon.
 */
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