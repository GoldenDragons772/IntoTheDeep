package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import org.firstinspires.ftc.teamcode.implementation.RootSystem;

// ~ should always sort last alphabetically
@TeleOp(name = "~Button Tester")
public class ButtonTuner extends LinearOpMode {
    DigitalChannel digitalTouch;  // Digital channel Object

    @Override
    public void runOpMode() {
        RootSystem root = new RootSystem(hardwareMap, telemetry, false);

        // get a reference to our touchSensor object.
        digitalTouch = hardwareMap.get(DigitalChannel.class, "outLimitSwitch"); // we called it "back" since it was called that in config.
        digitalTouch.setMode(DigitalChannel.Mode.INPUT);


//        root.getTelemetry().addData("DigitalTouchSensorExample", "Press start to continue...");
//        root.getTelemetry().update();

        waitForStart();

        // while the OpMode is active, loop and read the digital channel.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (opModeIsActive()) {

            // button is pressed if value returned is LOW or false.
            // send the info back to driver station using telemetry function.
            telemetry.addData("Button", (digitalTouch.getState()) ? "PRESSED" : "NOT PRESSED");
            telemetry.update();
        }
    }
}