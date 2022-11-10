package com.ppk.dinger.timeutil

enum class Format {
    Full,
    Full24,
    DayMonthYear,
    Days,
    Daily,

    /**
     * use only [TimeUtil.timestampToDate] and [TimeUtil.dateToTimestamp]was used
     * work only with custom format string
     */
    Custom
}

enum class CountDownFormat {
    FromDay,    //from days to 0 sec
    FromHour,   //from hour to 0 sec
    FromMinute, //from min to 0 sec
    FromSecond,  //from sec to milli sec

}

enum class CountUpFormat {
    InMin,  //format in 00:00
    InHour, //format in 00:00:00
    InDay,  //format in 000 Days 00:00:00
    FullDate,   //format in "DD/MM/YYYY hh:mm:ss a"
    FullDate24, //format in "DD/MM/YYYY hh:mm:ss"

    /**
     * use only [CountUp.countUp] was used
     * work only with custom format string
     */
    Custom
}

