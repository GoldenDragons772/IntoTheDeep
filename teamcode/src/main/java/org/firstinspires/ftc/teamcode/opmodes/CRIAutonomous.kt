package org.firstinspires.ftc.teamcode.opmodes

import android.util.Log
import com.arcrobotics.ftclib.command.*
import com.pedropathing.commands.FollowPath
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathBuilder
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.auto.CRIAutoPath
import org.firstinspires.ftc.teamcode.implementation.ClimbSystem
import org.firstinspires.ftc.teamcode.implementation.IntakeSystem
import org.firstinspires.ftc.teamcode.implementation.OuttakeSystem
import org.firstinspires.ftc.teamcode.implementation.RootSystem
import org.firstinspires.ftc.teamcode.implementation.commands.AutoScoreSpecimenCommand
import org.firstinspires.ftc.teamcode.implementation.commands.HoldPointCommand
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

@Autonomous(name = "CRISpecimen")
class CRIAutonomous : CommandOpMode() {
    // Constants
    // Starting Position S
    // Holding Position H
    // Observation Position O
    // First sample position A
    // Second sample position B
    // Third Sample Position C

    // Derived values
    // Angle HA
    // Angle HB
    // Angle HC
    // Distance HA
    // Distance HB
    // Distance HC
    // Angle HO

    // Score preload
    // Move along adjacent
    // Extend linear slides
    // Grab close sample
    // Retract
    // Move sample
    // Extend
    // Deposit sample
    // Rotate
    // Extend
    // Grab middle sample
    // Grab
    // Deposit sample
    // Grab far sample
    // Deposit sample
    // Score preload
    // Score preload
    // Score preload
    fun Pose.face(other: Pose): Pose {
        val angle = atan2(other.y - this.y, other.x - this.x)
        return Pose(this.x, this.y, angle)
    }

    fun Pose.distance(other: Pose): Double {
        return sqrt((other.x - this.x).pow(2) + (other.y - this.y).pow(2))
    }

    fun evaluateNextCommand(index: Int, array: List<() -> Command>): Command {
        if (index >= array.size) return InstantCommand({})
        // Should hopefully act sequentially
        Log.i("Autonomous", "Declaring $index")
        return array[index].invoke()
            .andThen(InstantCommand({ evaluateNextCommand(index + 1, array).schedule() }))
    }


