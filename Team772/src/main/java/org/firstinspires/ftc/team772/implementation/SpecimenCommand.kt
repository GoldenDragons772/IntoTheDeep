package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SequentialCommandGroup

class SpecimenCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem): SequentialCommandGroup() {

    init{
        super.addCommands(
            SequentialCommandGroup(
            outtakeSystem.toggleArmSpec(),
                intakeSystem.moveToHome()
            )

        )
    }

}