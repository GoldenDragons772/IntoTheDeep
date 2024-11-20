package org.firstinspires.ftc.team772.helpers

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.RepeatCommand
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem
import org.firstinspires.ftc.team772.implementation.TransferPixelCommand

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
    // TODO: Fix the arguments.
    fun update() {
        robot!!.drive(
            -gamepad1!!.leftX,
            gamepad1!!.rightY,
            gamepad1!!.rightX
        );
    }

    /**
     * Gets a gamepad from its int id.
     */
    private fun getGamepad(x: Int): GamepadEx? = if (x == 1) gamepad1 else gamepad2

    /**
     * Binds a function to a button on the controller.
     */
    private fun setPressedBinding(
        map: Pair<GamepadKeys.Button, Int>, function: () -> Unit, whenReleased: () -> Unit = {}
    ) {
        setPressedBinding(map, InstantCommand({ function() }), InstantCommand({ whenReleased() }))
    }

    private fun setPressedBinding(
        map: Pair<GamepadKeys.Button, Int>,
        function: Command,
        whenReleased: Command = InstantCommand()
    ) {
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whenPressed(
            function
        ).whenReleased(whenReleased)
    }


    private fun setHeldBinding(
        map: Pair<GamepadKeys.Button, Int>, function: () -> Unit, whenReleased: () -> Unit = {}
    ) {
        setHeldBinding(map, InstantCommand({ function() }), InstantCommand({ whenReleased() }))
    }

    private fun setHeldBinding(
        map: Pair<GamepadKeys.Button, Int>, function: Command, whenReleased: Command
    ) {
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whileHeld(function)
            .whenReleased(whenReleased)
    }


    /**
     * Binds a function to the trigger and runs the function (repeatedly) if the trigger is pushed more than the threshold.
     * Note that this is more akin to setHeldBinding than the other setPressedBinding
     */
    private fun setHeldTriggerBinding(
        map: Pair<GamepadKeys.Trigger, Int>, function: InstantCommand, onRelease: InstantCommand
    ) {
        var isDown = getGamepad(map.second)!!.getTrigger(map.first) > 0.5
        val geepad = getGamepad(map.second)!!
        var lastIsDown = isDown
        CommandScheduler.getInstance().schedule(RepeatCommand(InstantCommand({
            isDown = geepad.getTrigger(map.first) > 0.5
            if (isDown) {
                function.schedule()
            } else if (lastIsDown) {
                onRelease.schedule()
            }
            lastIsDown = isDown
        })))
    }

    /**
     * Honestly I'm not really sure what I did but it should do an action when the function is pressed.
     * @param map This is the controller binding you want to pass in.
     * @param function This is what will happen when the trigger is pressed.
     */
    private fun setPressedTriggerBinding(map: Pair<GamepadKeys.Trigger, Int>, function: Command){
        var isDown = getGamepad(map.second)!!.getTrigger(map.first) > 0.5
        val geepad = getGamepad(map.second)!!
        var lastIsDown = isDown
        CommandScheduler.getInstance().schedule(RepeatCommand(InstantCommand({
            isDown = geepad.getTrigger(map.first) > 0.5
            if(isDown){
                function.schedule()
            }
        })))
    }

    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings(mapping: Mapping) {
        setPressedBinding(mapping.lowclimbMapping, robot!!::lowclimb)// :: for the pointer to the function.
        setPressedBinding(mapping.highclimbMapping, robot!!::highclimb)
        setPressedBinding(mapping.unClimbMapping, robot!!::unclimb)
        setPressedTriggerBinding(mapping.suckMapping, robot!!.intakeSystem.suckToggle())
        setHeldBinding(mapping.unSuckMapping, robot!!.intakeSystem.spit(), robot!!.intakeSystem.stopSpit())
        // Toggle extending the arm out and prime for picking up pixels.
        setPressedBinding(mapping.aimMapping, robot!!.intakeSystem.aimToggle())
        setPressedBinding(mapping.swingMapping, robot!!.outtakeSystem.toggleSwing())
        setPressedBinding(mapping.transferMapping, TransferPixelCommand(robot!!.intakeSystem, robot!!.outtakeSystem))
        setPressedBinding(mapping.gripMapping, robot!!.outtakeSystem.toggleGripper())
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
        val unSuckMapping: Pair<GamepadKeys.Button, Int>,
        val aimMapping: Pair<GamepadKeys.Button, Int>,
        val swingMapping: Pair<GamepadKeys.Button, Int>,
        val gripMapping: Pair<GamepadKeys.Button, Int>,
        val transferMapping: Pair<GamepadKeys.Button, Int>
    )
}