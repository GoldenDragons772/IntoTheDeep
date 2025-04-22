package org.firstinspires.ftc.teamcode.helpers

import com.pedropathing.follower.Follower
import com.pedropathing.pathgen.PathChain
import kotlinx.coroutines.*
import org.firstinspires.ftc.teamcode.implementation.RootSystem

object Util {
    suspend fun waitUntilDoneFollowing(root: RootSystem) {
        while (root.follower.isBusy) yield()
    }
@JvmName("_blockPath")
    suspend fun blockPath(
        follower: Follower,
        path: PathChain,
        maxPower: Double,
        holdEnd: Boolean = false
    ): Job =
        coroutineScope {
            return@coroutineScope try {
                launch {
                    follower.followPath(path, maxPower, holdEnd)
                    while (true) {
                        if (follower.currentPathNumber == (path.size() - 1).toDouble() && kotlin.math.abs(follower.headingError) < 0.1) {
                            if (follower.currentTValue >= 0.99) break
                        }
                    }
                }
            } finally {
                follower.breakFollowing()
            }
        }

    @JvmName("_interruptOn")
    suspend fun interruptOn(job: Job, supplier: () -> Boolean): Job = coroutineScope {
        launch {
            while (job.isActive) {
                if (supplier()) job.cancel()
            }
        }
        return@coroutineScope job
    }

    suspend fun Follower.blockPath(path: PathChain, maxPower: Double, holdEnd: Boolean = false): Job =
        blockPath(this@blockPath, path, maxPower, holdEnd)

    suspend fun Job.interruptOn(supplier: () -> Boolean): Job = interruptOn(this@interruptOn, supplier)
    suspend fun Job.timeout(time: Long): Job = withTimeout(time) { return@withTimeout this@timeout }
    fun Double.clamp(low: Number, high: Number): Double {
        if (this < low.toDouble()) return low.toDouble()
        else if (this > high.toDouble()) return high.toDouble()
        return this
    }
}