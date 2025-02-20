package org.firstinspires.ftc.team772.implementation.commands

import com.arcrobotics.ftclib.command.CommandGroupBase
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.ParallelCommandGroup
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem

class AutoSpecimenCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): ParallelCommandGroup() {

    init{
        addCommands(
            climbSystem.setTargetPosition(ClimbSystem.ClimbState.HIGH_CHAMBER),
            outtakeSystem.moveArmToScoreSpec(),
            intakeSystem.moveToHome()
        )
    }
    fun clone(): AutoSpecimenCommand {
        return AutoSpecimenCommand(this.intakeSystem, this.outtakeSystem, this.climbSystem)
    }
}