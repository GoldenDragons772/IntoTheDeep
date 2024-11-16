package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class OuttakeSystem(hw: HardwareMap) : SubsystemBase() {

    //Defines servos
    private val swingServo: Servo = hw.get(Servo::class.java, "swingServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "wristServo")
    private val gripper: Servo = hw.get(Servo::class.java, "clawServo")

    //Other Utilites
    var gripState = false
        private set // Prevents setting outside of this class.
    var swingState = false
        private set
    var wristState = false
        private set

    fun swingToHome() {
        swingServo.position = Constants.SWING_SERVO_HOME
        swingState = false
    }

    fun swingToTarget() {
        swingServo.position = Constants.SWING_SERVO_TARGET
        swingState = true
    }

    fun wristHome() {
        wristServo.position = Constants.WRIST_SERVO_HOME
        wristState = false
    }

    fun wristTurn() {
        wristServo.position = Constants.WRIST_SERVO_TARGET
        wristState = true
    }

    fun unGrip() {
        gripper.position = Constants.UNGRIPPY;
        gripState = false
    }

    //GYAAAAAAAAAAAAAT
    fun gripIt() {
        gripper.position = Constants.GRIPPY
        gripState = true
    }

    /**
     * Ready to pick up a pixel: Ungripped, unturned, and swung down.
     */
    fun goHome() {
        if (swingState) swingToHome()
        if (wristState) wristHome()
        if (gripState) unGrip()
        // Does nothing if it's already in the home position.
    }

    /**
     * Open and close the claw.
     */
    fun toggleGripper() = if (!gripState) gripIt() else unGrip()

    /**
     * Pivot the servo that pivots the outtake.
     */
    fun toggleSwing() = if (!swingState) swingToTarget() else swingToHome()


}