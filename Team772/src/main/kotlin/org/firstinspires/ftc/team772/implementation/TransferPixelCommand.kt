package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.CommandBase

/**
 * Transfers a pixel from the intake to the outtake.
 */
class TransferPixelCommand(private val intake: IntakeSystem, private val outtake: OuttakeSystem) : CommandBase() {
    // I
    val targetPos = IntakeSystem.ExtendPos.HOME;

    init {
        addRequirements(intake)
        addRequirements(outtake)
    }

    override fun initialize() {
        // Enforce required states
        // TODO: Make these all run smoothly instead of all at once.
        if (!outtake.swingState) outtake.swingToHome()
        if (outtake.gripState) outtake.unGrip()
        if (intake.aimState) intake.goHome()
        intake.spit()
        outtake.gripIt()
        intake.aim()
        outtake.swingToTarget()
    }

    fun setPosition(pos: IntakeSystem.ExtendPos) {
        intake.setSlideToPos(pos)
    }

    override fun isFinished(): Boolean {
        return intake.slideMotor.currentPosition == targetPos.position;
    }

}