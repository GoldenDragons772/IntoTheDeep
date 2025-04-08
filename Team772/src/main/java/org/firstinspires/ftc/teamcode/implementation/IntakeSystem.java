package org.firstinspires.ftc.teamcode.implementation;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.*;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.SampleDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.HashMap;

@Config
public class IntakeSystem extends SubsystemBase {

    // Set Positions for Linkage
    public static double LEFT_LINKAGE_HOME = 0, LEFT_LINKAGE_TARGET = 0.46, LEFT_LINKAGE_HALF = 0.23;
    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_HALF = 0.23;

    // Set Positions for Strike Servos
    public static double LEFT_PIVOT_HOME = 0, LEFT_PIVOT_TARGET = 0.59, LEFT_PIVOT_TRANSFER = 0.5;
    public static double RIGHT_PIVOT_HOME = 0, RIGHT_PIVOT_TARGET = 0.59, RIGHT_PIVOT_TRANSFER = 0.5;

    static WristPosition wristState = WristPosition.HOME;
    // Set Positions for main pivot
    public static double PIVOT_HOME = 0.5, PIVOT_TARGET = 0.24, PIVOT_TRANSFER = 1.0;

    // Set Positions for Wrist
    public static double WRIST_HOME = 1.0, WRIST_TARGET = 0.67, WRIST_ANGLE = 0.85;

    // Set Positions for claw
    public static double CLAW_HOME = 1.0, CLAW_TARGET = 0.74, CLAW_STROKE = 0.5;

    //State stuff
    static IntakePosition extendState = IntakePosition.HOME;
    static LinkagePosition linkageState = LinkagePosition.HOME;
    static boolean clawState = false;

    public enum IntakePosition {
        HOME,
        TARGET,
        TRANSFER
    }

    public enum LinkagePosition {
        HOME,
        FULL,
        HALF
    }

    public enum WristPosition {
        HOME,
        TARGET,
        ANGLE
    }

    Servo leftLinkageServo, rightLinkageServo;
    Servo leftStrikeServo, rightStrikeServo;
    Servo pivotServo;
    Servo wristServo;
    Servo clawServo;
    SampleDetection sampleDetector;
    RootSystem root;


    public IntakeSystem(RootSystem root) {
        // Linkage Servo
        this.root = root;
        leftLinkageServo = root.getHw().get(Servo.class, "lLinkageServo");
        rightLinkageServo = root.getHw().get(Servo.class, "rLinkageServo");

        // strike servo
        leftStrikeServo = root.getHw().get(Servo.class, "hLeftStrike");
        rightStrikeServo = root.getHw().get(Servo.class, "hRightStrike");

        // Pivot Servo
        pivotServo = root.getHw().get(Servo.class, "hPivot");

        //Wrist Servo
        wristServo = root.getHw().get(Servo.class, "hSwivelServo");

        //Claw Servo
        clawServo = root.getHw().get(Servo.class, "intakeClawServo");

        rightLinkageServo.setDirection(Servo.Direction.REVERSE);
        rightStrikeServo.setDirection(Servo.Direction.REVERSE);
        wristServo.setDirection(Servo.Direction.REVERSE);

        // Set Default positions:
        leftLinkageServo.setPosition(LEFT_LINKAGE_HOME);
        rightLinkageServo.setPosition(RIGHT_LINKAGE_HOME);

        clawServo.setPosition(CLAW_HOME);

        leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
        rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);

