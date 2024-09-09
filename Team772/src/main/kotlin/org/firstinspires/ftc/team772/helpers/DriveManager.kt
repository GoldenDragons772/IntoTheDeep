package org.firstinspires.ftc.team772.helpers

import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.implementation.Constants

/**
 * Manages driving and button mappings for TeleOps.
 */
class DriveManager(private val hardwareMap: HardwareMap, gp1: Gamepad, gp2: Gamepad) {
    /**
     * Subsystems
     */
    var robot: ControlSystem? = null

    /**
     * Controllers
     */
    private var gamepad1: GamepadEx? = null
    private var gamepad2: GamepadEx? = null

    init {
//        TODO("Add stuff here: reset command scheduler, initialize bindings")
        gamepad1 = GamepadEx(gp1)
        gamepad2 = GamepadEx(gp2)
        // THIS IS FOR BLAH BLAH BLAH BLHA BLAH
        robot = Constants.CURRENT_IMPLEMENTATION(hardwareMap)
//        this.initializeBindings()
    }

    /**
     * Start driving and processing input: Main drive loop.
     */
    fun update() {
//        TODO("Add drive logic, run command scheduler")
        robot!!.drive(gamepad1!!.rightX, gamepad1!!.rightY, gamepad1!!.leftX)
    }

    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings() {
        TODO("Add logic here")
    }

    /**
     * Definition of possible mappings. Using this instead of a dictionary/hashmap allows for code completion.
     */
    class Mapping(
        val dummyMapping: Pair<GamepadKeys.Button, Int> // TODO: Add mappings
    )
}