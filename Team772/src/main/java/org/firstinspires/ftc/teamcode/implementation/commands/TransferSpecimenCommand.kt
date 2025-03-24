package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

/**
 * Transfers a grabbed sample from the intake claw to the outtake claw and moves the outtake claw up to score.
 * This makes no guarantees that a specimen is actually grabbed, so it will still attempt to transfer without one.
 */
class TransferSpecimenCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem): SequentialCommandGroup() {
    init {
        super.addCommands(
             // It's unknown if this actually needs to be placed in two SequentialCommandGroups
            outtakeSystem.clawOpen(),
            intakeSystem.setStrike(IntakeSystem.IntakePosition.TRANSFER),
            intakeSystem.setLinkage(IntakeSystem.IntakePosition.TRANSFER),
            WaitCommand(700), // These have default values from the previous codebase.
            outtakeSystem.moveArmToHome(),
            WaitCommand(500),
            outtakeSystem.clawClose(),
            WaitCommand(500),
//            intakeSystem., // Might require the intake to be reset.
            TODO("Add claw to intakeSubsystem."),
            WaitCommand(100),
            outtakeSystem.moveArmToScore()
        )
    }
}