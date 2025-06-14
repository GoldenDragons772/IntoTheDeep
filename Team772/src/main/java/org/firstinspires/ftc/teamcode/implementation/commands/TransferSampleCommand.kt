package org.firstinspires.ftc.teamcode.implementation.commands

import com.arcrobotics.ftclib.command.ParallelRaceGroup
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.arcrobotics.ftclib.command.WaitUntilCommand
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem

/**
 * Command to transfer a sample by moving the intake system to transfer position,
 * opening the outtake claw, and moving the outtake arm to transfer position.
 * This command is used in both teleop and auto modes to prepare the robot for sample transfer.
 */
class TransferSampleCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): SequentialCommandGroup() {

    init {
        addCommands(
            climbSystem.setTargetPosition(ClimbSystem.ClimbState.HOME),
            //intakeSystem.setClaw(IntakeSystem.IntakePosition.TARGET),
            outtakeSystem.clawOpen(),
            //outtakeSystem.moveArmToTransferPrep(),
            intakeSystem.moveToTransfer(),
            WaitCommand(200),
            outtakeSystem.moveArmToTransfer(),
            WaitUntilCommand{ outtakeSystem.getClawButtonState() }.withTimeout(800),
            outtakeSystem.clawClose(),
            WaitCommand(500),
            intakeSystem.setClaw(IntakeSystem.IntakePosition.HOME),
            outtakeSystem.moveArmToScore()
        )
    }

}