package org.firstinspires.ftc.team772.helpers

import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.implementation.Constants
import org.firstinspires.ftc.team772.implementation.ParallelPlateDrivesystem
import java.time.Instant

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
//        TODO("Add stuff here: reset command scheduler, initialize bindings")
        gamepad1 = GamepadEx(gp1)
        gamepad2 = GamepadEx(gp2) // THIS IS FOR BLAH BLAH BLAH BLHA BLAH
        robot = Constants.CURRENT_IMPLEMENTATION(hardwareMap)
        initializeBindings(mapping)
    }

    /**
     * Start driving and processing input: Main drive loop.
     */
    fun update() {
//        TODO("Add drive logic, run command scheduler")
        robot!!.drive(-gamepad1!!.rightX, gamepad1!!.rightY, gamepad1!!.leftX)
    }

    /**
     * Gets a gamepad from its int id.
     */
    private fun getGamepad(x: Int): GamepadEx? = if (x == 1) gamepad1 else gamepad2

    /**
     * Binds a function to a button on the controller.
     */
    private fun setPressedBinding(map: Pair<GamepadKeys.Button, Int>, function: () -> Unit, whenReleased: () -> Unit = {}) =
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whenPressed(InstantCommand({function()})).whenReleased(InstantCommand(whenReleased))

    private fun setHeldBinding(map: Pair<GamepadKeys.Button, Int>, function: () -> Unit, whenReleased: () -> Unit = {}) =
        getGamepad(map.second)!!.getGamepadButton(map.first)!!.whileHeld(InstantCommand({function()})).whenReleased(InstantCommand(whenReleased))

    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings(mapping: Mapping) {
        setPressedBinding(mapping.lowclimbMapping, robot!!::lowclimb)
        setPressedBinding(mapping.highclimbMapping, robot!!::highclimb)
        setPressedBinding(mapping.unClimbMapping, robot!!::unclimb)
        setPressedBinding(mapping.extendMapping, robot!!.intakeSystem::extend, robot!!.intakeSystem::retract)
        setPressedBinding(mapping.grabMapping, robot!!.intakeSystem::grab, robot!!.intakeSystem::ungrab)
        setPressedBinding(mapping.pivotMapping, robot!!.intakeSystem::pivot, robot!!.intakeSystem::unpivot)

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
        val extendMapping: Pair<GamepadKeys.Button, Int>,
        val grabMapping: Pair<GamepadKeys.Button, Int>,
        val pivotMapping: Pair<GamepadKeys.Button, Int>
    )
}