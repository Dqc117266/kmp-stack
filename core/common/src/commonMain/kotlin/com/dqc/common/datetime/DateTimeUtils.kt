package com.dqc.common.datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * 常用日期时间格式模式
 */
object DateTimePatterns {
    /** 完整日期时间: yyyy-MM-dd HH:mm:ss */
    val FULL_DATE_TIME = LocalDateTime.Format {
        date(LocalDate.Formats.ISO)
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    /** 日期: yyyy-MM-dd */
    val DATE = LocalDate.Formats.ISO

    /** 时间: HH:mm:ss */
    val TIME = LocalDateTime.Format {
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    /** 简化时间: HH:mm */
    val SHORT_TIME = LocalDateTime.Format {
        hour()
        char(':')
        minute()
    }

    /** 紧凑日期时间: yyyyMMddHHmmss */
    val COMPACT = LocalDateTime.Format {
        year()
        monthNumber()
        dayOfMonth()
        hour()
        minute()
        second()
    }

    /** 显示日期: yyyy年MM月dd日 */
    val DISPLAY_DATE = LocalDate.Format {
        year()
        char('年')
        monthNumber()
        char('月')
        dayOfMonth()
        char('日')
    }

    /** 显示日期时间: yyyy年MM月dd日 HH:mm */
    val DISPLAY_DATE_TIME = LocalDateTime.Format {
        year()
        char('年')
        monthNumber()
        char('月')
        dayOfMonth()
        char('日')
        char(' ')
        hour()
        char(':')
        minute()
    }
}

/**
 * 格式化 LocalDateTime 为字符串
 *
 * @param pattern 格式模式，默认为 FULL_DATE_TIME
 * @return 格式化后的字符串
 */
fun LocalDateTime.format(pattern: DateTimeFormat<LocalDateTime> = DateTimePatterns.FULL_DATE_TIME): String {
    return this.format(pattern)
}

/**
 * 格式化 LocalDate 为字符串
 *
 * @param pattern 格式模式，默认为 DATE (ISO)
 * @return 格式化后的字符串
 */
fun LocalDate.format(pattern: DateTimeFormat<LocalDate> = DateTimePatterns.DATE): String {
    return this.format(pattern)
}

/**
 * 格式化 Instant 为字符串
 *
 * @param timeZone 时区，默认为当前系统时区
 * @param pattern 格式模式，默认为 FULL_DATE_TIME
 * @return 格式化后的字符串
 */
fun Instant.format(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    pattern: DateTimeFormat<LocalDateTime> = DateTimePatterns.FULL_DATE_TIME
): String {
    return this.toLocalDateTime(timeZone).format(pattern)
}

/**
 * 解析日期时间字符串
 *
 * @param isoString ISO 格式字符串 (yyyy-MM-ddTHH:mm:ss)
 * @return LocalDateTime 对象
 */
fun parseDateTime(isoString: String): LocalDateTime {
    return LocalDateTime.parse(isoString)
}

/**
 * 解析日期字符串
 *
 * @param isoString ISO 格式字符串 (yyyy-MM-dd)
 * @return LocalDate 对象
 */
fun parseDate(isoString: String): LocalDate {
    return LocalDate.parse(isoString)
}

/**
 * 解析自定义格式的日期时间字符串
 *
 * @param string 要解析的字符串
 * @param format 格式模式
 * @return LocalDateTime 对象
 */
fun parseDateTime(string: String, format: DateTimeFormat<LocalDateTime>): LocalDateTime {
    return format.parse(string)
}

// ==================== 扩展属性 ====================

/**
 * 获取当前时间的 Instant
 */
val now: Instant
    get() = Clock.System.now()

/**
 * 获取当前时间的 LocalDateTime（系统时区）
 */
val nowLocal: LocalDateTime
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * 获取当前日期（系统时区）
 */
val today: LocalDate
    get() = nowLocal.date

/**
 * 将 Instant 转换为 LocalDateTime（系统时区）
 */
fun Instant.toLocalDateTime(): LocalDateTime {
    return this.toLocalDateTime(TimeZone.currentSystemDefault())
}

/**
 * 将 LocalDate 转换为 Instant（当天开始时间，系统时区）
 */
fun LocalDate.toInstant(): Instant {
    return this.atStartOfDayIn(TimeZone.currentSystemDefault())
}

// ==================== 日期计算 ====================

/**
 * 增加天数
 */
operator fun LocalDate.plus(days: Int): LocalDate {
    return this.plus(days, DateTimeUnit.DAY)
}

/**
 * 减少天数
 */
operator fun LocalDate.minus(days: Int): LocalDate {
    return this.minus(days, DateTimeUnit.DAY)
}

/**
 * 增加月份
 */
fun LocalDate.plusMonths(months: Int): LocalDate {
    return this.plus(months, DateTimeUnit.MONTH)
}

/**
 * 减少月份
 */
fun LocalDate.minusMonths(months: Int): LocalDate {
    return this.minus(months, DateTimeUnit.MONTH)
}

/**
 * 增加年份
 */
fun LocalDate.plusYears(years: Int): LocalDate {
    return this.plus(years, DateTimeUnit.YEAR)
}

/**
 * 减少年份
 */
fun LocalDate.minusYears(years: Int): LocalDate {
    return this.minus(years, DateTimeUnit.YEAR)
}

// ==================== 时间戳转换 ====================

/**
 * 将毫秒时间戳转换为 Instant
 */
fun Long.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this)
}

/**
 * 将 Instant 转换为毫秒时间戳
 */
fun Instant.toEpochMillis(): Long {
    return this.toEpochMilliseconds()
}

/**
 * 将秒时间戳转换为 Instant
 */
fun Long.toInstantSeconds(): Instant {
    return Instant.fromEpochSeconds(this)
}
