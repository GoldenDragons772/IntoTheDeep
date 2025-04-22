package org.firstinspires.ftc.teamcode.implementation

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.arcrobotics.ftclib.kotlin.extensions.util.clamp
import com.pedropathing.follower.Follower
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants
import kotlin.math.pow
import kotlin.math.sign

/**
 * Root subsystem -- he's the guy in charge.
 */
class RootSystem(
    val hw: HardwareMap,
    rawTelemetry: Telemetry,
    isAuto: Boolean,
    val isSpecAuto: Boolean,
    var isAllianceRed: Boolean = false
) {
    val telemetry = MultipleTelemetry(rawTelemetry, FtcDashboard.getInstance().telemetry)

    private val hubs: MutableList<LynxModule> = hw.getAll(LynxModule::class.java)

    val outtake = OuttakeSystem(this, isAuto)
    val climb = ClimbSystem(this, isAuto)
    val intake = IntakeSystem(this, isAuto, isSpecAuto)
    val vision = VisionSystem(this)

    val voltageSensor: VoltageSensor = hw.voltageSensor.first()
    val follower = Follower(hw, FConstants::class.java, LConstants::class.java)
    var driveScale = Triple(1.0,1.0,0.8)

    private var lastVoltage: Double = 0.0
    var voltage: Double = 0.0 // It's unknown if this is necessary.
        private set

    init {
        hubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
        follower.setupConstants(FConstants::class.java, LConstants::class.java)
    }

    fun update() = runBlocking {
        hubs.forEach(LynxModule::clearBulkCache) // Bulk read
        lastVoltage = voltage
        voltage = voltageSensor.voltage

        launch { climb.periodic() }
        launch { intake.periodic() }
        launch { follower.update() } // Used for teleop therefore must be updated in all cases.
        launch {
            if (follower.isBusy) follower.telemetryDebug(telemetry) // calls telemetry.update()
            else telemetry.update()
        }
    }


    fun teleOpDrive(x: Number, y: Number, theta: Number) {
        x as Double; y as Double; theta as Double
        val xSquared = x.pow(2) * (x).sign * driveScale.first
        val ySquared = y.pow(2) * y.sign  * driveScale.second
        val thSquared = (theta.pow(2) * theta.sign) * driveScale.third
        this.follower.setTeleOpMovementVectors(-ySquared, xSquared, thSquared)
    }

    fun teleOpDriveScaled(x: Number, y: Number, theta: Number) {
        x as Double; y as Double; theta as Double
        val xSquared = (x.pow(2) * (x).sign) * 0.3
        val ySquared = (y.pow(2) * y.sign) * 0.3
        val thSquared = (theta.pow(2) * theta.sign) * 0.2
        this.follower.setTeleOpMovementVectors(-ySquared, xSquared, thSquared)
    }

    fun getVoltageMultiplier(): Double {
        val alpha = 0.8
        return (Constants.NOMINAL_BATTERY_VOLTAGE / (alpha * voltage + (1 - alpha) * lastVoltage)).clamp(0.0, 1.0)
    }

}