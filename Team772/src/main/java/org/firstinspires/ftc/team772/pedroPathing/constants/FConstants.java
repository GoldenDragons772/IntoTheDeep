package org.firstinspires.ftc.team772.pedroPathing.constants;

import com.pedropathing.localization.Localizers;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.util.CustomFilteredPIDFCoefficients;
import com.pedropathing.util.CustomPIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

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

        FollowerConstants.mass = 13.38;

        FollowerConstants.xMovement = (77.45327207140595 + 78.93323198626484 + 79.35819681782496) / 3;
        FollowerConstants.yMovement = (52.115180766276616 + 47.98902129861951 + 50.71643725370416) / 3;

        FollowerConstants.forwardZeroPowerAcceleration = (-38.34462918953741 - 36.85483461261909 - 37.7281066498576) / 3;
        FollowerConstants.lateralZeroPowerAcceleration = (-74.87095702573642 -72.11748855034618 - 81.42476927122401) / 3;

        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.4,0,0.03,0);
        FollowerConstants.useSecondaryTranslationalPID = false;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.1,0,0.01,0); // Not being used, @see useSecondaryTranslationalPID

        FollowerConstants.headingPIDFCoefficients.setCoefficients(3,0,0.19,0);
        FollowerConstants.useSecondaryHeadingPID = false;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(2,0,0.1,0); // Not being used, @see useSecondaryHeadingPID

        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.025,0,0,0,0);
        FollowerConstants.useSecondaryDrivePID = false;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.02,0,0,0.6,0); // Not being used, @see useSecondaryDrivePID

        FollowerConstants.zeroPowerAccelerationMultiplier = 4;
        FollowerConstants.centripetalScaling = 0.0006;

        FollowerConstants.pathEndTimeoutConstraint = 100;
        FollowerConstants.pathEndTValueConstraint = 0.995;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;

        FollowerConstants.useVoltageCompensationInAuto = true;
        FollowerConstants.nominalVoltage = 13;
    }
}
