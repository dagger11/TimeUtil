package com.ppk.dinger.timeutil


import com.ppk.dinger.timeutil.CountUpFormat.Custom
import com.ppk.dinger.timeutil.CountUpFormat.FullDate
import com.ppk.dinger.timeutil.CountUpFormat.FullDate24
import com.ppk.dinger.timeutil.CountUpFormat.InDay
import com.ppk.dinger.timeutil.CountUpFormat.InHour
import com.ppk.dinger.timeutil.CountUpFormat.InMin
import com.ppk.dinger.timeutil.Format.Full
import com.ppk.dinger.timeutil.Format.Full24
import java.util.concurrent.TimeUnit

/**
 * @author Pyae Phyo Kyaw
 * ===================================================================================================
 * =
 * =            Count Up Timer
 * =
 * ===================================================================================================
 */
abstract class CountUp {
    private lateinit var countUpTimer: CountUpTimer
    private var from: Long = 0L //special case for count up from specific date to specific date
    private var totalCountUpTime: Long = 0L //total count up time duration for finite count up
    private var timeLeft: Long = 0L //totalCountUptime - passed time
    private var passedTime: Long = 0L // passed time
    private var interval: Long = 1000L  //count up interval (Default is 1000 milliSec)
    private lateinit var onTick: OnTick
    private lateinit var customFormat: String

    /**
     * count up [from] timestamp [to] timestamp with custom [interval] and [countUpFormat]
     * example1: starting from [11.11.2022 11:11:11 AM] to [11.11.2022 11:11:11 PM]
     * example2: starting from 00:00:50 to 00:30:00 (like a timer)
     */
    fun countUp(from: Long, to: Long, interval: Long, countUpFormat: CountUpFormat): CountUp {
        val diff = from - to
        this.from = from
        setUpTimer(diff, interval, countUpFormat)
        return this
    }

    fun countUp(from: Long, to: Long, interval: Long, customFormat: String): CountUp {
        val diff = from - to
        this.from = from
        this.customFormat = customFormat
        setUpTimer(diff, interval, Custom)
        return this
    }

    private fun setUpTimer(duration: Long, interval: Long, countUpFormat: CountUpFormat) {
        when (countUpFormat) {
            InMin -> countUpMin(duration, interval)
            InHour -> countUpHour(duration, interval)
            InDay -> countUpDays(duration, interval)
            FullDate -> countUpFullDate(duration, interval)
            FullDate24 -> countUpFullDate24(duration, interval)
            Custom -> countUpCustom(duration, interval)
        }
    }

    private fun countUpMin(duration: Long, interval: Long) {
        countUpTimer(duration, interval, object : OnTick {
            //here remainTime means timePassed
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

    private fun countUpHour(duration: Long, interval: Long) {
        countUpTimer(duration, interval, object : OnTick {
            //here remainTime means timePassed
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

    private fun countUpDays(duration: Long, interval: Long) {
        countUpTimer(duration, interval, object : OnTick {
            //here remainTime means timePassed
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

    private fun countUpFullDate(duration: Long, interval: Long) {
        countUpTimer(duration, interval, object : OnTick {
            //here remainTime means timePassed
            override fun onTick(remainTime: Long) {
                //total time passed = from user wanted date + passed time
                val timePassed = from + remainTime
                onTicked(TimeUtil.timestampToDate(timePassed, Full), remainTime)
            }
        })
    }

    private fun countUpFullDate24(duration: Long, interval: Long) {
        countUpTimer(duration, interval, object : OnTick {
            //here remainTime means timePassed
            override fun onTick(remainTime: Long) {
                //total time passed = from user wanted date + passed time
                val timePassed = from + remainTime
                onTicked(TimeUtil.timestampToDate(timePassed, Full24), remainTime)
            }
        })
    }

    private fun countUpCustom(duration: Long, interval: Long) {
        countUpTimer(duration, interval, object : OnTick {
            //here remainTime means timePassed
            override fun onTick(remainTime: Long) {
                //total time passed = from user wanted date + passed time
                val timePassed = from + remainTime
                onTicked(TimeUtil.timestampToDate(timePassed, customFormat), remainTime)
            }
        })
    }

    private fun countUpTimer(duration: Long, interval: Long, onTick: OnTick) {
        this.onTick = onTick
        this.totalCountUpTime = duration
        countUpTimer = object : CountUpTimer(duration, interval) {
            override fun onTicks(timePassed: Long, timeRemain: Long) {
                timeLeft = timeRemain
                passedTime = timePassed
                onTick.onTick(timePassed)
            }

            override fun onFinishTimer() {
                onFinished()
            }
        }
    }

    fun start() {
        countUpTimer.start()
    }

    fun resume() {
        countUpTimer(timeLeft, interval, onTick)
    }

    fun pause() {
        countUpTimer.cancel()
        onPaused(passedTime, timeLeft)
    }

    fun stop() {
        countUpTimer.cancel()
        onFinished()
    }

    abstract fun onTicked(formattedTime: String, passedTime: Long)
    abstract fun onPaused(passedTime: Long, timeLeft: Long)
    abstract fun onFinished()
}