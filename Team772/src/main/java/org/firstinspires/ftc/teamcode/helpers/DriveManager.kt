package org.firstinspires.ftc.teamcode.helpers

import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.implementation.ClimbState
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import kotlin.reflect.KProperty

/**
 * Must a property that provides a float or boolean.
 */
typealias Pressable = KProperty<Any>

/**
 * Manages driving and button mappings for TeleOps.
 */
class DriveManager(
    hw: HardwareMap,
    telemetry: Telemetry,
    private val gamepad1: Gamepad,
    private val gamepad2: Gamepad,
    mapping: Mapping,
    isAllianceRed: Boolean
) {
    /**
     * Subsystems
     */
    val root: RootSystem = RootSystem(hw, telemetry, false, isSpecAuto = false, isAllianceRed)
    private val bindings = mutableListOf<Binding>()

    /**
     * Controllers
     */
    init {
        initializeBindings(mapping)
        root.follower.startTeleopDrive()
    }


    /**
     * Start driving and processing input: Main drive loop.
     */
    fun update() {
        root.update() // Updates bulk reads and odometry.
        root.driveScale =
            if (gamepad1.left_trigger <= 0.0) Triple(1.0, 1.0, 0.8)
            else Triple(0.3, 0.3, 0.2)
        root.teleOpDrive(
            -gamepad1.right_stick_x,
            gamepad1.right_stick_y,
            -gamepad1.left_stick_x
        )
        updateRunBindings()
    }

    private fun getGamepad(id: UInt): Gamepad {
        assert(id in 1u..2u)
        return when (id) {
            1u -> gamepad1; 2u -> gamepad2; else -> TODO()
        }
    }

    private fun isPressed(map: Pressable): Boolean {
        val value = map.call()
        if (value is Float) return value > 0.0
        return value as Boolean
    }

    private fun registerBinding(
        mapping: Pressable,
        type: Binding.Type = Binding.Type.PRESSED,
        func: () -> Unit,
    ) {
        bindings.add(Binding({ isPressed(mapping) }, func, type))
    }

    private fun updateRunBindings() = runBlocking {
        for (i in bindings) {
            if (i.justTriggered && i.type == Binding.Type.PRESSED) continue
            if (i.shouldRun()) {
                launch { i.toRun() }
                i.justTriggered = true
            }
        }
    }


    /**
     * Take the bindings created in an OpMode and bind them to functions.
     */
    private fun initializeBindings(mapping: Mapping) {
        registerBinding(mapping.lowclimbMapping) { root.climb.climbState = ClimbState.LOW_BASKET }
        registerBinding(mapping.highclimbMapping) { root.climb.climbState = ClimbState.HIGH_BASKET }
        registerBinding(mapping.unClimbMapping) { root.climb.climbState = ClimbState.HOME }
        registerBinding(mapping.climbUpMapping) { root.climb.sendRawMotors(1.0) }
        registerBinding(mapping.climbDownMapping) { root.climb.sendRawMotors(-1.0) }
        registerBinding(mapping.hangSpecMapping) { suspend { root.intake.specimenCommandInverted() } }
        registerBinding(mapping.aimMapping) { suspend { root.intake.toggleIntake() } }
        registerBinding(mapping.transferMapping) { suspend { root.intake.transferSample() } }
        registerBinding(mapping.gripMapping) { root.intake.toggleClaw() }
        registerBinding(mapping.climbToHangSpec) { root.climb.climbState = ClimbState.HIGH_CHAMBER }
        registerBinding(mapping.clawMapping) { root.intake.toggleClaw() }
        registerBinding(mapping.wristMappingLeft, Binding.Type.HELD) { root.intake.wrist.incWrist(-0.2) }
        registerBinding(mapping.wristMappingRight, Binding.Type.HELD) { root.intake.wrist.incWrist(0.2) }
        if (mapping.moveIntakeMapping != null) {
            registerBinding(mapping.moveIntakeMapping)
            { suspend { root.intake.toggleHover() } }

        }
    }

    /**
     * Definition of possible mappings. Using this instead of a dictionary/hashmap allows for code completion.
     */
    class Mapping(
        val lowclimbMapping: Pressable,
        val highclimbMapping: Pressable,
        val unClimbMapping: Pressable,
        val aimMapping: Pressable,
        val gripMapping: Pressable,
        val transferMapping: Pressable,
        val climbToHangSpec: Pressable,
        val clawMapping: Pressable,
        val wristMappingLeft: Pressable,
        val wristMappingRight: Pressable,
        val hangSpecMapping: Pressable,
        val climbUpMapping: Pressable,
        val climbDownMapping: Pressable,
        val moveIntakeMapping: Pressable?
    )

    class Binding(val shouldRun: () -> Boolean, val toRun: () -> Unit, val type: Type) {
        enum class Type {
            HELD, PRESSED
        }

        var justTriggered = false
    }
}