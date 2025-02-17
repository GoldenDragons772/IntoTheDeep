package org.firstinspires.ftc.team772.implementation.commands

import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem

class AutoSpecWallCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init{
        super.addCommands(
            SequentialCommandGroup(
                climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                outtakeSystem.moveArmToHome(),
                intakeSystem.moveToHome()
            )
        )
    }
}