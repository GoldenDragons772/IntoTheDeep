package org.firstinspires.ftc.teamcode.implementation;

import com.acmerobotics.dashboard.FtcDashboard;
import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.*;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.helpers.LogState;
import org.firstinspires.ftc.teamcode.vision.SampleDetection;
import org.jetbrains.annotations.NotNull;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.HashMap;

/**
 * IntakeSystem is a subsystem that manages the intake mechanism of the robot.
 * It controls the servos for linkage, strike, pivot, wrist, and claw, and provides methods to set their positions.
 * The system also integrates with a camera for sample detection.
 */
@Config
public class IntakeSystem extends SubsystemBase implements LogState {

    // Set Positions for Linkage
    public static double LINKAGE_HOME = 0, LINKAGE_TARGET = 0.42, LINKAGE_HALF = 0.23;
//    public static double RIGHT_LINKAGE_HOME = 0, RIGHT_LINKAGE_TARGET = 0.45, RIGHT_LINKAGE_HALF = 0.23;

    // Set Positions for Strike Servos
    public static double STRIKE_HOME = 0.35, STRIKE_TARGET = 0.931, STRIKE_TRANSFER = 0.78, STRIKE_HOVER = 0.84;

    static WristPosition wristState = WristPosition.HOME;
    // Set Positions for main pivot
    public static double PIVOT_HOME = 0.5, PIVOT_TARGET = 0.29, PIVOT_TRANSFER = 0.9;

    // Set Positions for Wrist
    public static double WRIST_HOME = 0.35, WRIST_TARGET = 1.0, WRIST_ANGLE = 0.85, wristPos = 0.64, WRIST_ANGLE_BUCKET = 0.5, WRIST_INC = 0.02;

    // Set Positions for claw
    public static double CLAW_HOME = 1.0, CLAW_TARGET = 0.73, CLAW_STROKE = 0.5;

    //State stuff
    static IntakePosition extendState = IntakePosition.HOME;
    static LinkagePosition linkageState = LinkagePosition.HOME;
    static boolean clawState = false;
    public IntakePosition pivotPosition = IntakePosition.HOME;

    @Override
    @NotNull
    public String stateString() {
        return String.format("INTAKE SYSTEM Extend: %s Linkage %s Claw %s Pivot %s", extendState.name(), linkageState.name(), clawState, pivotPosition.name());
    }

    /**
     * ValueCache is a class that holds the current linkage position.
     * It is used to store the linkage position for later use, e.g., in periodic updates.
     */
    public class ValueCache {
        public double linkagePosition;
    }

    //Boolean to keep track of if were in auto.
    boolean isAuto;

    /**
     * Enum representing the different positions of the intake system.
     * Each position corresponds to a specific servo position for the intake mechanism.
     */
    public enum IntakePosition {
        HOME,
        TARGET,
        TRANSFER,
        HOVER
    }

    /**
     * Enum representing the different positions of the linkage system.
     * Each position corresponds to a specific servo position for the linkage mechanism.
     */
    public enum LinkagePosition {
        HOME,
        FULL,
        HALF
    }

    /**
     * Enum representing the different positions of the wrist servo.
     * Each position corresponds to a specific servo position for the wrist mechanism.
     */
    public enum WristPosition {
        HOME,
        TARGET,
        ANGLE,
        ANGLE_BUCKET
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
    private double lastRotation = 0;

    /**
     * Constructor for the IntakeSystem class.
     * Initializes the servos and camera, and sets their initial positions.
     *
     * @param root       The root system that contains hardware and telemetry.
     * @param isAuto     Indicates if the system is in autonomous mode.
     * @param isSpecAuto Indicates if the subsystem needs to be specimen auto mode.
     */
    public IntakeSystem(RootSystem root, boolean isAuto, boolean isSpecAuto) {
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

        rightLinkageServo.setPosition(LINKAGE_HOME);
        leftLinkageServo.setPosition(LINKAGE_HOME);

        pivotServo.setPosition(PIVOT_HOME);

        if (!isAuto || !isSpecAuto) {
            this.moveToHome().schedule();

/*
            setLinkage(LINKAGE_HOME);


            clawServo.setPosition(CLAW_HOME);

            leftStrikeServo.setPosition(STRIKE_HOME);
            rightStrikeServo.setPosition(STRIKE_HOME);

            pivotServo.setPosition(PIVOT_HOME + 0.2);
            setWrist(WristPosition.HOME);
*/
        }

        this.isAuto = isAuto;

        WebcamName webcamName = root.getHw().get(WebcamName.class, "GDVision");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName);

