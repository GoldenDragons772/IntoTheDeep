package org.firstinspires.ftc.teamcode.implementation.commands


import com.arcrobotics.ftclib.command.SequentialCommandGroup
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

/**
 * Command to automatically move the robot to the wall pickup state and set all systems to home position.
 * This command is used in the auto mode to prepare the robot for the next action.
 */
class AutoSpecWallCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {
        init {
            super.addCommands(
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
                    outtakeSystem.moveArmToHome(),
                    intakeSystem.moveToHome(),
            )

            addRequirements(intakeSystem, outtakeSystem, climbSystem)
        }
}