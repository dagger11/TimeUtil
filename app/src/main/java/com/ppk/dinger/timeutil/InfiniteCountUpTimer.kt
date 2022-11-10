package com.ppk.dinger.timeutil

import android.os.Handler
import android.os.Looper
import android.os.Message

internal abstract class InfiniteCountUpTimer(
    private val startTime: Long,
    interval: Long
) {

    private var isStopped = false
    private var resumeTime = 0L
    private var timePassed = 0L
    private var timeLeft = 0L
    private val MSG = 1

    /**
     * stop the timer
     */
    @Synchronized
    fun stop() {
        isStopped = true
        onFinish()
        mHandler.removeMessages(MSG)
    }

    /**
     * Start the timer
     */
    @Synchronized
    fun start() {
        isStopped = false
        resumeTime = startTime
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
    }

    fun pause() {
        isStopped = true
        onPause(resumeTime)
        mHandler.removeMessages(MSG)
    }

    fun resume() {
        isStopped = false
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
    }

    // handles infinite counting up
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this@InfiniteCountUpTimer) {
                if (isStopped)
                    return
                timePassed += interval
                resumeTime += timePassed
                onTick(resumeTime)
                sendMessageDelayed(
                    obtainMessage(MSG),
                    interval
                )
            }
        }
    }

    /**
     * Callback fired on regular interval.
     * @param passedTime The amount of time until finished.
     */
    abstract fun onTick(passedTime: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish()

    abstract fun onPause(passedTime: Long)
}