        pivotServo.setPosition(PIVOT_HOME);
        wristServo.setPosition(WRIST_HOME);
        // TODO: Make the camera work in teleop
//        WebcamName webcamName = root.getHw().get(WebcamName.class, "GDVision");
//        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName);
//        sampleDetector = new SampleDetection(root.getTelemetry(), true);
    }

    @Override
    public void periodic() {
        // TODO: Make the camera work in teleop
//        if (extendState == IntakePosition.TARGET && sampleDetector.sampleRotation != -70.0) {
//            double rotationValue = sampleDetector.sampleRotation;
//            var inputValue = ((rotationValue) / Math.PI + 0.5) % 1;
//            if (inputValue < 0) inputValue += 1;
//            wristServo.setPosition(inputValue * Constants.VISION_SERVO_MULTIPLIER);
//            root.getTelemetry().addData("Theta --", rotationValue);
//            root.getTelemetry().addData("Rotation", inputValue);
//        }
    }

    public WristPosition getWristPos() {
        return wristState;
    }

    public IntakePosition getIntakePos() {
        return extendState;
    }

    public LinkagePosition getLinkagePos() { return linkageState; }

    public Command setLinkage(LinkagePosition pos) {

        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                leftLinkageServo.setPosition(LEFT_LINKAGE_HOME);
                rightLinkageServo.setPosition(RIGHT_LINKAGE_HOME);
                linkageState  = LinkagePosition.HOME;
            });

            case FULL -> new InstantCommand(() -> {
                leftLinkageServo.setPosition(LEFT_LINKAGE_TARGET);
                rightLinkageServo.setPosition(RIGHT_LINKAGE_TARGET);
                linkageState  = LinkagePosition.FULL;
            });

            case HALF -> new InstantCommand(() -> {
                leftLinkageServo.setPosition(LEFT_LINKAGE_HALF);
                rightLinkageServo.setPosition(RIGHT_LINKAGE_HALF);
                linkageState  = LinkagePosition.HALF;
            });

        };

    }

    public Command setLinkage(Double pos) {

        if (pos >= 0.2) {
            pos = 0.2;
        }

        var scaledPos = Math.sqrt(pos);

        return new InstantCommand(() -> {
            leftLinkageServo.setPosition(scaledPos);
            rightLinkageServo.setPosition(scaledPos);
        }
        );
    }

    public Command setStrike(IntakePosition pos) {
        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
                rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);
            });
            case TARGET -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(LEFT_PIVOT_TARGET);
                rightStrikeServo.setPosition(RIGHT_PIVOT_TARGET);
            });
            case TRANSFER -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(LEFT_PIVOT_TRANSFER);
                rightStrikeServo.setPosition(RIGHT_PIVOT_TRANSFER);
            });
        };

    }

    public Command setPivot(IntakePosition pos) {
        return switch (pos) {
            case HOME -> new InstantCommand(() -> pivotServo.setPosition(PIVOT_HOME));
            case TARGET -> new InstantCommand(() -> pivotServo.setPosition(PIVOT_TARGET));
            case TRANSFER -> new InstantCommand(() -> pivotServo.setPosition(PIVOT_TRANSFER));
        };
    }

    public Command setWrist(WristPosition pos) {
        switch (pos) {
            case HOME -> {
                return new InstantCommand(() -> {
                    wristServo.setPosition(WRIST_HOME);
                    wristState = WristPosition.HOME;
                });
            }
            case TARGET -> {
                return new InstantCommand(() -> {
                    wristState = WristPosition.TARGET;
                    wristServo.setPosition(WRIST_TARGET);
                });
            }
            case ANGLE -> {
                return new InstantCommand(() -> {
                    wristState = WristPosition.ANGLE;
                    wristServo.setPosition(WRIST_ANGLE);
                });
            }
        }

        return null;
    }

    public Command setClaw(IntakePosition pos) {
        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                clawState = false;
                clawServo.setPosition(CLAW_HOME);
            });

            case TARGET -> new InstantCommand(() -> {
                clawState = true;
                clawServo.setPosition(CLAW_TARGET);
            });

            case TRANSFER -> new InstantCommand(() -> {
                clawState = false;
                clawServo.setPosition(CLAW_STROKE);
            });
        };
    }

    public Command moveToHome() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    extendState = IntakePosition.HOME;
                }),
                setWrist(WristPosition.HOME),
                setLinkage(LinkagePosition.HOME),
                setStrike(IntakePosition.HOME),
                setPivot(IntakePosition.HOME)
        );
    }

    public Command moveToTransfer() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    extendState = IntakePosition.TRANSFER;
                    // TODO: Make the camera work in teleop
//                    camera.closeCameraDevice();
                }),
                setWrist(WristPosition.HOME),
                setLinkage(LinkagePosition.HOME),
                setStrike(IntakePosition.TRANSFER),
                setPivot(IntakePosition.TRANSFER)
        );
    }

    public Command moveToTarget() {
        return new SequentialCommandGroup(
                new SelectCommand(
                        new HashMap<>() {{
                            put(LinkagePosition.HOME, setLinkage(LinkagePosition.FULL).andThen(setClaw(IntakePosition.HOME),
                                    setStrike(IntakePosition.TARGET),
                                    setPivot(IntakePosition.TARGET)));
                            put(LinkagePosition.FULL, setLinkage(LinkagePosition.HALF).andThen(setClaw(IntakePosition.HOME),
                                    setStrike(IntakePosition.TARGET),
                                    setPivot(IntakePosition.TARGET)));
                            put(LinkagePosition.HALF, setLinkage(LinkagePosition.HOME).andThen(moveToTransfer()));
                        }},
                        this::getLinkagePos
                ),
                // TODO: Make the camera work in teleop.

//                new InstantCommand(() -> {
//                    extendState = IntakePosition.TARGET;
//                    camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
//                        @Override
//                        public void onOpened() {
//                            camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
//                            camera.setPipeline(sampleDetector);
//                            FtcDashboard.getInstance().startCameraStream(camera, 100.0);
//                        }
//
//                        @Override
//                        public void onError(int i) {
//                        }
//                    });
//                }),
                setLinkage(IntakePosition.TARGET),
                setClaw(IntakePosition.HOME),
                setStrike(IntakePosition.TARGET),
                setPivot(IntakePosition.TARGET)
        );
    }

    public Command toggleIntake() {
        return new SelectCommand(
                new HashMap<>() {{
                    put(IntakePosition.TRANSFER, moveToTarget());
                    put(IntakePosition.TARGET, moveToTransfer());
                }},
                this::getIntakePos
        );
    }

    public ConditionalCommand toggleClaw() {
        return new ConditionalCommand(
                setClaw(IntakePosition.HOME),
                setClaw(IntakePosition.TARGET),
                () -> {
                    clawState = !clawState;
                    return !clawState; // Return original val
                }
        );
    }

    public Command toggleWrist() {
        return new SelectCommand(
                new HashMap<>() {{
                    put(WristPosition.HOME, setWrist(WristPosition.TARGET));
                    put(WristPosition.ANGLE, setWrist(WristPosition.HOME)); //Each state must trigger the next one
                    put(WristPosition.TARGET, setWrist(WristPosition.ANGLE));
                }},
                this::getWristPos
        );//.andThen(new InstantCommand({{Log.i("IntakeSystem", wristState.toString())}}));
    }

}
