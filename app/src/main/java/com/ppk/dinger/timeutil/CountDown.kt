package com.ppk.dinger.timeutil

import android.os.CountDownTimer

import androidx.annotation.NonNull
import com.ppk.dinger.timeutil.CountDownFormat.FromDay
import com.ppk.dinger.timeutil.CountDownFormat.FromHour
import com.ppk.dinger.timeutil.CountDownFormat.FromMinute
import com.ppk.dinger.timeutil.CountDownFormat.FromSecond

import java.util.concurrent.TimeUnit

/**
 * @author Pyae Phyo Kyaw
 */
abstract class CountDown {

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var customFormat: String
    private var totalCountDownTime: Long = 0L
    private var timeLeft: Long = 0L
    private var interval: Long = 1000L
    private lateinit var onTick: OnTick
    /**
     * =============================================================================================
     * =
     * =        Count Down Timer
     * =       with 4 count down types
     * =
     * =============================================================================================
     */

    /**
     * @param durationInMilliSec known duration for the specific interval with desired [countDownFormat]
     */
    fun countDown(
        @NonNull durationInMilliSec: Long,
        @NonNull interval: Long,
        @NonNull countDownFormat: CountDownFormat,
    ): CountDown {
        setUpCountDownTimer(durationInMilliSec, interval, countDownFormat)
        return this
    }

    /**
     * @param future the time want to start count down from
     * @param past the time want to end count down to with desired [countDownFormat]
     */
    fun countDown(
        future: Long, past: Long,
        interval: Long,
        countDownFormat: CountDownFormat
    ): CountDown {
        val diff: Long = future - past
        setUpCountDownTimer(diff, interval, countDownFormat)
        return this
    }

    private fun setUpCountDownTimer(
        duration: Long, interval: Long,
        countDownFormat: CountDownFormat
    ) {

        this.totalCountDownTime = duration
        when (countDownFormat) {
            FromDay -> countDownDay(duration, interval)
            FromHour -> countDownHour(duration, interval)
            FromMinute -> countDownMinute(duration, interval)
            FromSecond -> countDownSecond(duration, interval)

        }

    }

    private fun countDownDay(
        duration: Long, interval: Long,
    ) {
        countDownTimer(duration, interval, onTick = object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedRemainTime =
                    String.format(
                        "%02dDays %02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toDays(remainTime) % 24,
                        TimeUnit.MILLISECONDS.toHours(remainTime) % 60,
                        TimeUnit.MILLISECONDS.toMinutes(remainTime) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(remainTime) % 60
                    )
                onTicked(formattedRemainTime, remainTime)
            }

        })

    }

    private fun countDownHour(
        duration: Long, interval: Long
    ) {
        countDownTimer(duration, interval, onTick = object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedRemainTime =
                    String.format(
                        "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(remainTime) % 60,
                        TimeUnit.MILLISECONDS.toMinutes(remainTime) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(remainTime) % 60
                    )
                onTicked(formattedRemainTime, remainTime)
            }

        })
    }

    private fun countDownMinute(duration: Long, interval: Long) {
        countDownTimer(duration, interval, onTick = object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedRemainTime =
                    String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(remainTime) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(remainTime) % 60
                    )
                onTicked(formattedRemainTime, remainTime)
            }

        })
    }

    private fun countDownSecond(duration: Long, interval: Long) {
        countDownTimer(duration, interval, onTick = object : OnTick {
            override fun onTick(remainTime: Long) {
                val formattedRemainTime =
                    String.format(
                        "%02d",
                        TimeUnit.MILLISECONDS.toSeconds(remainTime) % 60
                    )
                onTicked(formattedRemainTime, remainTime)
            }

        })
    }

    private fun countDownTimer(duration: Long, interval: Long, onTick: OnTick) {
        this.onTick = onTick
        this.interval = interval
        countDownTimer = object :
            CountDownTimer(duration, interval) {
            override fun onTick(remainingTime: Long) {
                timeLeft = remainingTime
                onTick.onTick(remainingTime)
            }

            override fun onFinish() {
                onFinished()
            }
        }

    }

    fun start() {
        countDownTimer.start()
    }

    fun resume() {
        countDownTimer(timeLeft, interval, onTick)
    }

    fun pause() {
        countDownTimer.cancel()
        onPaused(totalCountDownTime - timeLeft, timeLeft)
    }

    fun stop() {
        countDownTimer.cancel()
        onFinished()
    }

    abstract fun onTicked(formattedRemainTime: String, remainTime: Long)
    abstract fun onPaused(passedTime: Long, timeLeft: Long)
    abstract fun onFinished()

}