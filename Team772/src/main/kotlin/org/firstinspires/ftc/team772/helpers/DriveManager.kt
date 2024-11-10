package org.firstinspires.ftc.team772.helpers

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.RepeatCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.implementation.Constants
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem

/**
 * Manages driving and button mappings for TeleOps.
 */
class DriveManager(private val hardwareMap: HardwareMap, gp1: Gamepad, gp2: Gamepad, mapping: Mapping) {
    /**
     * Subsystems
     */
    var robot: ParallelPlateDrivesystem? = null

    /**
     * Controllers
     */
    private var gamepad1: GamepadEx? = null
    private var gamepad2: GamepadEx? = null

    init {
        gamepad1 = GamepadEx(gp1)
        gamepad2 = GamepadEx(gp2)
        robot = ParallelPlateDrivesystem(hardwareMap)
        initializeBindings(mapping)
    }

    /**
     * Start driving and processing input: Main drive loop.
     */
    fun update() {
        robot!!.drive(-gamepad1!!.rightX, gamepad1!!.rightY, gamepad1!!.leftX)

    }

    /**
     * Gets a gamepad from its int id.
     */
    private fun getGamepad(x: Int): GamepadEx? = if (x == 1) gamepad1 else gamepad2

    /**
     * Binds a function to a button on the controller.
     */
    private fun setPressedBinding(
        map: Pair<GamepadKeys.Button, Int>,
        function: () -> Unit,
        whenReleased: () -> Unit = {}
    ) =
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whenPressed(InstantCommand({ function() }))
            .whenReleased(InstantCommand(whenReleased))


    private fun setPressedBinding(map: Pair<GamepadKeys.Button, Int>, function: Command) =
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whenPressed(
            SequentialCommandGroup(
                function
            )
        )


    private fun setHeldBinding(
        map: Pair<GamepadKeys.Button, Int>,
        function: () -> Unit,
        whenReleased: () -> Unit = {}
    ) =
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whileHeld(InstantCommand({ function() }))
            .whenReleased(InstantCommand(whenReleased))


    /**
     * Binds a function to the trigger and runs the function (repeatedly) if the trigger is pushed more than the threshold.
     * Note that this is more akin to setHeldBinding than the other setPressedBinding
     */
    private fun setHeldBinding(
        map: Pair<GamepadKeys.Trigger, Int>,
        function: () -> Unit, onRelease: () -> Unit
    ) {
        var isDown = getGamepad(map.second)!!.getTrigger(map.first) > 0.5
        val geepad = getGamepad(map.second)!!
        var lastIsDown = isDown
        CommandScheduler.getInstance().schedule(RepeatCommand(InstantCommand({
            isDown = geepad.getTrigger(map.first) > 0.5
            if (isDown) {
                function()
            } else if (lastIsDown) {
                onRelease()
            }
            lastIsDown = isDown
        })))
    }

    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings(mapping: Mapping) {
        setPressedBinding(mapping.lowclimbMapping, robot!!::lowclimb)
        setPressedBinding(mapping.highclimbMapping, robot!!::highclimb)
        setPressedBinding(mapping.unClimbMapping, robot!!::unclimb)
        setHeldBinding(mapping.suckMapping, robot!!.intakeSystem::swallow, robot!!.intakeSystem::stopSpit)
        setPressedBinding(mapping.aimMapping, robot!!.intakeSystem::aimToggle)
        setPressedBinding(mapping.swingMapping, robot!!.outtakeSystem::toggleSwing)
        setPressedBinding(mapping.gripMapping, robot!!.outtakeSystem::toggleGripper)
    }

    /**
     * Definition of possible mappings. Using this instead of a dictionary/hashmap allows for code completion.
     */
    class Mapping(
        /**
         * Added mapping for climbing
         */
        val lowclimbMapping: Pair<GamepadKeys.Button, Int>,
        val highclimbMapping: Pair<GamepadKeys.Button, Int>,
        val unClimbMapping: Pair<GamepadKeys.Button, Int>,
        val suckMapping: Pair<GamepadKeys.Trigger, Int>,
        val aimMapping: Pair<GamepadKeys.Button, Int>,
        val swingMapping: Pair<GamepadKeys.Button, Int>,
        val gripMapping: Pair<GamepadKeys.Button, Int>
    )
}