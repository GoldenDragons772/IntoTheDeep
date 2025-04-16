package org.firstinspires.ftc.teamcode.implementation;

import com.acmerobotics.dashboard.FtcDashboard;
import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.*;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.SampleDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.HashMap;

@Config
public class IntakeSystem extends SubsystemBase {

    // Set Positions for Linkage
    public static double LEFT_LINKAGE_HOME = 0, LEFT_LINKAGE_TARGET = 0.46, LEFT_LINKAGE_HALF = 0.23;
//    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_HALF = 0.23;

    public static double BOTH_PIVOT_HOME, BOTH_PIVOT_TARGET = 0.59, BOTH_PIVOT_TRANSFER = 0.5;
    // Set Positions for Strike Servos
    public static double LEFT_PIVOT_HOME = 0, LEFT_PIVOT_TARGET = BOTH_PIVOT_TARGET, LEFT_PIVOT_TRANSFER = BOTH_PIVOT_TRANSFER; // best code practice for sure
    public static double RIGHT_PIVOT_HOME = 0, RIGHT_PIVOT_TARGET = BOTH_PIVOT_TARGET, RIGHT_PIVOT_TRANSFER = BOTH_PIVOT_TRANSFER;

    static WristPosition wristState = WristPosition.HOME;
    // Set Positions for main pivot
    public static double PIVOT_HOME = 0.5, PIVOT_TARGET = 0.27, PIVOT_TRANSFER = 1.0;

    // Set Positions for Wrist
    public static double WRIST_HOME = 0.35, WRIST_TARGET = 1.0, WRIST_ANGLE = 0.85, wristPos = 0.64, WRIST_INC = 0.1;

    // Set Positions for claw
    public static double CLAW_HOME = 1.0, CLAW_TARGET = 0.74, CLAW_STROKE = 0.5;

    //State stuff
    static IntakePosition extendState = IntakePosition.HOME;
    static LinkagePosition linkageState = LinkagePosition.HOME;
    static boolean clawState = false;
    public IntakePosition pivotPosition = IntakePosition.HOME;

    public class ValueCache {
        public double linkagePosition;
    }

    public enum IntakePosition {
        HOME,
        TARGET,
        TRANSFER,
        HOVER
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

    public Servo leftLinkageServo, rightLinkageServo;
    public Servo leftStrikeServo, rightStrikeServo;
    Servo pivotServo;
    Servo wristServo;
    Servo clawServo;
    public SampleDetection sampleDetector;
    RootSystem root;
    public OpenCvWebcam camera;
    public ValueCache valueCache;


    public IntakeSystem(RootSystem root, boolean isAuto) {
        // Linkage Servo
        this.root = root;
        valueCache = new ValueCache();
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

        if (!isAuto) {
//            leftLinkageServo.setPosition(LEFT_LINKAGE_HOME);
            setLinkage(LEFT_LINKAGE_HOME);
//            rightLinkageServo.setPosition(RIGHT_LINKAGE_HOME);

            clawServo.setPosition(CLAW_HOME);

            leftStrikeServo.setPosition(LEFT_PIVOT_HOME);
            rightStrikeServo.setPosition(RIGHT_PIVOT_HOME);

            pivotServo.setPosition(PIVOT_HOME);
            wristServo.setPosition(WRIST_HOME);
        }
        WebcamName webcamName = root.getHw().get(WebcamName.class, "GDVision");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName);

        sampleDetector = new SampleDetection(root.getTelemetry(), root.isAllianceRed());

