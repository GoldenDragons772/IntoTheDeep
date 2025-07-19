package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class LConstants {
    static {

// Three Wheel odo
//        ThreeWheelConstants.forwardTicksToInches = 0.0009;
//        ThreeWheelConstants.strafeTicksToInches = -0.0011;
//        ThreeWheelConstants.turnTicksToInches = -0.0011;
//        ThreeWheelConstants.leftY = 6.62;
//        ThreeWheelConstants.rightY = -6.62;
//        ThreeWheelConstants.strafeX = 3.1;
//        ThreeWheelConstants.leftEncoder_HardwareMapName = "BLMotor";
//        ThreeWheelConstants.rightEncoder_HardwareMapName = "FLMotor";
//        ThreeWheelConstants.strafeEncoder_HardwareMapName = "BRMotor";
//        ThreeWheelConstants.leftEncoderDirection = Encoder.FORWARD;
//        ThreeWheelConstants.rightEncoderDirection = Encoder.REVERSE;
//        ThreeWheelConstants.strafeEncoderDirection = Encoder.FORWARD;

        PinpointConstants.forwardY = 2.5; //2.854;//4.54 - 0.5;
        PinpointConstants.strafeX = 8 - 0.3;//6.398 - 0.65;     //7+.666;
        PinpointConstants.distanceUnit = DistanceUnit.INCH;
        PinpointConstants.hardwareMapName = "pinpoint";
        PinpointConstants.useYawScalar = true;
        PinpointConstants.yawScalar = 0.984;
        //PinpointConstants.useCustomEncoderResolution = true;
        PinpointConstants.encoderResolution = GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;
        //PinpointConstants.customEncoderResolution = 37.251;
        PinpointConstants.forwardEncoderDirection = GoBildaPinpointDriver.EncoderDirection.REVERSED;
        PinpointConstants.strafeEncoderDirection = GoBildaPinpointDriver.EncoderDirection.FORWARD;
    }
}




