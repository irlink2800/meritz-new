package com.irlink.meritz.util

import com.irlink.meritz.util.extension.format
import com.irlink.meritz.util.extension.parseDate
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs

open class FormatUtil {

    companion object {
        const val TAG: String = "FormatUtil"
    }

    /**
     * 국제 번호를 지역 번호 형태로 변경.
     * @param internationalNumber 국제 전화 번호.
     * @param includeHyphen 하이픈을 포함한 형태로 변경할지에 대한 여부.
     * @param includeAsterisk 전화번호 마스킹 여부.
     */
    open fun toLocalPhoneNumber(internationalNumber: String?, includeHyphen: Boolean = false, includeAsterisk: Boolean = false): String {
        try {
            if (internationalNumber.isNullOrEmpty()) {
                return ""
            }
            var toLocalPhoneNumber: String = Pattern.compile("(^[+]82)|(^82)")
                .matcher(internationalNumber)
                .replaceAll("0")

            if (includeHyphen) {
                toLocalPhoneNumber = when (toLocalPhoneNumber.length) {
                    8 -> {
                        toLocalPhoneNumber.replaceFirst("^([0-9]{4})([0-9]{4})$".toRegex(), "$1-$2")
                    }
                    12 -> {
                        toLocalPhoneNumber.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$".toRegex(), "$1-$2-$3")
                    }
                    else -> {
                        toLocalPhoneNumber.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$".toRegex(), "$1-$2-$3")
                    }
                }
            }
            if (includeAsterisk) {
                val parts = toLocalPhoneNumber.split("-")
                var maskNumber = ""

                if (parts.size > 1) {
                    for ((i, part) in parts.withIndex()) {
                        maskNumber += if (i == parts.size - 1) {
                            "*".repeat(part.length)
                        } else {
                            "$part-"
                        }
                    }
                } else {
                    val length = toLocalPhoneNumber.length
                    val half = length / 2

                    val front = toLocalPhoneNumber.substring(0, length - half)
                    val rear = "*".repeat(toLocalPhoneNumber.substring(length - half, length).length)

                    maskNumber = "$front$rear"
                }
                toLocalPhoneNumber = maskNumber
            }
            return toLocalPhoneNumber
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
            return internationalNumber ?: ""
        }
    }

    /**
     * 날짜 값 반환 (yyyyMMdd)
     */
    open fun toDate(date: Date, format: String = "yyyyMMdd"): String = date format format

    /**
     * 시간 값 반환 (HHmmss)
     */
    open fun toTime(date: Date): String = date format "HHmmss"

    /**
     * 시간 간격 반환 (초)
     */
    open fun toDurationSec(fromTime: String, toTime: String): String {
        val timeFormat: SimpleDateFormat = SimpleDateFormat("HHmmss", Locale.KOREA)
        val fromTimeDate: Date? = timeFormat.parse(fromTime)
        val toTimeDate: Date? = timeFormat.parse(toTime)
        val duration: Long = toTimeDate?.time!! - fromTimeDate?.time!!
        val sec: Long = abs(duration / 1000)
        return sec.toString()
    }

    /**
     *  시간을 추가한 타임 스탬프 가져옴 (초).
     */
    open fun getAddTimeStamp(targetTime: String, second: Int): String {
        var timeStamp = "0"
        try {
            val cal: Calendar = Calendar.getInstance().apply {
                time = (targetTime parseDate "HHmmss") ?: Date()
            }
            cal.run {
                add(Calendar.SECOND, second)
            }
            timeStamp = cal.time format "HHmmss"
        } catch (e: Exception) {
            LogUtil.exception(TAG, e)
        }
        return timeStamp
    }

    /**
     * 초를 시분초 문자열로 반환
     */
    open fun secondToTimeString(
        second: Int,
        includeCustomFormat: Boolean = false,
        hourCustomFormat: String = "",
        minuteCustomFormat: String = "",
        secondCustomFormat: String = ""
    ): String {

        var timeString: String = ""
        val outputHour: Int = second / 3600
        val outputMinute: Int = (second % 3600) / 60
        val outputSecond: Int = (second % 3600) % 60

        var format: String = ""

        if (includeCustomFormat) {
            if (outputHour > 0 && outputMinute >= 0 && outputSecond >= 0) {
                format = "%d$hourCustomFormat %d$minuteCustomFormat %d$secondCustomFormat"
                timeString = String.format(
                    format,
                    outputHour,
                    outputMinute,
                    outputSecond
                )
            } else if (outputHour == 0 && outputMinute > 0 && outputSecond >= 0) {
                format = "%d$minuteCustomFormat %d$secondCustomFormat"
                timeString = String.format(
                    format,
                    outputMinute,
                    outputSecond
                )
            } else if (outputHour == 0 && outputMinute == 0 && outputSecond >= 0) {
                format = "%d$secondCustomFormat"
                timeString = String.format(
                    format,
                    outputSecond
                )
            }
        } else {
            format = "%02d:%02d:%02d"
            timeString = String.format(
                format,
                outputHour,
                outputMinute,
                outputSecond
            )
        }
        return timeString
    }

    /**
     * 주민등록번호 포맷으로 변경.
     */
    open fun toSocialSecurityNumber(
        number: String,
        includeAsterisk: Boolean = false
    ): String {
        return if (number.isEmpty() || number.length != 13) {
            return number
        } else {
            when (includeAsterisk) {
                true -> number.substring(0, 6) + "-" + "*******"
                false -> number.substring(0, 6) + "-" + number.substring(6, 13)
            }
        }
    }

}

/**
 * 특수문자 제거.
 */
fun String.removeSpecialCharacters(): String =
    replace("[\\\\|*\"?:/<>]".toRegex(), "")

/**
 * 지역 휴대 전화번호 체크.
 */
fun String.checkLocalPhoneNumber(): Boolean {
    val regex = "^01([016789])-?\\d{3,4}-?\\d{4}".toRegex()
    return contains(regex)
}