        Log.i("Camera", "Before camera initialization");
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                Log.i("Camera", "Started streaming");
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT, OpenCvWebcam.StreamFormat.MJPEG);
                camera.setPipeline(sampleDetector);
                FtcDashboard.getInstance().startCameraStream(camera, 100.0);
//                camera.pauseViewport(); // have it paused by default.
            }

            @Override
            public void onError(int i) {
            }
        });
    }

    @Override
    public void periodic() {
        root.getTelemetry().addData("extendState", extendState.toString());
        root.getTelemetry().addData("pivotPosition", pivotPosition.toString());
        root.getTelemetry().addData("linkageState", linkageState.toString());
        //
        if (pivotPosition == IntakePosition.HOME || pivotPosition == IntakePosition.HOVER && sampleDetector.sampleRotation != -70.0 && !clawState) {
            visionWristRotation();
        }
    }
    public void visionWristRotation(){
        double rotationValue = sampleDetector.sampleRotation;
        var inputValue = ((rotationValue) / Math.PI + 0.5) % 1;
        if (inputValue < 0) inputValue += 1;
        wristServo.setPosition(inputValue * Constants.VISION_SERVO_MULTIPLIER);
        root.getTelemetry().addData("Theta --", rotationValue);
        root.getTelemetry().addData("Rotation", inputValue);
    }

    public WristPosition getWristPos() {
        return wristState;
    }

    public IntakePosition getIntakePos() {
        return extendState;
    }

    public IntakePosition getPivotPosition() {
        return pivotPosition;
    }

    public LinkagePosition getLinkagePos() {
        return linkageState;
    }

    /**
     * @param desiredLength the desired length of the slides in inches (between zero and the maximum length, in this case 15 inches).
     * @return a command
     */
    public double horizontalSlideExtensionConversion(double desiredLength) {
        double adjustedLength = desiredLength + 3.92904;
        Log.i("Vision", "Adjusted " + adjustedLength);
        double angle = Math.acos(adjustedLength / (2 * Constants.LINKAGE_LENGTH));
        assert Constants.LINKAGE_TARGET_ANGLE < angle && angle < Constants.LINKAGE_HOME_ANGLE : String.format("%f, %f",desiredLength, angle); //
        double servoOutput = angleToLinkageServo(angle);
        assert 0.0 < servoOutput && servoOutput < LEFT_LINKAGE_TARGET : String.format("%f, %f, %f",servoOutput, desiredLength, angle);
        return servoOutput;
    }

    public double getHorizontalSlideExtension() {
        double lowerBound = Constants.LINKAGE_HOME_ANGLE;
        double angleIntervalWidth = (Constants.LINKAGE_TARGET_ANGLE - lowerBound);
        double currentServoRatio = this.valueCache.linkagePosition / LEFT_LINKAGE_TARGET;
        return (2 * Constants.LINKAGE_LENGTH * Math.cos(currentServoRatio * angleIntervalWidth + lowerBound)) - 3.92904;
        // 2l*cos((1-current/max) * (max_angle - min_angle) + min_angle)
    }

    public double angleToLinkageServo(double angle) {
        // 0.46 * (78 - 12) + 12
        // s * (m_1 - m_0) + m_0 = a
        // (a - m_0) / (m_1 - m_0)
        return LEFT_LINKAGE_TARGET * ((angle - Constants.LINKAGE_HOME_ANGLE) / (Constants.LINKAGE_TARGET_ANGLE - Constants.LINKAGE_HOME_ANGLE));
    }

    public Command setLinkage(LinkagePosition pos) {

        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                setLinkage(LEFT_LINKAGE_HOME).schedule();
                linkageState = LinkagePosition.HOME;
            });

            case FULL -> new InstantCommand(() -> {
                setLinkage(LEFT_LINKAGE_TARGET).schedule();
                linkageState = LinkagePosition.FULL;
            });

            case HALF -> new InstantCommand(() -> {
                setLinkage(LEFT_LINKAGE_HALF).schedule();
                linkageState = LinkagePosition.HALF;
            });

        };

    }

    public Command setLinkage(Double pos) {
        assert 0.0 <= pos && pos <= LEFT_LINKAGE_TARGET : pos;
        Log.i("Linkage", pos.toString());
        return new InstantCommand(() -> {
            valueCache.linkagePosition = pos;
            leftLinkageServo.setPosition(pos);
            rightLinkageServo.setPosition(pos);
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
            case HOVER -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(LEFT_PIVOT_TRANSFER);
                rightStrikeServo.setPosition(RIGHT_PIVOT_TRANSFER);
            });
        };

    }

    public Command setPivot(IntakePosition pos) {
        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                pivotServo.setPosition(PIVOT_HOME);
                pivotPosition = pos;
            });
            case TARGET -> new InstantCommand(() -> {
                pivotServo.setPosition(PIVOT_TARGET);
                pivotPosition = pos;
            });
            case TRANSFER -> new InstantCommand(() -> {
                pivotServo.setPosition(PIVOT_TRANSFER);
                pivotPosition = pos;
            });
            case HOVER -> new InstantCommand(() -> {
                pivotServo.setPosition(PIVOT_HOME);
                pivotPosition = pos;
            });
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

    public Command setWrist(double pos) {
        return new InstantCommand(() -> {
            wristPos += pos;
            if (wristPos > 1.0) {
                wristPos = 0.3;
            } else if (wristPos < 0.3) {
                wristPos = 1.0;
            }
            wristServo.setPosition(wristPos);
            if (wristPos == WRIST_TARGET) {
                wristState = WristPosition.TARGET;
            } else if (wristPos == WRIST_ANGLE) {
                wristState = WristPosition.ANGLE;
            } else {
                wristState = WristPosition.HOME;
            }
            Log.i("Intake", String.valueOf(wristPos));
        });
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

            case HOVER -> new InstantCommand(() -> {
                clawState = false;
                clawServo.setPosition(CLAW_HOME);
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
                    camera.stopStreaming();
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
                            put(LinkagePosition.HOME,
                                    setLinkage(LinkagePosition.FULL)
                                            .andThen(setClaw(IntakePosition.HOME),
                                                    hoverIntake()));
                            put(LinkagePosition.FULL,
                                    setLinkage(LinkagePosition.HALF)
                                            .andThen(setClaw(IntakePosition.HOME),
                                                    hoverIntake()));
                            put(LinkagePosition.HALF,
                                    setLinkage(LinkagePosition.HOME)
                                            .andThen(moveToTransfer()));
                        }},
                        this::getLinkagePos
                ),

                new InstantCommand(() -> {
                    camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT, OpenCvWebcam.StreamFormat.MJPEG);
                    camera.setPipeline(sampleDetector);
//                    camera.resumeViewport();
                })
