package org.firstinspires.ftc.teamcode.helpers

import com.arcrobotics.ftclib.command.*
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.SpecimenCommand
import org.firstinspires.ftc.teamcode.implementation.commands.TransferSampleCommand
import org.firstinspires.ftc.teamcode.implementation.commands.ToggleIntakeCommand

/**
 * Manages driving and button mappings for TeleOps.
 */
class DriveManager(hw: HardwareMap, telemetry: Telemetry, gp1: Gamepad, gp2: Gamepad, mapping: Mapping) {
    /**
     * Subsystems
     */
    val root: RootSystem = RootSystem(hw, telemetry, false);

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
        if(gamepad1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.0){
            root.teleOpDriveScaled(
                -gamepad1.rightX,
                gamepad1.rightY,
                -gamepad1.leftX
            )
        }else {
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


    /**
     * Binds a function to the trigger and runs the function (repeatedly) if the trigger is pushed more than the threshold.
     * Note that this is more akin to setHeldBinding than the other setPressedBinding
     */
    private fun setHeldTriggerBinding(
        map: Pair<GamepadKeys.Trigger, Int>, function: InstantCommand, onRelease: InstantCommand
    ) {
        var isDown = getGamepad(map.second).getTrigger(map.first) > 0.5
        var lastIsDown = isDown
        CommandScheduler.getInstance().schedule(RepeatCommand(InstantCommand({
            isDown = getGamepad(map.second).getTrigger(map.first) > 0.5
            if (isDown) function.schedule() else if (lastIsDown) onRelease.schedule()
            lastIsDown = isDown
        })))
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
     * This function reads the value of the trigger and sends it into the function
     * @param map The controller binding being passed in
     * @param function The function that will run when activated
     */
    private fun triggerReader(map: Pair<GamepadKeys.Trigger, Int>, function: (Double) -> Command) {
        //if(triggerPos > 0.2) {
        CommandScheduler.getInstance().schedule(
            RepeatCommand(
                InstantCommand({
                    //Get the position of the trigger.
                    var triggerPos = getGamepad(map.second).getTrigger(map.first)
                    //if(triggerPos > 0.2) {
                    function(triggerPos).schedule() //invoke the function that was originally referenced by passing in the position of the trigger.
                    //}
                })
            )
        )
        //}
    }

    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings(mapping: Mapping) {
        setPressedBinding(mapping.lowclimbMapping, root.climb.setTargetPosition(ClimbSystem.ClimbState.LOW_BASKET))
        setPressedBinding(mapping.highclimbMapping, root.climb.setTargetPosition(ClimbSystem.ClimbState.HIGH_BASKET))
        setPressedBinding(mapping.unClimbMapping, root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME))
        setPressedBinding(mapping.hangSpecMapping, SpecimenCommand(root.intake, root.outtake, root.climb))
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
        setPressedBinding(mapping.wristMappingLeft, root.intake.incrementWristLeft())
        setPressedBinding(mapping.wristMappingRight, root.intake.incrementWristRight())

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
    )
}