package org.firstinspires.ftc.team772.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Touch Button")
public class TestButton extends LinearOpMode {

    TouchSensor touchSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        touchSensor = hardwareMap.get(TouchSensor.class, "hardStop");

        waitForStart();

        while (opModeIsActive()) {
            if (touchSensor.isPressed()) {
                telemetry.addData("Touch Sensor", "Is Pressed");
            } else {
                telemetry.addData("Touch Sensor", "Is Not Pressed");
            }

            telemetry.update();
        }
    }
}