    override fun initialize() {
        val root = RootSystem(hardwareMap, telemetry, true, isSpecAuto = true)
        root.follower.setStartingPose(CRIAutoPath.start)
        root.follower.pose = CRIAutoPath.start
        root.follower.updatePose()

        // Must be called, then scheduled.
        // ex of a higher order function, i.e., a function which generates a function to be called later.
        // Must be in lambda to evaluate root.follower.pose at call time.
        // these are in closures/lambdas instead of functions so that they can capture their environments
        val preload = {
            // Stash in zone
            SequentialCommandGroup(

            )
        }


        val moveToSample = { samplePos: Pose, offset: Double ->
            root.update()
            val offsetPos = Pose(CRIAutoPath.hold.x, CRIAutoPath.hold.y + offset, 0.0)
            val position = offsetPos.face(samplePos)
            Log.i("Autonomous", "Move from ${root.follower.pose} to: $position")
            FollowPath(
                root.follower, PathBuilder().addPath(
                    BezierLine(root.follower.pose, position)
                ).setLinearHeadingInterpolation(root.follower.pose.heading, position.heading).build(),
                true
            )
                .andThen(InstantCommand({
                    Log.i("Autonomous", "Done moving to $position")
                }))
                .andThen(
                    InstantCommand({
                        root.update()
                        root.intake.setLinkageExtension(root.follower.pose.distance(samplePos))
                            .andThen(
                                root.intake.setWristRotation(root.follower.pose.heading) // may need to be rotated by 90 deg
                            )
                            .schedule()
                    })
                )
                .andThen(
                    WaitCommand(0), // TODO: determine empirically
                )
        }
        val pickupSample = { samplePos: Pose ->
            SequentialCommandGroup(
                root.intake.hoverIntake(),
                WaitCommand(800),
                root.intake.strikeIntake(),
                WaitCommand(300),
                root.intake.setClaw(IntakeSystem.IntakePosition.TARGET),
                WaitCommand(100), // TODO: determine empirically
                root.intake.moveToHome()
            )
            // Then, dispense

        }
        val dispenseSample = {
            val pos = CRIAutoPath.destHold.face(CRIAutoPath.dest)

            root.follower.updatePose()
            SequentialCommandGroup(
                FollowPath(
                    root.follower, PathBuilder().addPath(BezierLine(root.follower.pose, pos))
                        .setConstantHeadingInterpolation(pos.heading).build()
                ),
                InstantCommand({ Log.i("Autonomous", "Dispensing Sample") }),
                WaitCommand(100),
                root.intake.hoverIntake(),
                WaitCommand(100),
                root.intake.moveToTarget(),
                WaitCommand(350),
                root.intake.setClaw(IntakeSystem.IntakePosition.HOME),
                WaitCommand(100),
                root.intake.moveToHome(),
                WaitCommand(400)
            )
            // May need timeouts to account for the lack of servo feedback.
        }

        val score = {
            SequentialCommandGroup(
                FollowPath(root.follower, CRIAutoPath.playerToSub().build()),
                FollowPath(root.follower, CRIAutoPath.subToPlayer().build()),
            )
        }

        // closure that returns a command that evaluates all subclosures when executed,
        // which each themselves return commands which should theoretically be instantly executed
        // This evaluates the closures (which create commands) at execution time
        val win = {
            val cmds = mutableListOf<() -> Command>(
                // Drop preload
                { moveToSample(CRIAutoPath.sample1, -6.0) },
                { pickupSample(CRIAutoPath.sample1) },
                dispenseSample,

                { moveToSample(CRIAutoPath.sample2, 0.0) },
                { pickupSample(CRIAutoPath.sample2) },
                dispenseSample,

                { moveToSample(CRIAutoPath.sample3, 4.0) },
                { pickupSample(CRIAutoPath.sample3) },
                dispenseSample,
                // ~19 s
                score,
                score,
                score,
                score
            )
            InstantCommand({
                // For each command, evaluate the function and immediately schedule it after the completion of the first one.
                evaluateNextCommand(0, cmds).schedule()
            })
        }


        // Each command needs an instant command which evaluates everything at execution time.
        // Then score


        schedule(
            /*
                        root.outtake.clawClose(),
            */

            root.outtake.setStrike(OuttakeSystem.OuttakePosition.TARGET),
            root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
            root.intake.moveToHome(),
            root.intake.setClaw(IntakeSystem.IntakePosition.HOME),

            WaitUntilCommand(this::opModeIsActive),
            RunCommand({ root.update() }),
            SequentialCommandGroup(
                //Preload
                win(),
                // DEBUG RETURNER -- REMOVE FOR COMP!!
                WaitCommand(25000),
                InstantCommand({
                    root.intake.moveToHome().andThen(
                        HoldPointCommand(
                            root.follower,
                            Pose(CRIAutoPath.start.x + 2.0, CRIAutoPath.start.y, CRIAutoPath.start.heading)
                        ).withTimeout(2000).andThen(
                            InstantCommand({ this.requestOpModeStop() })
                        )
                    ).schedule()
                })
                /*

                                //Spec 2
                                root.outtake.clawClose(),
                                //WaitCommand(200),
                                scoreCommand,
                                FollowPath(root.follower, SpecimenAutoPaths.spec1(), true, 0.9),
                                WaitCommand(150),
                                root.outtake.clawOpen(),
                                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                                ParallelCommandGroup(
                                    FollowPath(root.follower, SpecimenAutoPaths.grab2(), true, 0.9),
                                    WaitCommand(600).andThen(
                                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                                        root.outtake.moveArmToHome(),
                                        root.intake.moveToHome(),
                                    )
                                ),
                                FollowPath(
                                    root.follower,
                                    SpecimenAutoPaths.lineGrab2(),
                                    false,
                                    0.6
                                ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500),
                //                // spec3
                                root.outtake.clawClose(),
                                //WaitCommand(200),
                                scoreCommand,
                                FollowPath(root.follower, SpecimenAutoPaths.spec2(), true, 0.9),
                                WaitCommand(150),
                                root.outtake.clawOpen(),
                                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                                ParallelCommandGroup(
                                    FollowPath(root.follower, SpecimenAutoPaths.grab3(), true, 0.9),
                                    WaitCommand(600).andThen(
                                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                                        root.outtake.moveArmToHome(),
                                        root.intake.moveToHome(),
                                    )
                                ),
                                FollowPath(
                                    root.follower,
                                    SpecimenAutoPaths.lineGrab3(),
                                    false,
                                    0.6
                                ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500),
                //                // spec4
                                root.outtake.clawClose(),
                                //WaitCommand(200),
                                scoreCommand,
                                FollowPath(root.follower, SpecimenAutoPaths.spec3(), true, 0.9),
                                WaitCommand(150),
                                root.outtake.clawOpen(),
                                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),

                                ParallelCommandGroup(
                                    FollowPath(root.follower, SpecimenAutoPaths.grab4(), true, 0.8),
                                    WaitCommand(600).andThen(
                                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                                        root.outtake.moveArmToHome(),
                                        root.intake.moveToHome(),
                                    )
                                ),
                                FollowPath(
                                    root.follower,
                                    SpecimenAutoPaths.lineGrab4(),
                                    false,
                                    0.6
                                ).interruptOn { root.outtake.getClawButtonState() }.withTimeout(1500),
                //                // spec5
                                root.outtake.clawClose(),
                                //WaitCommand(200),
                                scoreCommand,
                                FollowPath(root.follower, SpecimenAutoPaths.spec4(), true, 0.9),

                                WaitCommand(150),
                                root.outtake.clawOpen(),
                                root.outtake.setPivot(OuttakeSystem.OuttakePosition.SAFE),
                //                FollowPath(root.follower, SpecimenAutoPaths.park(), true, 1.0),

                                ParallelCommandGroup(
                                    FollowPath(root.follower, SpecimenAutoPaths.park(), true, 1.0),
                                    WaitCommand(600).andThen(
                                        root.climb.setTargetPosition(ClimbSystem.ClimbState.HOME),
                                        root.outtake.moveArmToHome(),
                                        root.intake.moveToHome(),
                                    )
                                ),
                */
            )
        )
    }

}