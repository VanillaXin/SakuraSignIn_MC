package xin.vanilla.mc.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String HMS_FORMAT = "HH:mm:ss";
    public static final String ISO_MONTH_FORMAT = "yyyyMM";
    public static final String ISO_DATE_FORMAT = "yyyyMMdd";
    public static final String ISO_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TAIWAN_DATE_FORMAT = "yyyy/MM/dd";
    public static final String TAIWAN_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String POINT_DATE_FORMAT = "yyyy.MM.dd";
    public static final String CHINESE_DATE_FORMAT = "yyyy年MM月dd日";

    public DateUtils() {
    }

    private static LocalDateTime getLocalDateTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static Locale getLocalFromLanguageTag(String languageTag) {
        if (StringUtils.isNullOrEmpty(languageTag)) {
            languageTag = Locale.getDefault().getLanguage();
        } else if (languageTag.contains("_") || languageTag.contains("-")) {
            languageTag = languageTag.replace("-", "_").split("_")[0];
        }
        return Locale.forLanguageTag(languageTag);
    }

    public static String toLocalStringYear(Date date, String languageTag) {
        LocalDateTime localDateTime = getLocalDateTime(date);
        if (getLocalFromLanguageTag(languageTag).getLanguage().equalsIgnoreCase(Locale.CHINESE.getLanguage())) {
            return localDateTime.format(DateTimeFormatter.ofPattern("yyyy年"));
        } else {
            return localDateTime.format(DateTimeFormatter.ofPattern("yyyy"));
        }
    }

    public static String toLocalStringMonth(Date date, String languageTag) {
        LocalDateTime localDateTime = getLocalDateTime(date);
        if (getLocalFromLanguageTag(languageTag).getLanguage().equalsIgnoreCase(Locale.CHINESE.getLanguage())) {
            return localDateTime.format(DateTimeFormatter.ofPattern("M月"));
        } else {
            return localDateTime.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, getLocalFromLanguageTag(languageTag));
        }
    }

    public static String toLocalStringWeek(Date date, String languageTag) {
        LocalDateTime localDateTime = getLocalDateTime(date);
        return localDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, getLocalFromLanguageTag(languageTag));
    }

    public static String toLocalStringDay(Date date, String languageTag) {
        LocalDateTime localDateTime = getLocalDateTime(date);
        if (getLocalFromLanguageTag(languageTag).getLanguage().equalsIgnoreCase(Locale.CHINESE.getLanguage())) {
            return localDateTime.format(DateTimeFormatter.ofPattern("d日"));
        } else {
            return localDateTime.format(DateTimeFormatter.ofPattern("dd"));
        }
    }

    public static String toString(Date date) {
        return toString(date, DATE_FORMAT);
    }

    public static String toDateTimeString(Date date) {
        return toString(date, DATETIME_FORMAT);
    }

    public static String toString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static int toDateInt(Date date) {
        return date == null ? 0 : Integer.parseInt(toString(date, ISO_DATE_FORMAT));
    }

    public static long toDateTimeInt(Date date) {
        return date == null ? 0 : Long.parseLong(toString(date, ISO_DATE_TIME_FORMAT));
    }

    /**
     * 获取给定日期的月份
     */
    public static int getMonthOfDate(Date date) {
        if (date == null) {
            date = new Date();
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }

    /**
     * 获取月初的星期
     */
    public static int getDayOfWeekOfMonthStart(Date date) {
        if (date == null) {
            date = new Date();
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1);
        return localDate.getDayOfWeek().getValue();
    }

    /**
     * 获取给定日期的年份
     */
    public static int getYearPart(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.YEAR);
    }

    /**
     * 获取给定日期是当年的第几天
     */
    public static int getDayOfYear(Date date) {
        if (date == null) {
            date = new Date();
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfYear();
    }

    /**
     * 获取给定日期是当月的第几天
     */
    public static int getDayOfMonth(Date date) {
        if (date == null) {
            date = new Date();
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfMonth();
    }

    /**
     * 获取给定日期是星期几
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) {
            date = new Date();
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfWeek().getValue();
    }

    /**
     * 获取给定年份的总天数
     */
    public static int getDaysOfYear(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取给定年份的总天数
     */
    public static int getDaysOfYear(int year) {
        Calendar ca = Calendar.getInstance();
        ca.set(year, Calendar.JANUARY, 1);
        return ca.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取给定月份的总天数
     */
    public static int getDaysOfMonth(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date addYear(Date current, int year) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static Date addYear(Date current, float year) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        double floor = Math.floor(year);
        calendar.add(Calendar.YEAR, (int) floor);
        calendar.add(Calendar.DATE, (int) (DateUtils.getDaysOfYear(current) * (year - floor)));
        return calendar.getTime();
    }

    public static Date addMonth(Date current, int month) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Date addMonth(Date current, float month) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        double floor = Math.floor(month);
        calendar.add(Calendar.MONTH, (int) floor);
        calendar.add(Calendar.DATE, (int) (DateUtils.getDaysOfMonth(calendar.getTime()) * (month - floor)));
        return calendar.getTime();
    }

    public static Date addDay(Date current, int day) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    public static Date addDay(Date current, float day) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        double floor = Math.floor(day);
        calendar.add(Calendar.DATE, (int) floor);
        calendar.add(Calendar.MILLISECOND, (int) (24 * 60 * 60 * 1000 * (day - floor)));
        return calendar.getTime();
    }

    public static Date addHour(Date current, int hour) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.HOUR, hour);
        return calendar.getTime();
    }

    public static Date addHour(Date current, float hour) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        double floor = Math.floor(hour);
        calendar.add(Calendar.HOUR, (int) floor);
        calendar.add(Calendar.MILLISECOND, (int) (60 * 60 * 1000 * (hour - floor)));
        return calendar.getTime();
    }

    public static Date addMinute(Date current, int minute) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static Date addMinute(Date current, float minute) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        double floor = Math.floor(minute);
        calendar.add(Calendar.MINUTE, (int) floor);
        calendar.add(Calendar.MILLISECOND, (int) (60 * 1000 * (minute - floor)));
        return calendar.getTime();
    }

    public static Date addSecond(Date current, int second) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    public static Date addSecond(Date current, float second) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        double floor = Math.floor(second);
        calendar.add(Calendar.SECOND, (int) floor);
        calendar.add(Calendar.MILLISECOND, (int) (1000 * (second - floor)));
        return calendar.getTime();
    }

    public static Date addMilliSecond(Date current, int ms) {
        if (current == null) {
            current = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MILLISECOND, ms);
        return calendar.getTime();
    }

    public static Date getDate(int year, int month, int day, int hour, int minute, int second, int milliSecond) {
        Calendar cal = Calendar.getInstance();
        // cal.setLenient(false);
        cal.set(year, month - 1, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, milliSecond);
        return cal.getTime();
    }

    public static Date getDate(String yearStr, String monthStr, String dayStr, String hourStr, String minuteStr, String secondStr, String milliSecondStr) {
        Date date = null;
        if (StringUtils.isNotNullOrEmpty(yearStr) && StringUtils.isNotNullOrEmpty(monthStr) && StringUtils.isNotNullOrEmpty(dayStr)) {
            int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0, milliSecond = 0;
            try {
                year = Integer.parseInt(yearStr);
                month = Integer.parseInt(monthStr);
                day = Integer.parseInt(dayStr);
                hour = StringUtils.isNotNullOrEmpty(hourStr) ? 0 : Integer.parseInt(hourStr);
                minute = StringUtils.isNotNullOrEmpty(minuteStr) ? 0 : Integer.parseInt(minuteStr);
                second = StringUtils.isNotNullOrEmpty(secondStr) ? 0 : Integer.parseInt(secondStr);
                milliSecond = Integer.parseInt(milliSecondStr);
            } catch (NumberFormatException ignored) {
            }
            if (year > 0 && month > 0 && day > 0) {
                date = getDate(year, month, day, hour, minute, second, milliSecond);
            }
        }
        return date;
    }

    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        return getDate(year, month, day, hour, minute, second, 0);
    }

    public static Date getDate(String yearStr, String monthStr, String dayStr, String hourStr, String minuteStr, String secondStr) {
        return getDate(yearStr, monthStr, dayStr, hourStr, minuteStr, secondStr, null);
    }

    public static Date getDate(int year, int month, int day) {
        return getDate(year, month, day, 0, 0, 0, 0);
    }

    public static Date getDate(String yearStr, String monthStr, String dayStr) {
        return getDate(yearStr, monthStr, dayStr, null, null, null, null);
    }

    public static void main(String[] args) {
        System.out.println(toLocalStringMonth(new Date(), "zh"));
    }
}
