package com.funkytwig.tasktimer

import java.util.Date

private const val TAG = "TimingXX"

class Timing(val taskId: Long, val startTime: Long = Date().time / 1000, var id: Long = 0) {
    var duration: Long = 0
        private set // privet setter but public getter (can only be set internaly)

    fun setDuration() {
        // duration is set to duration from startTime to current time
        duration = (Date().time / 1000) - startTime
    }

    override fun toString(): String {
        return "Timing: taskId: $taskId, startTime: $startTime, duration: $duration"
    }
}