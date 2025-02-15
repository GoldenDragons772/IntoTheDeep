package org.firstinspires.ftc.team772.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Climb Tuner")
public class ClimbTuner extends LinearOpMode {


    private DcMotorEx climb2;

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotorEx climb1 = hardwareMap.get(DcMotorEx.class, "climbMotorUp");
        DcMotorEx climb2 = hardwareMap.get(DcMotorEx.class, "climbMotorDown");

        waitForStart();

        while(opModeIsActive()) {
            climb1.setPower(gamepad1.left_stick_y);
            climb2.setPower(gamepad1.right_stick_y);

        }
    }
}
