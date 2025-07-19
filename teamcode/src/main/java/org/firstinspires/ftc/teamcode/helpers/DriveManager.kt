package org.firstinspires.ftc.teamcode.helpers

import android.util.Log
import com.arcrobotics.ftclib.command.*
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.SpecimenCommandInverted
import org.firstinspires.ftc.teamcode.implementation.commands.ToggleIntakeCommand
import org.firstinspires.ftc.teamcode.implementation.commands.TransferSampleCommand

/**
 * Manages driving and button mappings for TeleOps.
 */
class DriveManager(
    hw: HardwareMap,
    telemetry: Telemetry,
    gp1: Gamepad,
    gp2: Gamepad,
    mapping: Mapping,
    isAllianceRed: Boolean
) {
    /**
     * Subsystems
     */
    val root: RootSystem = RootSystem(hw, telemetry, false, isSpecAuto = false, isAllianceRed)

    /**
     * Controllers
     */
    val gamepad1: GamepadEx = GamepadEx(gp1)
    val gamepad2: GamepadEx = GamepadEx(gp2)

    init {
        initializeBindings(mapping)
        root.follower.startTeleopDrive()
    }

    /**
     * Start driving and processing input: Main drive loop.
     */
    fun update() {
        root.update() // Updates bulk reads and odometry.
        if (gamepad1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.0) {
            root.teleOpDriveScaled(
                -gamepad1.rightX,
                gamepad1.rightY,
                -gamepad1.leftX
            )
        } else {
            root.teleOpDrive(
                -gamepad1.rightX,
                gamepad1.rightY,
                -gamepad1.leftX
            )
        }
    }

    /**
     * Gets a gamepad from its int id.
     */
    private fun getGamepad(x: Int): GamepadEx = if (x == 1) gamepad1 else gamepad2


    private fun setPressedBinding(
        map: Pair<GamepadKeys.Button, Int>,
        function: Command,
        whenReleased: Command = InstantCommand()
    ) {
        getGamepad(map.second).getGamepadButton(map.first)!!.whenPressed(
            function
        ).whenReleased(whenReleased)
    }

    private fun setHeldBinding(
        map: Pair<GamepadKeys.Button, Int>,
        function: Command,
        whenReleased: Command = InstantCommand()
    ) {
        getGamepad(map.second).getGamepadButton(map.first)!!.whileHeld(
            function
        ).whenReleased(whenReleased)
    }


    /**
     * Honestly I'm not really sure what I did but it should do an action when the function is pressed.
     * @param map This is the controller binding you want to pass in.
     * @param function This is what will happen when the trigger is pressed.
     */
    private fun setPressedTriggerBinding(map: Pair<GamepadKeys.Trigger, Int>, function: Command) {
        var isDown = getGamepad(map.second).getTrigger(map.first) > 0.5
        var lastIsDown = isDown
        CommandScheduler.getInstance().schedule(RepeatCommand(InstantCommand({ // Repeatedly run an instant command
            isDown = getGamepad(map.second).getTrigger(map.first) > 0.5 // Every loop, update isDown
            if (!lastIsDown && isDown) function.schedule()// If was just down and is now up, schedule function.
            lastIsDown = isDown
        })))
    }

    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings(mapping: Mapping) {

        //Climb controls
        setPressedBinding(mapping.lowclimbMapping, root.climb.setTargetPosition(ClimbSystem.ClimbState.LOW_BASKET))
        setPressedBinding(mapping.highclimbMapping, root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET))
        setPressedBinding(mapping.unClimbMapping, root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME))
        setPressedBinding(mapping.climbUpMapping, root.climb.sendRawMotors(1.0))
        setPressedBinding(mapping.climbDownMapping, root.climb.sendRawMotors(-1.0))
        setPressedBinding(
            mapping.hangLowSpecMapping,
            SpecimenCommandInverted(root.intake, root.outtake, root.climb, ClimbSystem.ClimbState.LOW_CHAMBER)
        )

        setPressedBinding(
            mapping.hangSpecMapping,
            SpecimenCommandInverted(root.intake, root.outtake, root.climb, ClimbSystem.ClimbState.HIGH_CHAMBER_INVERTED)
        )
        setPressedBinding(mapping.aimMapping, ToggleIntakeCommand(root.intake, root.outtake))
        setPressedBinding(mapping.transferMapping, TransferSampleCommand(root.intake, root.outtake, root.climb))
        // Try to move to transfer position if some conditions are met
        // I'm pretty sure this can be safely removed, but I won't remove it because I'm not quite sure what it does.
        setPressedBinding(
            mapping.gripMapping,
            ConditionalCommand(
                root.outtake.toggleClaw()
                    .andThen(root.outtake.setPivot(OuttakeSystem.OuttakePosition.TRANSFER)),
                root.outtake.toggleClaw()
            )
            { !root.outtake.homeState && root.climb.position == ClimbSystem.ClimbState.HIGH_CHAMBER })
        setPressedBinding(mapping.climbToHangSpec, root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_CHAMBER))
        // Claw Commands
        setPressedTriggerBinding(mapping.clawMapping, root.intake.toggleClaw())
        setHeldBinding(mapping.wristMappingLeft, root.intake.incrementWristLeft())
        setHeldBinding(mapping.wristMappingRight, root.intake.incrementWristRight())
        if (mapping.moveIntakeMapping != null) {
            Log.i("ROBO Init", "Initialized with moveIntake.")
            setPressedTriggerBinding(
                mapping.moveIntakeMapping,
                root.intake.toggleHover()
            )
        }

    }

    /**
     * Definition of possible mappings. Using this instead of a dictionary/hashmap allows for code completion.
     */
    class Mapping(
        val lowclimbMapping: Pair<GamepadKeys.Button, Int>,
        val highclimbMapping: Pair<GamepadKeys.Button, Int>,
        val unClimbMapping: Pair<GamepadKeys.Button, Int>,
        val aimMapping: Pair<GamepadKeys.Button, Int>,
        val gripMapping: Pair<GamepadKeys.Button, Int>,
        val transferMapping: Pair<GamepadKeys.Button, Int>,
        val climbToHangSpec: Pair<GamepadKeys.Button, Int>,
        val clawMapping: Pair<GamepadKeys.Trigger, Int>,
        val wristMappingLeft: Pair<GamepadKeys.Button, Int>,
        val wristMappingRight: Pair<GamepadKeys.Button, Int>,
        val hangSpecMapping: Pair<GamepadKeys.Button, Int>,
        val climbUpMapping: Pair<GamepadKeys.Button, Int>,
        val climbDownMapping: Pair<GamepadKeys.Button, Int>,
        val hangLowSpecMapping: Pair<GamepadKeys.Button, Int>,
        val moveIntakeMapping: Pair<GamepadKeys.Trigger, Int>?
    )
}