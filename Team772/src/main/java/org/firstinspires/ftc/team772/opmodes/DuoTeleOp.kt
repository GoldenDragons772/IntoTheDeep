package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.team772.helpers.DriveManager

@TeleOp(name = "DuoTeleOp")
class clsDuoTeleOp: CommandOpMode() {
    private lateinit var driveManager: DriveManager

    override fun initialize() {
        CommandScheduler.getInstance().reset()
        val mapping = DriveManager.Mapping(
            unClimbMapping = Pair(GamepadKeys.Button.DPAD_DOWN, 2),
            lowclimbMapping = Pair(GamepadKeys.Button.DPAD_LEFT, 2),
            highclimbMapping = Pair(GamepadKeys.Button.DPAD_UP, 2),
            suckMapping = Pair(GamepadKeys.Trigger.RIGHT_TRIGGER, 2),
            unSuckMapping = Pair(GamepadKeys.Button.LEFT_BUMPER, 2),
            aimMapping = Pair(GamepadKeys.Button.Y, 2),
            transferMapping = Pair(GamepadKeys.Button.A, 2),
            swingMapping = Pair(GamepadKeys.Button.B, 2),
            gripMapping = Pair(GamepadKeys.Button.X, 2)
        )

        driveManager = DriveManager(hardwareMap, gamepad1, gamepad2, mapping)

    }

    override fun run() {
        super.run()
        driveManager.update()
    }

}