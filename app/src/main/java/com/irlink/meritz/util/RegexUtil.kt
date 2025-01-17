package com.irlink.meritz.util

import java.util.regex.Pattern

open class RegexUtil {

    companion object {
        const val REGEX_MOBILE_PHONE_NUMBER: String = "^01([016789])[\\d\\-]+$"
        const val REGEX_MOBILE_PHONE_NUMBER_INCLUDE_HYPHEN: String = "^01([016789])-[\\d\\-]+$"
    }

    /**
     * 모바일 전화번호 형식인지 확인
     * @param phoneNumber 전화번호
     * @param includeHyphen 하이픈 포함 여부
     */
    open fun checkMobilePhoneNumber(phoneNumber: String?, includeHyphen: Boolean = false): Boolean {
        if (phoneNumber.isNullOrEmpty()) {
            return false
        }
        return when (includeHyphen) {
            true -> Pattern.compile(REGEX_MOBILE_PHONE_NUMBER_INCLUDE_HYPHEN)
            else -> Pattern.compile(REGEX_MOBILE_PHONE_NUMBER)
        }.matcher(phoneNumber)
            .matches()
    }

}