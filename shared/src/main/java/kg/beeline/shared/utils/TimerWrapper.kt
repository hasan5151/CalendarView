/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kg.beeline.shared.utils

import java.util.*

/**
 * The timer used by the app is customizable. This way tests can run synchronously and very fast.
 *
 * See [DefaultTimer] for the default implementation and IntervalTimerViewModelTest.kt for a
 * test implementation.
 */
interface Timer {
    fun start(task: TimerTask, delay: Long, period: Long)
    fun cancel()
    fun getElapsedTime(): Long
    fun updatePausedTime()
    fun getPausedTime(): Long
    fun resetStartTime()
    fun resetPauseTime()
}

/**
 * The default timer is used in the normal execution of the app.
 */
class TimerImpl : Timer {

    companion object {
        private const val TIMER_PERIOD_MS = 1000L
    }

    private var timer = Timer()
    private var startTime = System.currentTimeMillis()
    private var pauseTime = 0L

    override fun start(task: TimerTask, delay: Long, period: Long) {
        timer = Timer()
        timer.scheduleAtFixedRate(task, delay, period)
    }

    override fun cancel() {
        timer.cancel()
    }

    override fun getPausedTime(): Long = pauseTime - startTime

    override fun getElapsedTime() = System.currentTimeMillis() - startTime

    override fun resetPauseTime() {
        pauseTime = System.currentTimeMillis()
    }

    override fun resetStartTime() {
        startTime = System.currentTimeMillis()
    }

    override fun updatePausedTime() {
        startTime += System.currentTimeMillis() - pauseTime
    }

}
