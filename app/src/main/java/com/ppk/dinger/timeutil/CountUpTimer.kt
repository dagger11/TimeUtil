package com.ppk.dinger.timeutil

import android.os.CountDownTimer

/**
 * @author Pyae Phyo Kyaw
 */
internal abstract class CountUpTimer(private val duration: Long, interval: Long) :
    CountDownTimer(duration, interval) {

    override fun onTick(msUntilFinished: Long) {
        val timePassed = duration - msUntilFinished
        onTicks(timePassed, msUntilFinished)
    }

    override fun onFinish() {
        onFinishTimer()
    }

    abstract fun onTicks(timePassed: Long, timeRemain: Long)
    abstract fun onFinishTimer()
}