        sampleDetector = new SampleDetection(root.getTelemetry(), root.isAllianceRed());
        Log.i("Camera", "Started with color" + root.isAllianceRed());

        Log.i("Camera", "Before camera initialization");
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                Log.i("Camera", "Started streaming");
                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT, OpenCvWebcam.StreamFormat.MJPEG);
                camera.setPipeline(sampleDetector);
                FtcDashboard.getInstance().startCameraStream(camera, 100.0);
//                camera.getGainControl().setGain(Constants.CAMERA_GAIN);
//                camera.pauseViewport(); // have it paused by default.
            }

            @Override
            public void onError(int i) {
            }

        });
    }

    @Override
    public void periodic() {
        if (!isAuto) {

            root.getTelemetry().addData("extendState", extendState.toString());
            root.getTelemetry().addData("pivotPosition", pivotPosition.toString());
            root.getTelemetry().addData("linkageState", linkageState.toString());
            if (pivotPosition == IntakePosition.HOME || pivotPosition == IntakePosition.HOVER && sampleDetector.sampleRotation.get() != -70.0 && !clawState) {
                visionWristRotation();
            }
        }
    }

    /**
     * Uses the vision system to set the wrist servo position based on the detected sample rotation.
     * This method calculates the servo position based on the sample rotation and updates the wrist servo accordingly.
     */
    public void visionWristRotation() {
        double rotationValue = sampleDetector.sampleRotation.get();
        double inputValue = ((rotationValue) / Math.PI + 0.5) % 1;
        if (inputValue < 0) inputValue += 1;
        setWrist(inputValue * Constants.VISION_SERVO_MULTIPLIER).schedule();
        wristPos = inputValue;
        root.getTelemetry().addData("Theta --", rotationValue);
        root.getTelemetry().addData("Rotation", inputValue);
    }

    public Command setWristRotation(Double rot) {
        double inputValue = (rot / Math.PI + 0.5) % 1;
        if (inputValue < 0) inputValue += 1;
        return setWrist(inputValue * Constants.VISION_SERVO_MULTIPLIER);
    }

    /**
     * Gets the current wrist position.
     * This method returns the current state of the wrist system, which can be HOME, TARGET, ANGLE, or ANGLE_BUCKET.
     *
     * @return The current wrist position as a WristPosition enum.
     */
    public WristPosition getWristPos() {
        return wristState;
    }

    /**
     * Gets the current intake position.
     * This method returns the current state of the intake system, which can be HOME, TARGET, TRANSFER, or HOVER.
     *
     * @return The current intake position as an IntakePosition enum.
     */
    public IntakePosition getIntakePos() {
        return extendState;
    }

    /**
     * Gets the current pivot position.
     * This method returns the current state of the pivot system, which can be HOME, TARGET, TRANSFER, or HOVER.
     *
     * @return The current pivot position as an IntakePosition enum.
     */
    public IntakePosition getPivotPosition() {
        return pivotPosition;
    }

    /**
     * Gets the current linkage position.
     * This method returns the current state of the linkage system, which can be HOME, FULL, or HALF.
     *
     * @return The current linkage position as a LinkagePosition enum.
     */
    public LinkagePosition getLinkagePos() {
        return linkageState;
    }

    /**
     * Sets the linkage extension in from the centroid of the robot (the point about which it rotates without moving).
     * Theoretically allows for picking up a sample based on its distance from the center of the robot.
     * DOES NOT UPDATE THE LINKAGE STATE!
     * Accounts for strike offset
     */
    public Command setLinkageExtension(double inches) {
        // +Z distance from the robot (Facing)
        double centroid_distance = 7.0; // must be measured manually
        double strike_offset = 4.3;
        double length = inches - centroid_distance - strike_offset;
        double adjustedLength = horizontalSlideExtensionConversion(length);
        return setLinkage(adjustedLength);
    }

    /**
     * @param desiredLength the desired length of the slides in inches (between zero and the maximum length, in this case 15 inches).
     * @return a command
     */
    public double horizontalSlideExtensionConversion(double desiredLength) {
        double adjustedLength = desiredLength + 3.92904;
        Log.i("Vision", "Adjusted " + adjustedLength);
        double angle = Math.acos(adjustedLength / (2 * Constants.LINKAGE_LENGTH));
        assert Constants.LINKAGE_TARGET_ANGLE < angle && angle < Constants.LINKAGE_HOME_ANGLE : String.format("%f, %f", desiredLength, angle); //
        double servoOutput = angleToLinkageServo(angle);
        assert 0.0 < servoOutput && servoOutput < LINKAGE_TARGET : String.format("%f, %f, %f", servoOutput, desiredLength, angle);
        return servoOutput;
    }

    /**
     * Calculates the horizontal slide extension based on the current linkage position.
     * This method computes the extension of the horizontal slide using the current linkage position and defined constants.
     *
     * @return The calculated horizontal slide extension in inches.
     */
    public double getHorizontalSlideExtension() {
        double lowerBound = Constants.LINKAGE_HOME_ANGLE;
        double angleIntervalWidth = (Constants.LINKAGE_TARGET_ANGLE - lowerBound);
        double currentServoRatio = this.valueCache.linkagePosition / LINKAGE_TARGET;
        return (2 * Constants.LINKAGE_LENGTH * Math.cos(currentServoRatio * angleIntervalWidth + lowerBound)) - 3.92904;
        // 2l*cos((1-current/max) * (max_angle - min_angle) + min_angle)
    }

    /**
     * Converts an angle in radians to a servo position for the linkage.
     * This method calculates the servo position based on the angle and the defined constants for the linkage.
     *
     * @param angle The angle in radians to convert to a servo position.
     * @return The calculated servo position for the linkage.
     */
    public double angleToLinkageServo(double angle) {
        // 0.46 * (78 - 12) + 12
        // s * (m_1 - m_0) + m_0 = a
        // (a - m_0) / (m_1 - m_0)
        return LINKAGE_TARGET * ((angle - Constants.LINKAGE_HOME_ANGLE) / (Constants.LINKAGE_TARGET_ANGLE - Constants.LINKAGE_HOME_ANGLE));
    }

    public double getCentroidX() {
        return sampleDetector.centroid.get().x;
    }

    /**
     * Sets the linkage servo position based on the specified LinkagePosition.
     * This method uses an InstantCommand to set both the left and right linkage servos to the specified position.
     *
     * @param pos The desired linkage position, which can be HOME, FULL, or HALF.
     * @return A command that sets the linkage servo position.
     */
    public Command setLinkage(LinkagePosition pos) {

        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                setLinkage(LINKAGE_HOME).schedule();
                linkageState = LinkagePosition.HOME;
            });

            case FULL -> new InstantCommand(() -> {
                setLinkage(LINKAGE_TARGET).schedule();
                linkageState = LinkagePosition.FULL;
            });

            case HALF -> new InstantCommand(() -> {
                setLinkage(LINKAGE_HALF).schedule();
                linkageState = LinkagePosition.HALF;
            });

        };

    }

    /**
     * Sets the linkage servo position to a specific value.
     * This method uses an InstantCommand to set both the left and right linkage servos to the specified position.
     *
     * @param pos The desired linkage position, which should be between 0.0 and LEFT_LINKAGE_TARGET.
     * @return A command that sets the linkage servo position.
     */
    public Command setLinkage(Double pos) {
        assert 0.0 <= pos && pos <= LINKAGE_TARGET : pos;
        Log.i("Linkage", pos.toString());
        return new InstantCommand(() -> {
            valueCache.linkagePosition = pos;
            leftLinkageServo.setPosition(pos);
            rightLinkageServo.setPosition(pos);
        }
        );
    }

    /**
     * Sets the strike servo position based on the specified IntakePosition.
     * This method uses an InstantCommand to set the strike servo to a specific position.
     *
     * @param pos The desired strike position, which can be HOME, TARGET, TRANSFER, or HOVER.
     * @return A command that sets the strike servo position.
     */
    public Command setStrike(IntakePosition pos) {
        return switch (pos) {
            case HOME -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(STRIKE_HOME);
                rightStrikeServo.setPosition(STRIKE_HOME);
            });
            case TARGET -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(STRIKE_TARGET);
                rightStrikeServo.setPosition(STRIKE_TARGET);
            });
            case TRANSFER -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(STRIKE_TRANSFER);
                rightStrikeServo.setPosition(STRIKE_TRANSFER);
            });
            case HOVER -> new InstantCommand(() -> {
                leftStrikeServo.setPosition(STRIKE_HOVER);
                rightStrikeServo.setPosition(STRIKE_HOVER);
            });
        };

    }

    /**
     * Sets the pivot servo position based on the specified IntakePosition.
     * This method uses an InstantCommand to set the pivot servo to a specific position.
     *
     * @param pos The desired pivot position, which can be HOME, TARGET, TRANSFER, or HOVER.
     * @return A command that sets the pivot servo position.
     */
    public Command setPivot(IntakePosition pos) {
        return switch (pos) {
            case HOME, HOVER -> new InstantCommand(() -> {
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
        };
    }

    /**
     * Sets the wrist servo position based on the specified WristPosition.
     * This method uses an InstantCommand to set the wrist servo to a specific position.
     *
     * @param pos The desired wrist position, which can be HOME, TARGET, ANGLE, or ANGLE_BUCKET.
     * @return A command that sets the wrist servo position.
     */
    public Command setWrist(WristPosition pos) {
        switch (pos) {
            case HOME -> {
                return new InstantCommand(() -> {
                    wristState = WristPosition.HOME;
                }).andThen(setWrist(WRIST_HOME));
            }
            case TARGET -> {
                return new InstantCommand(() -> {
                    wristState = WristPosition.TARGET;
                }).andThen(setWrist(WRIST_TARGET));
            }
            case ANGLE -> {
                return new InstantCommand(() -> {
                    wristState = WristPosition.ANGLE;
                }).andThen(setWrist(WRIST_ANGLE));
            }
            case ANGLE_BUCKET -> {
                return new InstantCommand(() -> {
                    wristState = WristPosition.ANGLE_BUCKET;
                }).andThen(setWrist(WRIST_ANGLE_BUCKET));
            }
        }

        return null;
    }

    public Command setWrist(double pos) {
        assert 0.0 <= pos && pos <= 1.0;
        return new InstantCommand(() -> {
            wristPos = pos;
            Log.i("CMDS", "setWrist(" + pos + ")\n" + this.stateString());
            this.wristServo.setPosition(wristPos);
        });
    }

    /**
     * Sets the wrist servo position BASED ON THE CURRENT POSITION
     * This method uses an InstantCommand to set the wrist servo to a specific position.
     *
     * @param pos The desired wrist position, which can be a positive or negative value.
     * @return A command that sets the wrist servo position.
     */
    public Command incWrist(double pos) {
        return new InstantCommand(() -> {
            Log.i("CMDS", "incWrist(" + pos + ")\n" + this.stateString());

            wristPos += pos;

            wristPos %= 1.0;
            if (wristPos > 1.0) {
                wristPos = 1.0;
            } else if (wristPos < 0.0) {
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

    /**
     * Sets the claw servo position based on the specified position.
     * This method uses an InstantCommand to set the claw servo to either the home or target position.
     * TARGET is closed, HOME is open
     *
     * @param pos The desired claw position, which can be HOME, TARGET, TRANSFER, or HOVER.
     * @return A command that sets the claw servo position.
     */
    public Command setClaw(IntakePosition pos) {
        return switch (pos) {
            case HOME, HOVER -> new InstantCommand(() -> {
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

    /**
     * Moves the intake system to the home position.
     * This method sets the pivot, linkage, strike, and wrist positions to their home values.
     * It also resets the extend state to HOME.
     *
     * @return A command that moves the intake system to the home position.
     */
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

    /**
     * Moves the intake system to the transfer position.
     * This method sets the pivot, linkage, strike, and wrist positions to their transfer values.
     * It also disables the sample detector and stops the camera streaming.
     *
     * @return A command that moves the intake system to the transfer position.
     */
    public Command moveToTransfer() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    extendState = IntakePosition.TRANSFER;
//                    camera.stopStreaming();
                    sampleDetector.isEnabled.set(false);
                }),
                setWrist(WristPosition.HOME),
                setLinkage(LinkagePosition.HOME),
                setStrike(IntakePosition.TRANSFER),
                setPivot(IntakePosition.TRANSFER)
        );
    }

    /**
     * Moves the intake system to the target position for picking up samples.
     * This method sets the pivot, linkage, strike, and wrist positions to their target values.
     * It also enables the sample detector for sample detection.
     *
     * @return A command that moves the intake system to the target position.
     */
    public Command moveToTarget() {
        return new SequentialCommandGroup(
                new SelectCommand(
                        new HashMap<>() {{
                            // BUG: this selector is redundant because the subsequent commands override the linkage orders.
                            put(LinkagePosition.HOME,
                                    setLinkage(LinkagePosition.FULL)

                                            .andThen(hoverIntake()));
                            put(LinkagePosition.FULL,
                                    setLinkage(LinkagePosition.HOME)
                                            .andThen(moveToTransfer()));
                            put(LinkagePosition.HALF,
                                    setLinkage(LinkagePosition.HOME)
                                            .andThen(moveToTransfer()));
                        }},
                        this::getLinkagePos
                ),

                new InstantCommand(() -> {
                    sampleDetector.isEnabled.set(true);
                })
        );
    }

    /**
     * Toggles the intake system between home and target positions.
     * This method uses a SelectCommand to switch between the two intake positions based on the current state.
     *
     * @return A command that toggles the intake system position.
     */
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

    /**
     * Toggles the intake system between hover and target positions.
     * This method uses a SelectCommand to switch between the two intake positions based on the current state.
     *
     * @return A command that toggles the intake system position.
     */
    public Command toggleHover() {
        return new SelectCommand(
                new HashMap<>() {{
                    put(IntakePosition.HOVER, strikeIntake());
                    put(IntakePosition.TARGET, hoverIntake());
                }},
                this::getPivotPosition
        );
    }

    /**
     * Moves the intake system to the hover position.
     * This method sets the pivot, linkage, strike, and wrist positions to their hover values.
     *
     * @return A command that moves the intake system to the hover position.
     */
    public Command hoverIntake() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    Log.i("CMDS", "hoverIntake()\n" + this.stateString());
                }),
                setStrike(IntakePosition.HOVER),
                new WaitCommand(50),
                setPivot(IntakePosition.HOME),
                new InstantCommand(() -> {
                    pivotPosition = IntakePosition.HOVER;
                    //extendState = IntakePosition.TARGET;
                })
        );

    }

    /**
     * Moves the intake system to the target position for picking up samples.
     * This method sets the pivot, linkage, strike, and wrist positions to their target values.
     *
     * @return A command that moves the intake system to the target position.
     */
    public Command strikeIntake() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    Log.i("CMDS", "strikeIntake()\n" + this.stateString());
                }),
                setPivot(IntakePosition.TARGET),
                new WaitCommand(50),
                setStrike(IntakePosition.TARGET)
        );
    }

    /**
     * Toggles the claw servo position between two states: HOME and TARGET.
     * This method uses a ConditionalCommand to switch between the two claw positions based on the current state.
     *
     * @return A command that toggles the claw servo position.
     */
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

    /**
     * Toggles the wrist servo position between three states: HOME, TARGET, and ANGLE.
     * This method uses a SelectCommand to switch between the different wrist positions based on the current state.
     *
     * @return A command that toggles the wrist servo position.
     */
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

    /**
     * Increment the wrist servo position to the left.
     * This method is used to adjust the wrist position in a negative direction.
     *
     * @return A command that sets the wrist servo position to the left.
     */
    public Command incrementWristLeft() {
        return incWrist(-WRIST_INC);
    }

    /**
     * Increment the wrist servo position to the right.
     * This method is used to adjust the wrist position in a positive direction.
     *
     * @return A command that sets the wrist servo position to the right.
     */
    public Command incrementWristRight() {
        return incWrist(WRIST_INC);
    }
}