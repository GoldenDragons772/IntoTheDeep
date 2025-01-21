package org.firstinspires.ftc.team772.pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;

public class LConstants {
    static {
        ThreeWheelConstants.forwardTicksToInches = 0.0009;
        ThreeWheelConstants.strafeTicksToInches = -0.0011;
        ThreeWheelConstants.turnTicksToInches = -0.0011;
        ThreeWheelConstants.leftY = 6.62;
        ThreeWheelConstants.rightY = -6.62;
        ThreeWheelConstants.strafeX = 3.1;
        ThreeWheelConstants.leftEncoder_HardwareMapName = "BLMotor";
        ThreeWheelConstants.rightEncoder_HardwareMapName = "FLMotor";
        ThreeWheelConstants.strafeEncoder_HardwareMapName = "BRMotor";
        ThreeWheelConstants.leftEncoderDirection = Encoder.FORWARD;
        ThreeWheelConstants.rightEncoderDirection = Encoder.REVERSE;
        ThreeWheelConstants.strafeEncoderDirection = Encoder.FORWARD;
    }
}




