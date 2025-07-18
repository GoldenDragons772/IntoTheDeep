package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

// ~ should always sort last alphabetically
@TeleOp(name = "~Climb Tuner")
public class ClimbTuner extends LinearOpMode {

    @Override
    public void runOpMode() {

        DcMotorEx climb1 = hardwareMap.get(DcMotorEx.class, "climbMotorUp");
        DcMotorEx climb2 = hardwareMap.get(DcMotorEx.class, "climbMotorDown");
        DcMotorEx climb3 = hardwareMap.get(DcMotorEx.class, "climbMotor3");

        climb3.setDirection(DcMotorSimple.Direction.REVERSE);
        climb2.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            climb1.setPower(gamepad1.left_stick_y);
            climb2.setPower(gamepad1.left_stick_y);
            climb3.setPower(gamepad1.left_stick_y);

        }
    }
}
