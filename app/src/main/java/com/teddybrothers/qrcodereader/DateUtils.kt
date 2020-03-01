package com.teddybrothers.qrcodereader

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getLocalTime(dateFormat: String): String {
        val localTime = Date()
        val converter: DateFormat =
            SimpleDateFormat(dateFormat, Locale.ENGLISH)
        return converter.format(localTime)
    }
}