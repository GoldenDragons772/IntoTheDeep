package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.CommandBase

/**
 * Performs actions on the intake subsystem.
 */
class IntakeCommand(intake: IntakeSystem) : CommandBase() {
    // I
    val intake: IntakeSystem = intake;
    val targetPos = IntakeSystem.ExtendPos.HOME;

    init {
        addRequirements(intake)
    }

    override fun initialize() {
        intake.setSlideToPos(IntakeSystem.ExtendPos.HOME);
    }

    fun setPosition(pos: IntakeSystem.ExtendPos) {
        intake.setSlideToPos(pos)
    }

    override fun isFinished(): Boolean {
        return intake.slideMotor.currentPosition == targetPos.position;
    }

}