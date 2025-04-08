package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.pedropathing.localization.Localizers;
import com.pedropathing.follower.FollowerConstants;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.implementation.Constants;

public class FConstants {
    static {
        FollowerConstants.localizers = Localizers.PINPOINT;

        FollowerConstants.leftFrontMotorName = "FLMotor";
        FollowerConstants.leftRearMotorName = "BLMotor";
        FollowerConstants.rightFrontMotorName = "FRMotor";
        FollowerConstants.rightRearMotorName = "BRMotor";

        FollowerConstants.leftFrontMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.leftRearMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.rightFrontMotorDirection = DcMotorSimple.Direction.FORWARD;
        FollowerConstants.rightRearMotorDirection = DcMotorSimple.Direction.FORWARD;

        FollowerConstants.mass = 13.567;

        FollowerConstants.xMovement = (78.649 + 82.801 + 80.975) / 3;
        FollowerConstants.yMovement = (62.784 + 63.825 + 63.963) / 3;

        FollowerConstants.forwardZeroPowerAcceleration = (-30.693 - 33.370 - 28.782) / 3;
        FollowerConstants.lateralZeroPowerAcceleration = (-61.280 - 65.573 - 64.313) / 3;

        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.3,0,0.03,0);
        FollowerConstants.useSecondaryTranslationalPID = true;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.1,0,0.01,0); // Not being used, @see useSecondaryTranslationalPID
        FollowerConstants.secondaryTranslationalPIDFFeedForward = 0.0005;

        FollowerConstants.headingPIDFCoefficients.setCoefficients(8,0,0.19,0);
        FollowerConstants.useSecondaryHeadingPID = true;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(3,0,0.15,0); // Not being used, @see useSecondaryHeadingPID
        FollowerConstants.headingPIDFFeedForward = 0.01;
        FollowerConstants.secondaryDrivePIDFFeedForward = 0.01;


        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.025,0,0,0,0);
        FollowerConstants.useSecondaryDrivePID = true;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.02,0,0.00005,0.6,0); // Not being used, @see useSecondaryDrivePID
        FollowerConstants.drivePIDFFeedForward = 0.01;
        FollowerConstants.secondaryDrivePIDFFeedForward = 0.0005;

        FollowerConstants.zeroPowerAccelerationMultiplier = 6.5;
        FollowerConstants.centripetalScaling = 0.0006;

        FollowerConstants.pathEndTimeoutConstraint = 50;
        FollowerConstants.pathEndTValueConstraint = 0.95;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;

        FollowerConstants.useVoltageCompensationInAuto = false;
        FollowerConstants.useVoltageCompensationInTeleOp = false;
        FollowerConstants.nominalVoltage = Constants.NOMINAL_BATTERY_VOLTAGE;
    }
}
