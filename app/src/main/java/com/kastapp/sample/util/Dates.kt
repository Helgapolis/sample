package com.kastapp.sample.util

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object Dates {

    private const val PATTERN_SIMPLE_DATE = "dd.MM.yyyy"

    fun toSimpleDate(date: Long?): String {
        date ?: return ""
        return SimpleDateFormat(PATTERN_SIMPLE_DATE, Locale.getDefault()).format(date)
    }

    fun simpleDateToLong(str: String): Long? {
        return try {
            if (str.length == 10) {
                SimpleDateFormat(PATTERN_SIMPLE_DATE, Locale.getDefault()).parse(str)?.time
            } else null
        } catch (ignore: Exception) {
            null
        }
    }
}
