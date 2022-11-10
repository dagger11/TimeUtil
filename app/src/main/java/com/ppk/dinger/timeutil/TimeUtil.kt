package com.ppk.dinger.timeutil

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.text.format.DateFormat
import androidx.annotation.NonNull
import com.ppk.dinger.timeutil.Format.Custom
import com.ppk.dinger.timeutil.Format.Daily
import com.ppk.dinger.timeutil.Format.DayMonthYear
import com.ppk.dinger.timeutil.Format.Days
import com.ppk.dinger.timeutil.Format.Full
import com.ppk.dinger.timeutil.Format.Full24

import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

object TimeUtil {

    private lateinit var countDownTimer: CountDownTimer
    private var totalCountDownTime: Long = 0L
    private var timeLeft: Long = 0L
    private var interval: Long = 1000L
    private lateinit var onTick: OnTick
    private lateinit var customFormat: String

    /**
     * convert date to timestamp
     * @param format dateFormat for input [date] string
     */
    @SuppressLint("SimpleDateFormat")
    fun dateToTimestamp(@NonNull date: String, @NonNull format: Format): Long {
        return try {
            val formatter = SimpleDateFormat(map(format))
            val mDate = (formatter.parse(date) as Date)
            return mDate.time
        } catch (e: Exception) {
            0L
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateToTimestamp(@NonNull date: String, @NonNull customFormat: String): Long {
        return try {
            this.customFormat = customFormat
            val formatter = SimpleDateFormat(map(Custom))
            val mDate = (formatter.parse(date) as Date)
            return mDate.time
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * convert timestamp to date
     * @param format desired format of the date for the given [timeStamp]
     */
    fun timestampToDate(@NonNull timeStamp: Long, @NonNull format: Format): String {
        return try {
//            val calendar: Calendar = Calendar.getInstance()
//            calendar.timeInMillis = timeStamp
//            DateFormat.format(FormatToStringMapper.map(format),calendar).toString()
            DateFormat.format(map(format), timeStamp).toString()
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    fun timestampToDate(@NonNull timeStamp: Long, @NonNull customFormat: String): String {
        return try {
            this.customFormat = customFormat
            DateFormat.format(map(Custom), timeStamp).toString()
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun map(format: Format): String {
        return when (format) {
            Full -> "DD:MM:YYYY hh:mm:ss a"
            Full24 -> "DD:MM:YYYY hh:mm:ss"
            DayMonthYear -> "DD:MM:YYY"
            Days -> "DD hh:mm:ss"
            Daily -> "hh:mm:ss"
            Custom -> customFormat
        }
    }

    fun timestampDifference(future: Long, past: Long): Long {
        return abs(future - past)
    }

    fun dateDifference(date1: String, date2: String) {}

    fun timestampDifferenceInDate(timeStamp1: Long, timeStamp2: Long) {}

    fun lastSeen(timeStamp: Long) {}

    fun lastSeen(timeStamp1: Long, timeStamp2: Long) {}

}