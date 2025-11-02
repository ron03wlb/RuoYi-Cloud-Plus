package org.dromara.common.core.utils;

import org.dromara.common.core.BaseUnitTest;
import org.dromara.common.core.enums.FormatsType;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * DateUtils 工具类测试
 * <p>
 * 测试覆盖：
 * - 获取当前日期/时间
 * - 日期格式化
 * - 日期解析
 * - 时间差计算
 * - 类型转换
 * - 日期验证
 * </p>
 *
 * @author Test Team
 */
class DateUtilsTest extends BaseUnitTest {

    // ========================================
    // 获取当前日期/时间测试
    // ========================================

    @Test
    void shouldGetNowDate() {
        Date now = DateUtils.getNowDate();

        assertThat(now).isNotNull();
        assertThat(now.getTime()).isLessThanOrEqualTo(System.currentTimeMillis());
    }

    @Test
    void shouldGetCurrentDateInYYYYMMDDFormat() {
        String date = DateUtils.getDate();

        assertThat(date)
            .isNotNull()
            .matches("\\d{4}-\\d{2}-\\d{2}");
    }

    @Test
    void shouldGetCurrentDateInYYYYMMDDCompactFormat() {
        String date = DateUtils.getCurrentDate();

        assertThat(date)
            .isNotNull()
            .hasSize(8)
            .matches("\\d{8}");
    }

    @Test
    void shouldGetDatePath() {
        String path = DateUtils.datePath();

        assertThat(path)
            .isNotNull()
            .matches("\\d{4}/\\d{2}/\\d{2}");
    }

    @Test
    void shouldGetCurrentTime() {
        String time = DateUtils.getTime();

        assertThat(time)
            .isNotNull()
            .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    @Test
    void shouldGetTimeWithHourMinuteSecond() {
        String time = DateUtils.getTimeWithHourMinuteSecond();

        assertThat(time)
            .isNotNull()
            .matches("\\d{2}:\\d{2}:\\d{2}");
    }

    @Test
    void shouldGetDateTimeNowInDefaultFormat() {
        String dateTime = DateUtils.dateTimeNow();

        assertThat(dateTime)
            .isNotNull()
            .hasSize(14)
            .matches("\\d{14}");
    }

    @ParameterizedTest
    @EnumSource(FormatsType.class)
    void shouldGetDateTimeNowWithDifferentFormats(FormatsType format) {
        String dateTime = DateUtils.dateTimeNow(format);

        assertThat(dateTime).isNotNull();
    }

    // ========================================
    // 日期格式化测试
    // ========================================

    @Test
    void shouldFormatDateToYYYYMMDD() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15);
        Date date = cal.getTime();

        String formatted = DateUtils.formatDate(date);

