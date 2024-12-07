package org.firstinspires.ftc.team772.implementation

import android.util.Log
import com.arcrobotics.ftclib.command.*
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

/**
 * Class that holds the outtake implementation and commands
 * @param hw The robot's hardwareMap
 * @property SubsystemBase extends the robot's subsystem
 */
class OuttakeSystem(hw: HardwareMap) : SubsystemBase() {

    //Defines servos
    private val swingServo: Servo = hw.get(Servo::class.java, "swingServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "wristServo")
    private val gripper: Servo = hw.get(Servo::class.java, "clawServo")

    //Other Utilites
    var gripState = true
        private set // Prevents setting outside of this class (Kotlin Moment).
    var swingState = false
        private set
    var wristState = true
        private set

    /**
     * Things that the robot needs to initialize upon start.
     */
    init {
        wristServo.position = Constants.WRIST_SERVO_HOME //Lock Wrist Servo
        swingServo.position = Constants.SWING_SERVO_INIT
    }

    /**
     * Command that swings the bot's outtake arm to home
     * @exception Command This class extends the FTCLib's commandbase to work in upper level programs.
     */
    fun swingToHome(): Command {
        return InstantCommand({
            Log.i("Big MEN", "${swingServo.position}")
            if (!swingState) return@InstantCommand;

            swingServo.position = Constants.SWING_SERVO_HOME;
            Log.i("ROBO", "Climbing")
            swingState = false
        })
    }

    /**
     * Swings the robot's arm to the target position
     */
    fun swingToTarget(): Command {
        return InstantCommand({
            Log.i("Outtake", "Point hit")
            if (swingState) return@InstantCommand
            swingServo.position = Constants.SWING_SERVO_TARGET
            swingState = true
        })
    }

    /**
     * Swings the robot's arm to the initialization position so that it doesn't get in the way of other things.
     */
    fun initializeSwing(): Command {
        return InstantCommand({
            swingServo.position  = Constants.SWING_SERVO_INIT
        })
    }

    /**
     * Locks the wrist in the home position to avoid it from moving.
     */
    fun wristHome(): Command {
        return InstantCommand({
            if (!wristState) return@InstantCommand //If the wrist isn't already in this position
            wristServo.position = Constants.WRIST_SERVO_HOME //Go to this position
            wristState = false //Finally, change the variable
        })
    }

    /**
     * Turns the wrist to a position for scoring specimen.
     */
    fun wristTurn(): Command {
        return InstantCommand({
            if (wristState) return@InstantCommand
            wristServo.position = Constants.WRIST_SERVO_TARGET
            wristState = true
        })
    }

    /**
     * Opens the outtake claw
     */
    fun unGrip(): Command {
        return InstantCommand({
            if (!gripState) return@InstantCommand
            gripper.position = Constants.UNGRIPPY;
            gripState = false
        })
    }

    //GYAAAAAAAAAAAAAT
    fun gripIt(): CommandBase {
        return InstantCommand({
            Log.i("Outtake", "Gripping")
            //if (gripState) return@InstantCommand  //We need to bypass this since the claw needs to contiuously grip onto the sample as it doesn't know when to and when not to.
            gripper.position = Constants.GRIPPY
            gripState = true
        })

    }

    /**
     * Ready to pick up a pixel: Ungripped, unturned, and swung down.
     * @exception SequentialCommandGroup Runs all the commands in a sequence.
     */
    fun goHome(): SequentialCommandGroup {
        return SequentialCommandGroup(
            swingToHome(),
            wristHome(),
            unGrip()
        )
        // Does nothing if it's already in the home position.
    }

    /**
     * Open and close the claw.
     */
    fun toggleGripper() = ConditionalCommand(unGrip(), gripIt()) { gripState } // ({if (gripState) unGrip() else gripIt()})

    /**
     * Pivot the servo that pivots the outtake.
     */
//    fun toggleSwing() = if (!swingState) swingToTarget() else swingToHome()
    fun toggleSwing() = ConditionalCommand(swingToHome(), swingToTarget()) {swingState}  // if swung, swing home. if unswuing (home), swing out


}

