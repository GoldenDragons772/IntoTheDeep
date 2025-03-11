package org.firstinspires.ftc.team772.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp(name = "Button Tester")
public class ButtonTuner extends LinearOpMode {
    DigitalChannel digitalTouch;  // Digital channel Object

    @Override
    public void runOpMode() {

        // get a reference to our touchSensor object.
        digitalTouch = hardwareMap.get(DigitalChannel.class, "back"); // we called it "back" since it was called that in config.

        digitalTouch.setMode(DigitalChannel.Mode.INPUT);
        telemetry.addData("DigitalTouchSensorExample", "Press start to continue...");
        telemetry.update();

        // wait for the start button to be pressed.
        waitForStart();

        // while the OpMode is active, loop and read the digital channel.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {

            // button is pressed if value returned is LOW or false.
            // send the info back to driver station using telemetry function.
            if (digitalTouch.getState() == false) {
                telemetry.addData("Button", "NOT PRESSED");
            } else {
                telemetry.addData("Button", "PRESSED");
            }

            telemetry.update();
        }
    }
}