        assertThat(formatted).startsWith("2024-01-15");
    }

    @Test
    void shouldFormatDateTimeToYYYYMMDDHHMMSS() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();

        String formatted = DateUtils.formatDateTime(date);

        assertThat(formatted).startsWith("2024-01-15 14:30:45");
    }

    @Test
    void shouldParseDateToStringWithCustomFormat() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();

        String formatted = DateUtils.parseDateToStr(FormatsType.YYYYMMDD, date);

        assertThat(formatted).isEqualTo("20240115");
    }

    @Test
    void shouldParseDateToStringWithYYYYMMDDHHMMSSFormat() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();

        String formatted = DateUtils.parseDateToStr(FormatsType.YYYYMMDDHHMMSS, date);

        assertThat(formatted).isEqualTo("20240115143045");
    }

    // ========================================
    // 日期解析测试
    // ========================================

    @Test
    void shouldParseDateTimeWithFormat() {
        String dateTimeStr = "2024-01-15 14:30:45";

        Date date = DateUtils.parseDateTime(FormatsType.YYYY_MM_DD_HH_MM_SS, dateTimeStr);

        assertThat(date).isNotNull();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertThat(cal.get(Calendar.YEAR)).isEqualTo(2024);
        assertThat(cal.get(Calendar.MONTH)).isEqualTo(Calendar.JANUARY);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(15);
        assertThat(cal.get(Calendar.HOUR_OF_DAY)).isEqualTo(14);
        assertThat(cal.get(Calendar.MINUTE)).isEqualTo(30);
        assertThat(cal.get(Calendar.SECOND)).isEqualTo(45);
    }

    @Test
    void shouldThrowExceptionWhenParseDateTimeWithInvalidFormat() {
        String invalidDateStr = "invalid-date";

        assertThatThrownBy(() ->
            DateUtils.parseDateTime(FormatsType.YYYY_MM_DD_HH_MM_SS, invalidDateStr)
        ).isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2024-01-15",
        "2024-01-15 14:30:45",
        "2024-01-15 14:30",
        "2024-01",
        "2024/01/15",
        "2024/01/15 14:30:45",
        "2024.01.15",
        "2024.01.15 14:30:45"
    })
    void shouldParseDateFromVariousFormats(String dateStr) {
        Date date = DateUtils.parseDate(dateStr);

        assertThat(date).isNotNull();
    }

    @Test
    void shouldReturnNullWhenParseDateWithNull() {
        Date date = DateUtils.parseDate(null);

        assertThat(date).isNull();
    }

    @Test
    void shouldReturnNullWhenParseDateWithInvalidFormat() {
        Date date = DateUtils.parseDate("invalid-date-format");

        assertThat(date).isNull();
    }

    // ========================================
    // 服务器启动时间测试
    // ========================================

    @Test
    void shouldGetServerStartDate() {
        Date startDate = DateUtils.getServerStartDate();

        assertThat(startDate).isNotNull();
        assertThat(startDate.getTime()).isLessThanOrEqualTo(System.currentTimeMillis());
    }

    // ========================================
    // 时间差计算测试
    // ========================================

    @Test
    void shouldCalculateDifferenceInDays() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 11, 0, 0, 0);
        Date end = cal2.getTime();

        long diff = DateUtils.difference(start, end, TimeUnit.DAYS);

        assertThat(diff).isEqualTo(10);
    }

    @Test
    void shouldCalculateDifferenceInHours() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 15, 0, 0);
        Date end = cal2.getTime();

        long diff = DateUtils.difference(start, end, TimeUnit.HOURS);

        assertThat(diff).isEqualTo(5);
    }

    @Test
    void shouldCalculateDifferenceInMinutes() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 10, 30, 0);
        Date end = cal2.getTime();

        long diff = DateUtils.difference(start, end, TimeUnit.MINUTES);

        assertThat(diff).isEqualTo(30);
    }

    @Test
    void shouldCalculateDifferenceInSeconds() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 10, 0, 45);
        Date end = cal2.getTime();

        long diff = DateUtils.difference(start, end, TimeUnit.SECONDS);

        assertThat(diff).isEqualTo(45);
    }

    @Test
    void shouldCalculateAbsoluteDifference() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 10, 0, 0, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        Date end = cal2.getTime();

        // end 在 start 之前，但应该返回绝对值
        long diff = DateUtils.difference(start, end, TimeUnit.DAYS);

        // 应该是9天的差异（取绝对值）
        assertThat(diff).isGreaterThanOrEqualTo(8).isLessThanOrEqualTo(9);
    }

    @Test
    void shouldCalculateDifferenceInMilliseconds() {
        Date start = new Date(1000);
        Date end = new Date(5000);

        long diff = DateUtils.difference(start, end, TimeUnit.MILLISECONDS);

        assertThat(diff).isEqualTo(4000);
    }

    @Test
    void shouldGetDatePoorString() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 3, 14, 45, 0);
        Date end = cal2.getTime();

        String poor = DateUtils.getDatePoor(end, start);

        assertThat(poor)
            .contains("2天")
            .contains("4小时")
            .contains("45分钟");
    }

    @Test
    void shouldGetDatePoorWithNegativeValue() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 10, 0, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        Date end = cal2.getTime();

        String poor = DateUtils.getDatePoor(end, start);

        // 应该包含负数
        assertThat(poor).isNotNull();
    }

    @Test
    void shouldGetTimeDifferenceString() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 3, 14, 45, 30);
        Date end = cal2.getTime();

        String diff = DateUtils.getTimeDifference(end, start);

        assertThat(diff)
            .contains("2天")
            .contains("4小时")
            .contains("45分钟")
            .contains("30秒");
    }

    @Test
    void shouldGetTimeDifferenceWithOnlyHours() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 15, 0, 0);
        Date end = cal2.getTime();

        String diff = DateUtils.getTimeDifference(end, start);

        assertThat(diff)
            .isEqualTo("5小时")
            .doesNotContain("天")
            .doesNotContain("分钟")
            .doesNotContain("秒");
    }

    @Test
    void shouldReturnZeroSecondWhenTimeDifferenceIsZero() {
        Date date = new Date();

        String diff = DateUtils.getTimeDifference(date, date);

        assertThat(diff).isEqualTo("0秒");
    }

    // ========================================
    // LocalDateTime/LocalDate 转换测试
    // ========================================

    @Test
    void shouldConvertLocalDateTimeToDate() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);

        Date date = DateUtils.toDate(localDateTime);

        assertThat(date).isNotNull();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertThat(cal.get(Calendar.YEAR)).isEqualTo(2024);
        assertThat(cal.get(Calendar.MONTH)).isEqualTo(Calendar.JANUARY);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(15);
        assertThat(cal.get(Calendar.HOUR_OF_DAY)).isEqualTo(14);
        assertThat(cal.get(Calendar.MINUTE)).isEqualTo(30);
        assertThat(cal.get(Calendar.SECOND)).isEqualTo(45);
    }

    @Test
    void shouldConvertLocalDateToDate() {
        LocalDate localDate = LocalDate.of(2024, 1, 15);

        Date date = DateUtils.toDate(localDate);

        assertThat(date).isNotNull();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertThat(cal.get(Calendar.YEAR)).isEqualTo(2024);
        assertThat(cal.get(Calendar.MONTH)).isEqualTo(Calendar.JANUARY);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(15);
        assertThat(cal.get(Calendar.HOUR_OF_DAY)).isEqualTo(0);
        assertThat(cal.get(Calendar.MINUTE)).isEqualTo(0);
        assertThat(cal.get(Calendar.SECOND)).isEqualTo(0);
    }

    // ========================================
    // 日期范围验证测试
    // ========================================

    @Test
    void shouldValidateDateRangeSuccessfully() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 5);
        Date end = cal2.getTime();

        // 不应该抛出异常
        assertThatCode(() ->
            DateUtils.validateDateRange(start, end, 10, TimeUnit.DAYS)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenEndDateBeforeStartDate() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 10);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1);
        Date end = cal2.getTime();

        assertThatThrownBy(() ->
            DateUtils.validateDateRange(start, end, 10, TimeUnit.DAYS)
        ).isInstanceOf(ServiceException.class)
            .hasMessageContaining("结束日期不能早于开始日期");
    }

    @Test
    void shouldThrowExceptionWhenDateRangeExceedsMaxDays() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.FEBRUARY, 1);
        Date end = cal2.getTime();

        assertThatThrownBy(() ->
            DateUtils.validateDateRange(start, end, 10, TimeUnit.DAYS)
        ).isInstanceOf(ServiceException.class)
            .hasMessageContaining("最大时间跨度");
    }

    @Test
    void shouldThrowExceptionWhenDateRangeExceedsMaxHours() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 2, 10, 0);
        Date end = cal2.getTime();

        assertThatThrownBy(() ->
            DateUtils.validateDateRange(start, end, 12, TimeUnit.HOURS)
        ).isInstanceOf(ServiceException.class)
            .hasMessageContaining("最大时间跨度");
    }

    @Test
    void shouldThrowExceptionWhenDateRangeExceedsMaxMinutes() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1, 10, 0);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 1, 12, 0);
        Date end = cal2.getTime();

        assertThatThrownBy(() ->
            DateUtils.validateDateRange(start, end, 60, TimeUnit.MINUTES)
        ).isInstanceOf(ServiceException.class)
            .hasMessageContaining("最大时间跨度");
    }

    @Test
    void shouldThrowExceptionWhenUnsupportedTimeUnit() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 2);
        Date end = cal2.getTime();

        assertThatThrownBy(() ->
            DateUtils.validateDateRange(start, end, 10, TimeUnit.SECONDS)
        ).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("不支持的时间单位");
    }

    @Test
    void shouldValidateDateRangeExactlyAtMaxValue() {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(2024, Calendar.JANUARY, 1);
        Date start = cal1.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(2024, Calendar.JANUARY, 11);
        Date end = cal2.getTime();

        // 正好10天，不应该抛出异常
        assertThatCode(() ->
            DateUtils.validateDateRange(start, end, 10, TimeUnit.DAYS)
        ).doesNotThrowAnyException();
    }

    // ========================================
    // 边界值测试
    // ========================================

    @Test
    void shouldHandleNullDateInFormatMethods() {
        // 这些方法会抛出 NullPointerException，因为没有 null 检查
        assertThatThrownBy(() -> DateUtils.formatDate(null))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> DateUtils.formatDateTime(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldHandleSameDateInDifference() {
        Date date = new Date();

        long diff = DateUtils.difference(date, date, TimeUnit.SECONDS);

        assertThat(diff).isZero();
    }

    @Test
    void shouldHandleMillisecondsInDifference() {
        Date start = new Date(1000);
        Date end = new Date(1500);

        long diff = DateUtils.difference(start, end, TimeUnit.MILLISECONDS);

        assertThat(diff).isEqualTo(500);
    }

    @Test
    void shouldHandleMicrosecondsInDifference() {
        Date start = new Date(1000);
        Date end = new Date(2000);

        long diff = DateUtils.difference(start, end, TimeUnit.MICROSECONDS);

        assertThat(diff).isEqualTo(1000000); // 1000 毫秒 = 1000000 微秒
    }

    @Test
    void shouldHandleNanosecondsInDifference() {
        Date start = new Date(1000);
        Date end = new Date(2000);

        long diff = DateUtils.difference(start, end, TimeUnit.NANOSECONDS);

        assertThat(diff).isEqualTo(1000000000L); // 1000 毫秒 = 1000000000 纳秒
    }
}
