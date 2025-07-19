package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import android.util.Log;

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

        FollowerConstants.mass = 12.519;

        FollowerConstants.xMovement = (81.719 + 80.890 + 80.382) / 3;
        FollowerConstants.yMovement = (64.62 + 64.68 + 64.32) / 3;

        FollowerConstants.forwardZeroPowerAcceleration = (-41.640 - 46.194 - 46.921) / 3;
        FollowerConstants.lateralZeroPowerAcceleration = (-78.681 - 75.491 - 73.338) / 3;

        FollowerConstants.useSecondaryTranslationalPID = false;
        FollowerConstants.useSecondaryHeadingPID = true;
        FollowerConstants.useSecondaryDrivePID = false;

        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.1,0,0.002,0);
        //FollowerConstants.translationalPIDFFeedForward = 0.05;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.1,0,0.01,0); // Not being used, @see useSecondaryTranslationalPID
        FollowerConstants.secondaryTranslationalPIDFFeedForward = 0.005;

        FollowerConstants.headingPIDFCoefficients.setCoefficients(7,0,0.31,0);
        //FollowerConstants.headingPIDFFeedForward = 0.01;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(3,0,0.15,0); // Not being used, @see useSecondaryHeadingPID
        //FollowerConstants.secondaryDrivePIDFFeedForward = 0.01;


        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.015,0,0.001,0.6,0);
        //FollowerConstants.drivePIDFFeedForward = 0.01;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.01,0,0.00055,0.6,0); // Not being used, @see useSecondaryDrivePID
        //FollowerConstants.secondaryDrivePIDFFeedForward = 0.0005;

        FollowerConstants.zeroPowerAccelerationMultiplier = 4;
        FollowerConstants.centripetalScaling = 0.0007;

        FollowerConstants.pathEndTimeoutConstraint = 50;
        FollowerConstants.pathEndTValueConstraint = 0.95;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;

        FollowerConstants.useVoltageCompensationInAuto = true;
        FollowerConstants.useVoltageCompensationInTeleOp = false;
        FollowerConstants.nominalVoltage = 13.5;
    }
}
