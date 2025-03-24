package org.firstinspires.ftc.teamcode.helpers

class ControlHelper {
}

/**
 * A generic proportional-integral-derivative controller. Shamelessly ripped from last year.
 * @param kp the proportional term, provides the bulk of the output
 * @param kd the derivative term, decreases extreme outputs
 * @param ki the integral term, decreases steady-state error (usually unnecessary)
 */
class PIDController(private val kp: Double, private val ki: Double, private val kd: Double) {
    private var lastError: Double = 0.0
    private var lastTime: Long = 0
    fun tick(error: Double): Double {
        var i = 0.0
        val time = System.currentTimeMillis()

        val maxI = 1.0

        val p = kp * error
        i += ki * (error * (time - lastTime))

        if (i > maxI) {
            i = maxI
        }
        if (i < -maxI) {
            i = -maxI
        }

        val d = kd * (error - lastError) / (time - lastTime)
        lastError = error
        lastTime = time
        return d + i + p
    }
}