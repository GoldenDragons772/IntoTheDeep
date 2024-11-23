package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand

/**
 * Transfers a pixel from the intake to the outtake.
 */
class TransferPixelCommand(private val intake: IntakeSystem, private val outtake: OuttakeSystem) : SequentialCommandGroup() {
    // I
    val targetPos = IntakeSystem.ExtendPos.HOME


    init {
        super.addCommands(

            outtake.unGrip(),
            outtake.swingToHome(),
            WaitCommand(1000),
            intake.goHome(),
            WaitCommand(500),
            outtake.gripIt(),
            intake.spit(),
            outtake.gripIt(),
            intake.edgeCommand(),
            WaitCommand(500),
            outtake.gripIt(),
            outtake.swingToTarget(),
            WaitCommand(500),
            outtake.gripIt(),
            intake.retractCommand(),
            intake.stopSpit()

            /*
            outtake.unGrip(),
            WaitCommand(500),
            intake.goHome(),
            WaitCommand(500),
            outtake.swingToHome(),
            WaitCommand(500),
            outtake.gripIt(),
            WaitCommand(500),
            intake.spit(),
            outtake.gripIt(),
            intake.edgeCommand(),
            WaitCommand(500),
            outtake.gripIt(),
            outtake.swingToTarget(),
            WaitCommand(500),
            outtake.gripIt(),
            intake.retractCommand(),
            intake.stopSpit()
             */
        )
        addRequirements(intake, outtake)
    }

//    override fun isFinished(): Boolean {
//        return intake.slideMotor.currentPosition == targetPos.position;
//    }

}