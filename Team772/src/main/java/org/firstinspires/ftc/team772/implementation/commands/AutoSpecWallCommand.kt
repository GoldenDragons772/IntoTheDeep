package org.firstinspires.ftc.team772.implementation.commands

import android.util.Log
import com.arcrobotics.ftclib.command.*
import org.firstinspires.ftc.team772.implementation.ClimbSystem
import org.firstinspires.ftc.team772.implementation.IntakeSystem
import org.firstinspires.ftc.team772.implementation.OuttakeSystem

class AutoSpecWallCommand(private val intakeSystem: IntakeSystem, private val outtakeSystem: OuttakeSystem, private val climbSystem: ClimbSystem): ParallelCommandGroup() {
        init {
            addCommands(
                WaitCommand(500).andThen(climbSystem.setTargetPosition(ClimbSystem.ClimbState.SPEC_HANG.position/1.7)),
                outtakeSystem.moveArmToHome(),
                intakeSystem.moveToHome(),
                InstantCommand({Log.i("ROBO", "Tried to return home")})
            )
        }

    fun clone(): AutoSpecWallCommand {
        return AutoSpecWallCommand(this.intakeSystem, this.outtakeSystem, this.climbSystem)
    }
    fun alongWith(other: Command): AutoSpecWallCommand {
        val new = this.clone()
        new.addCommands(other)
        return new
    }

}