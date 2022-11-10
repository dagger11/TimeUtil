package com.ppk.dinger.timeutil

import com.ppk.dinger.timeutil.CountUpFormat.Custom
import com.ppk.dinger.timeutil.CountUpFormat.FullDate
import com.ppk.dinger.timeutil.CountUpFormat.FullDate24
import com.ppk.dinger.timeutil.CountUpFormat.InDay
import com.ppk.dinger.timeutil.CountUpFormat.InHour
import com.ppk.dinger.timeutil.CountUpFormat.InMin
import com.ppk.dinger.timeutil.Format.Full
import java.util.concurrent.TimeUnit

abstract class InfiniteCountUp {

    private var from: Long = 0L
    private var timePassed = 0L
    private var interval: Long = 0
    private lateinit var onTick: OnTick
    private lateinit var infiniteCountUpTimer: InfiniteCountUpTimer
    private lateinit var customFormat: String

    /**
     * infinite count up from 0
     * can be used as well as stop watch
     */
    fun infiniteCountUp(interval: Long, countUpFormat: CountUpFormat): InfiniteCountUp {
        infiniteCountUp(0, interval, countUpFormat)
        return this
    }

    /**
     * infinite count up from desired [startTime]
     * example: starting from [11.11.2022 11:11:11 AM], count up infinitely every second
     */
    fun infiniteCountUp(
        startTime: Long,
        interval: Long,
        countUpFormat: CountUpFormat
    ): InfiniteCountUp {
        from = startTime
        this.interval = interval
        setUpTimer(startTime, countUpFormat)
        return this
    }

    fun infiniteCountUp(startTime: Long, interval: Long, customFormat: String): InfiniteCountUp {
        from = startTime
        this.customFormat = customFormat
        this.interval = interval
        setUpTimer(startTime, Custom)
        return this
    }

    private fun setUpTimer(startTime: Long, countUpFormat: CountUpFormat) {
        when (countUpFormat) {
            InMin -> countUpMin(startTime)
            InHour -> countUpHour(startTime)
            InDay -> countUpDay(startTime)
            FullDate -> countUpFullDate(startTime)
            FullDate24 -> countUpFullDate24(startTime)
            Custom -> countUpCustom(startTime)
        }
    }


    private fun countUpMin(startTime: Long) {
        runTimer(startTime, object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedTime = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(remainTime % 60),
                    TimeUnit.MILLISECONDS.toSeconds(remainTime % 60)
                )
                onTicked(formattedTime, remainTime)
            }
        })
    }

    private fun countUpHour(startTime: Long) {
        runTimer(startTime, object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedTime = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(remainTime % 60),
                    TimeUnit.MILLISECONDS.toMinutes(remainTime % 60),
                    TimeUnit.MILLISECONDS.toSeconds(remainTime % 60)
                )
                onTicked(formattedTime, remainTime)
            }
        })
    }

    private fun countUpDay(startTime: Long) {
        runTimer(startTime, object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedTime = String.format(
                    "%02d Days %02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toDays(remainTime % 24),
                    TimeUnit.MILLISECONDS.toHours(remainTime % 60),
                    TimeUnit.MILLISECONDS.toMinutes(remainTime % 60),
                    TimeUnit.MILLISECONDS.toSeconds(remainTime % 60)
                )
                onTicked(formattedTime, remainTime)
            }
        })
    }

    private fun countUpFullDate(startTime: Long) {
        runTimer(startTime, object : OnTick {
            override fun onTick(remainTime: Long) {
                onTicked(TimeUtil.timestampToDate(remainTime, Full), remainTime)
            }
        })
    }

    private fun countUpFullDate24(startTime: Long) {
        runTimer(startTime, object : OnTick {
            override fun onTick(remainTime: Long) {
                onTicked(TimeUtil.timestampToDate(remainTime, Full), remainTime)
            }
        })
    }

    private fun countUpCustom(startTime: Long) {
        runTimer(startTime, object : OnTick {
            override fun onTick(remainTime: Long) {
                onTicked(TimeUtil.timestampToDate(remainTime, customFormat), remainTime)
            }
        })
    }

    private fun runTimer(startTime: Long, onTick: OnTick) {
        this.onTick = onTick
        infiniteCountUpTimer = object : InfiniteCountUpTimer(startTime, interval) {
            override fun onTick(passedTime: Long) {
                onTick.onTick(passedTime)
            }

            override fun onFinish() {
                onFinished()
            }

            override fun onPause(passedTime: Long) {
                onPaused(passedTime)
            }
        }
    }

    fun start() {
        infiniteCountUpTimer.start()
    }

    fun resume() {
        infiniteCountUpTimer.resume()
    }

    fun pause() {
        infiniteCountUpTimer.pause()
    }

    fun stop() {
        infiniteCountUpTimer.stop()
    }

    abstract fun onTicked(formattedTime: String, passedTime: Long)
    abstract fun onPaused(passedTime: Long)
    abstract fun onFinished()
}

