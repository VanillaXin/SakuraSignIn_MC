package xin.vanilla.mc.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final String HMS_FORMAT = "HH:mm:ss";
    public static final String ISO_YEAR_FORMAT = "yyyy";
    public static final String ISO_DATE_FORMAT = "yyyyMMdd";
    public static final String ISO_MONTH_FORMAT = "yyyyMM";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TAIWAN_DATE_FORMAT = "yyyy/MM/ddHHmm";
    public static final String TAIWAN_DATE_FORMAT2 = "yyyy/MM/dd";
    public static final String ISO_ISO_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_DATETIME_14 = "yyyyMMddHHmmss";
    public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_POINTYYYYMMDD = "yyyy.MM.dd";
    public static final String CHINESE_EXPANDED_DATE_FORMAT = "yyyy年MM月dd日";

    public DateUtils() {
    }

    public static String toString(Date date) {
        return toString(date, ISO_EXPANDED_DATE_FORMAT);
    }

    public static String toDateTimeString(Date date) {
        return toString(date, DATETIME_PATTERN);
    }

    public static String toString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

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

    public static int getYearPart(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.YEAR);
    }

    public static int getDayOfMonth(Date date) {
        if (date == null) {
            date = new Date();
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfMonth();
    }

    public static int getDaysOfYear(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    public static int getDaysOfYear(int year) {
        Calendar ca = Calendar.getInstance();
        ca.set(year, Calendar.JANUARY, 1);
        return ca.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

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
}
