package org.firstinspires.ftc.teamcode.helpers

import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.Telemetry

object AllianceSelector {
    /**
     * Returns isRed boolean -- false is blue.
     */
    fun selectAlliance(gp1: Gamepad, telemetry: Telemetry): Boolean {
        telemetry.addLine("* * * * * * * * * * * * * * * * * * * * ")
        telemetry.addLine("SELECT ALLIANCE on DPAD <-RED-- --BLUE->")
        telemetry.addLine("* * * * * * * * * * * * * * * * * * * * ")
        telemetry.update()
        var returnValue: Boolean
        while (true) {
            if (gp1.dpad_left) {
                returnValue = true; break
            } else if (gp1.dpad_right) {
                returnValue = false; break
            }
        }
        telemetry.addLine("Using ${if (returnValue) "RED" else "BLUE"} alliance.")
        telemetry.update()
        return returnValue
    }
}