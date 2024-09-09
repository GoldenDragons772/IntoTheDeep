package org.firstinspires.ftc.team772.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.team772.abstractions.ControlSystem
import org.firstinspires.ftc.team772.autos.AutoPath
import org.firstinspires.ftc.team772.helpers.PathFollower
import org.firstinspires.ftc.team772.implementation.Constants

@Autonomous(name = "DummyAuto")
class DummyAuto : CommandOpMode() {

    private lateinit var robot: ControlSystem;
    private var selectedAuto = AutoPath.XYZDUMMYAUTO;
    private val pathFollower = PathFollower(hardwareMap)

    /**
     * Use the controller to select the autonomous.
     */
    private fun selectAuto(): AutoPath {
        // TODO: actually select different autos
        return AutoPath.XYZDUMMYAUTO
    }

    /**
     * Run the selected autonomous.
     */
    private fun runAuto() {
        pathFollower.followPath(selectedAuto)
    }

    /**
     * Select, then run the auto.
     */
    override fun initialize() {
        /*
         * auto = selectAuto()
         * runAuto(auto)
         */
        selectedAuto = selectAuto()
        runAuto()
    }

    override fun run() {
        this.pathFollower.update()
    }

}