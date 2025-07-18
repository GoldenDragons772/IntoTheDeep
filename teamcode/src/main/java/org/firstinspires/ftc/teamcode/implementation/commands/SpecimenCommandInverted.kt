package org.firstinspires.ftc.teamcode.implementation.commands

import android.util.Log
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

/**
 * Command to score the specimen in inverted position by retracting the intake system,
 * toggling the outtake arm, and setting the climb system to high chamber or home position based on the outtake state.
 * This command is used in the tele-op mode primarily to set the servos in specimen scoring or picking position.
 */
class SpecimenCommandInverted(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init {

        var prevState = IntakeSystem.LinkagePosition.HOME

        super.addCommands(
            SequentialCommandGroup(
                InstantCommand({Log.i("CMDS", this.javaClass.name + "\n" + intakeSystem.stateString() + "\n" + outtakeSystem.stateString() + "\n" + climbSystem.stateString())}),
                InstantCommand({prevState = intakeSystem.linkagePos}),
                intakeSystem.moveToHome(), // retract intake system
                ConditionalCommand(WaitCommand(500), InstantCommand()) { prevState == IntakeSystem.LinkagePosition.FULL },
                ConditionalCommand( // Move climb system down
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HIGH_CHAMBER_INVERTED),
                    climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME)
                ) { outtakeSystem.getSpecState() },
                WaitCommand(100), // Move arm to spec grab position
                outtakeSystem.toggleArmSpecInv()
            )
        )
    }
}