//                setLinkage(IntakePosition.TARGET),
//                setClaw(IntakePosition.HOME),
//                setStrike(IntakePosition.TARGET),
//                setPivot(IntakePosition.TARGET)
        );
    }

    public Command toggleIntake() {
        return new SelectCommand(
                new HashMap<>() {{
                    put(IntakePosition.HOME, moveToTarget());
                    put(IntakePosition.TRANSFER, moveToTarget());
                    put(IntakePosition.TARGET, moveToTransfer());
                }},
                this::getIntakePos
        );
    }

    public Command toggleHover() {
        return new SelectCommand(
                new HashMap<>() {{
                    put(IntakePosition.HOVER, strikeIntake());
                    put(IntakePosition.TARGET, hoverIntake());
                }},
                this::getPivotPosition
        );
    }

    public Command hoverIntake() {
        return new SequentialCommandGroup(
                setStrike(IntakePosition.TRANSFER),
                new WaitCommand(150),
                setPivot(IntakePosition.HOME),
                new InstantCommand(() -> {
                    pivotPosition = IntakePosition.HOVER;
                })
        );
    }

    public Command strikeIntake() {
        return new SequentialCommandGroup(
                setPivot(IntakePosition.TARGET),
                new WaitCommand(150),
                setStrike(IntakePosition.TARGET),
                new InstantCommand(() -> {
                    pivotPosition = IntakePosition.TARGET;
                })
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

    public Command incrementWristLeft() {
        return setWrist(WRIST_INC);
    }

    public Command incrementWristRight() {
        return setWrist(-WRIST_INC);
    }

}
