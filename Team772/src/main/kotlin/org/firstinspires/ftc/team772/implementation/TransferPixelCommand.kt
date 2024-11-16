package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.CommandBase
import com.arcrobotics.ftclib.command.SequentialCommandGroup

/**
 * Transfers a pixel from the intake to the outtake.
 */
class TransferPixelCommand(private val intake: IntakeSystem, private val outtake: OuttakeSystem) : SequentialCommandGroup() {
    // I
    val targetPos = IntakeSystem.ExtendPos.HOME;


    init {
        addRequirements(intake)
        addRequirements(outtake)
    }

    override fun initialize() {
        // Enforce required states
        super.addCommands(
        outtake.swingToHome(),
        outtake.unGrip(),
        intake.goHome(),
        intake.spit(),
        outtake.gripIt(),
        intake.aim(),
        outtake.swingToTarget()
        )
    }

//    override fun isFinished(): Boolean {
//        return intake.slideMotor.currentPosition == targetPos.position;
//    }

}