package com.digquant.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val TAG = DateUtil::class.java.simpleName
    const val DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm"
    const val DATETIME_FORMAT_PATTERN2 = "yyyy/MMdd HH:mm"
    const val DATETIME_FORMAT_PATTERN_WITH_SECOND = "yyyy-MM-dd HH:mm:ss"
    fun format(time: Long, pattern: String?): String? {
        val simpleDateFormat = SimpleDateFormat()
        return if (time > 0) {
            val date = Date(time)
            simpleDateFormat.applyLocalizedPattern(pattern)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+8")
            simpleDateFormat.format(date)
        } else {
            null
        }
    }

    fun parse(timeText: String?, pattern: String?): Long {
        val simpleDateFormat = SimpleDateFormat()
        simpleDateFormat.applyPattern(pattern)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+8")
        try {
            return simpleDateFormat.parse(timeText).time
        } catch (e: ParseException) {

        }
        return 0L
    }

    fun isToday(time: Long): Boolean {
        val sf = SimpleDateFormat("yyyyMMdd")
        val nowDay = sf.format(Date())
        //对比的时间
        val day = sf.format(Date(time))
        return day == nowDay
    }

    /**
     * 毫秒转秒
     *
     * @param millisecond
     * @return
     */
    fun millisecondToMinutes(millisecond: Long): Long {
        val time = millisecond / (60 * 1000)
        return Math.max(1, time)
    }
}
