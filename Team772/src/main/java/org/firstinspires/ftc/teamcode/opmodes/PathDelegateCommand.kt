package org.firstinspires.ftc.teamcode.opmodes

import androidx.core.util.Supplier
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandBase
import com.pedropathing.commands.FollowPath

class PathDelegateCommand(val commandSupplier: Supplier<FollowPath>): CommandBase() {

    lateinit var command: FollowPath

    override fun initialize() {
        command = commandSupplier.get()
        command.initialize()
    }

    override fun execute() {
        command.execute()
    }

    override fun end(interrupted: Boolean) {
        command.end(interrupted)
    }

    override fun isFinished(): Boolean {
        return command.isFinished
    }

}