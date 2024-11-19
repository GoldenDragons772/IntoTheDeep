package org.firstinspires.ftc.team772.implementation

import android.util.Log
import com.arcrobotics.ftclib.command.*
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class OuttakeSystem(hw: HardwareMap) : SubsystemBase() {

    //Defines servos
    private val swingServo: Servo = hw.get(Servo::class.java, "swingServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "wristServo")
    private val gripper: Servo = hw.get(Servo::class.java, "clawServo")

    //Other Utilites
    var gripState = true
        private set // Prevents setting outside of this class.
    var swingState = false
        private set
    var wristState = true
        private set

    fun swingToHome(): Command {
        return InstantCommand({
            Log.i("Big MEN", "${swingServo.position}")
            if (!swingState) return@InstantCommand;

            swingServo.position = Constants.SWING_SERVO_HOME;
            swingState = false
        })
    }

    fun swingToTarget(): Command {
        return InstantCommand({
            if (swingState) return@InstantCommand
            swingServo.position = Constants.SWING_SERVO_TARGET
            swingState = true
        })
    }

    fun wristHome(): Command {
        return InstantCommand({
            if (!wristState) return@InstantCommand
            wristServo.position = Constants.WRIST_SERVO_HOME
            wristState = false
        })
    }

    fun wristTurn(): Command {
        return InstantCommand({
            if (wristState) return@InstantCommand
            wristServo.position = Constants.WRIST_SERVO_TARGET
            wristState = true
        })
    }

    fun unGrip(): Command {
        return InstantCommand({
            if (!gripState) return@InstantCommand
            gripper.position = Constants.UNGRIPPY;
            gripState = false
        })

    }

    //GYAAAAAAAAAAAAAT
    fun gripIt(): Command {
        return InstantCommand({
            //if (gripState) return@InstantCommand  //We need to bypass this since the claw needs to contiuously grip onto the sample as it doesn't know when to and when not to.

            gripper.position = Constants.GRIPPY
            gripState = true
        })
    }

    /**
     * Ready to pick up a pixel: Ungripped, unturned, and swung down.
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

