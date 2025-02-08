package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.team772.helpers.DriveManager
import kotlin.math.PI

@TeleOp(name = "Solo TeleOp")
class Solo: CommandOpMode() {

    private lateinit var driveManager: DriveManager

    override fun initialize() {
        CommandScheduler.getInstance().reset()

        val mapping = DriveManager.Mapping(
            testMapping = Pair(GamepadKeys.Button.A, 1),
            gripMapping = Pair(GamepadKeys.Button.B, 1),
            lowclimbMapping = Pair(GamepadKeys.Button.DPAD_LEFT, 1),
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 1),
            highclimbMapping = Pair(GamepadKeys.Button.DPAD_UP, 1)
        )
        driveManager = DriveManager(hardwareMap, gamepad1, gamepad2, mapping)

    }

    /**
     * Runs when the run button is pressed. Runs the main drive loop.
     */
    override fun run() {
        super.run()
        driveManager.update()
    }

}