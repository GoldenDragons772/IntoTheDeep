package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand

/**
 * Transfers a pixel from the intake to the outtake.
 */
class TransferPixelCommand(private val intake: IntakeSystem, private val outtake: OuttakeSystem) : SequentialCommandGroup() {
    // I
    val targetPos = IntakeSystem.ExtendPos.HOME;


    init {
        super.addCommands(
            outtake.swingToHome(),
            WaitCommand(500),
            outtake.unGrip(),
            WaitCommand(500),
            intake.goHome(),
            WaitCommand(500),
            intake.spit(),
            WaitCommand(500),
            outtake.gripIt(),
            WaitCommand(500),
            intake.aim(),
            WaitCommand(500),
            outtake.swingToTarget()
        )
        addRequirements(intake, outtake)
    }

//    override fun isFinished(): Boolean {
//        return intake.slideMotor.currentPosition == targetPos.position;
//    }

}