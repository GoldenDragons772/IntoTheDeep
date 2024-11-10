package org.firstinspires.ftc.team772.implementation

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class OuttakeSystem(hw: HardwareMap): SubsystemBase() {
    //Defines servos
    private val swingServo: Servo = hw.get(Servo::class.java, "swingServo")
    private val wristServo: Servo = hw.get(Servo::class.java, "wristServo")
    private val gripper: Servo = hw.get(Servo::class.java, "clawServo")

    //Other Utilites
    private var gripState = false
    private var swingState = false

    fun swingToHome(){
        swingServo.position = Constants.SWING_SERVO_HOME
    }

    fun swingToTarget(){
        swingServo.position = Constants.SWING_SERVO_TARGET
    }

    fun wristHome(){
        wristServo.position = Constants.WRIST_SERVO_HOME
    }

    fun wristTurn(){
        wristServo.position = Constants.WRIST_SERVO_TARGET
    }

    fun unGrip(){
        gripper.position = Constants.UNGRIPPY;
    }

    //GYAAAAAAAAAAAAAT
    fun gripIt(){
        gripper.position = Constants.GRIPPY
    }

    fun toggleGripper(){

        if(!gripState){
            gripIt()
        }else{
            unGrip()
        }

        gripState = !gripState

    }

    fun toggleSwing(){

        if(!swingState){
            swingToTarget()
        }else{
            swingToHome()
        }

        swingState = !swingState

    }


}