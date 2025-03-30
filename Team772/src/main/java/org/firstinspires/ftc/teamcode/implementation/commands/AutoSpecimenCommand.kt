package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.ParallelCommandGroup
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

class AutoSpecimenCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): ParallelCommandGroup() {

    init{
        addCommands(
            climbSystem.setTargetPosition(ClimbSystem.ClimbState.HIGH_CHAMBER),
            outtakeSystem.moveArmToScoreSpec(),
            intakeSystem.moveToHome()
        )

        addRequirements(climbSystem, outtakeSystem, intakeSystem)
    }
}