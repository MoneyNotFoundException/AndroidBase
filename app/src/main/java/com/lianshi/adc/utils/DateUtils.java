package com.walter.adc.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author 笨鸟不乖
 * @email yongjia_xue@kingdee.com
 */
public final class DateUtils {

    private static final String TAG = "DateUtils";
    public static final int TYPE_ALL = 0;
    public static final int TYPE_DAY = 1;
    public static final int TYPE_WEEK = 2;
    public static final int TYPE_MONTH = 3;
    public static final int TYPE_SEASON = 4;
    public static final int TYPE_YEAR = 5;
    public static final int TYPE_CUSTOM = 6;


    private DateUtils() {

    }

    /**
     * 返回日期
     *
     * @return
     */
    public static Date newDate() {
        return new Date();
    }

    /**
     * 返回日期
     *
     * @return
     */
    public static Date toDate(long millisecond) {
        return new Date(millisecond);
    }

    /**
     * 返回日期
     *
     * @return
     */
    public static String toString(long millisecond) {
        return formatByStyle(new Date(millisecond), "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 返回日期格式为: yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDateDefault() {
        return getCurrentDateByStyle("yyyy-MM-dd");
    }

    /**
     * 返回自定义格式的日期, 如: "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd".
     *
     * @param style
     * @return
     */
    public static String getCurrentDateByStyle(String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style, Locale.getDefault());

        return sdf.format(new Date());
    }

    /**
     * 返回自定义格式的日期, 如: "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd".
     *
     * @param date
     * @param style
     * @return
     * @throws ParseException
     */
    public static Date parseDateByStyle(String date, String style) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(style, Locale.getDefault());
        // Added 2014年10月24日17:23:21
        // sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        return sdf.parse(date);
    }

    /**
     * 系统默认的日期格式: "yyyy-MM-dd", {@link #defaultFormat(Date)}
     *
     * @param milliseconds
     * @return
     */
    public static String defaultFormat(long milliseconds) {
        return defaultFormat(new Date(milliseconds));
    }

    /**
     * 系统默认的日期格式: "yyyy-MM-dd"
     *
     * @param date
     * @return
     */
    public static String defaultFormat(Date date) {
        return formatByStyle(date, "yyyy-MM-dd");
    }

    /**
     * 从yyyy-MM-dd转成MM-dd
     *
     * @param date
     * @return
     */
    public static String format2(String date) {
        try {
            return formatByStyle(parseDateByStyle(date, "yyyy-MM-dd"), "MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 从yyyy-MM-dd hh:mm:ss.SSS转成yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String format3(String date) {
        try {
            return formatByStyle(parseDateByStyle(date, "yyyy-MM-dd hh:mm:ss.SSS"), "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 自定义的日期格式
     *
     * @param date
     * @param style
     * @return
     */
    public static String formatByStyle(Date date, String style) {
        return new SimpleDateFormat(style, Locale.getDefault()).format(date);
    }

    /**
     * 自定义的日期格式
     *
     * @param milliseconds
     * @param style
     * @return
     */
    public static String formatByStyle(long milliseconds, String style) {
        Date date = new Date(milliseconds);
        return new SimpleDateFormat(style, Locale.getDefault()).format(date);
    }

    public static String timestamp2Date(String str_num, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (str_num.length() == 13) {
            String date = sdf.format(new Date(Long.parseLong(str_num)));
            return date;
        } else {
            String date = sdf.format(new Date(Integer.parseInt(str_num) * 1000L));
            return date;
        }
    }

    /**
     * 系统默认的日期格式: "yyyy-MM-dd HH:mm:ss"
     *
     * @param date
     * @return
     */
    public static String defaultFormat2(Date date) {
        return formatByStyle(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 一天起始.
     *
     * @param calendar
     * @return
     */
    public static Calendar getStartOfCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * 一天结束.
     *
     * @param calendar
     * @return
     */
    public static Calendar getEndOfCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);

        return calendar;
    }

    /**
     * 返回当天开始时刻.
     *
     * @return
     */
    public static long getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    /**
     * 返回当天开始时刻, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getStartOfTodayString() {
        return defaultFormat(getStartOfToday());
    }

    /**
     * 返回当天最后一刻
     *
     * @return
     */
    public static long getEndOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 24);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, -1);

        return today.getTimeInMillis();
    }

    /**
     * 返回当天最后一刻, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getEndOfTodayString() {
        return defaultFormat(getEndOfToday());
    }

    /**
     * 返回周的第一天
     *
     * @return
     */
    public static long getFirstDayOfWeek(Date date) {
//        Calendar calendar = new GregorianCalendar();
//        calendar.set(Calendar.DAY_OF_WEEK, 1);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        return calendar.getTimeInMillis();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 1);
        return c.getTimeInMillis();
    }

    /**
     * 返回本周开始日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getFirstDayOfWeekString() {
        return defaultFormat(getFirstDayOfWeek(new Date()));
    }

    /**
     * 返回上一周第一天
     *
     * @return
     */
    public static long getFirstDayOfLastWeek() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -7);

        return calendar.getTimeInMillis();
    }

    /**
     * 返回周的最后一天
     *
     * @return
     */
    public static long getLastDayOfWeek(Date date) {
//        Calendar calendar = new GregorianCalendar();
//
//        calendar.set(Calendar.DAY_OF_WEEK, 7);
//        calendar.set(Calendar.HOUR_OF_DAY, 24);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, -1);
//
//        return calendar.getTimeInMillis();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 7);
        return c.getTimeInMillis();
    }

    /**
     * 返回本周结束日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getLastDayOfWeekString() {
        return defaultFormat(getLastDayOfWeek(new Date()));
    }

    /**
     * 返回月的第一天
     *
     * @return
     */
    public static long getFirstDayOfMonth() {
        Calendar calendar = new GregorianCalendar();

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 返回本月开始日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getFirstDayOfMonthString() {
        return defaultFormat(getFirstDayOfMonth());
    }

    /**
     * 返回月的最后一天
     *
     * @return
     */
    public static long getLastDayOfMonth() {
        Calendar calendar = new GregorianCalendar();

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);

        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);

        return calendar.getTimeInMillis();
    }

    /**
     * 返回本月结束日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getLastDayOfMonthString() {
        return defaultFormat(getLastDayOfMonth());
    }

    /**
     * 返回季度的第一天
     *
     * @return
     */
    public static long getFirstDayOfSeason() {
        Calendar calendar = new GregorianCalendar();
        int month = getQuarterInMonth(calendar.get(Calendar.MONTH), true);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 返回本季度开始日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getFirstDayOfSeasonString() {
        return defaultFormat(getFirstDayOfSeason());
    }

    /**
     * 返回季度的最后一天
     *
     * @return
     */
    public static long getLastDayOfSeason() {
        Calendar calendar = new GregorianCalendar();
        int month = getQuarterInMonth(calendar.get(Calendar.MONTH), false);
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);

        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);

        return calendar.getTimeInMillis();
    }

    /**
     * 返回本季度结束日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getLastDayOfSeasonString() {
        return defaultFormat(getLastDayOfSeason());
    }

    /**
     * 返回当前年的第一天
     *
     * @return
     */
    public static long getFirstDayOfYear() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_YEAR, 1);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * 返回本年开始日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getFirstDayOfYearString() {
        return defaultFormat(getFirstDayOfYear());
    }

    /**
     * 返回当前年的最后一天
     *
     * @return
     */
    public static long getLastDayOfYear() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(calendar.get(Calendar.YEAR), 11, 31);

        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);


        return calendar.getTimeInMillis();
    }

    /**
     * 返回本年结束日期, 日期格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getLastDayOfYearString() {
        return defaultFormat(getLastDayOfYear());
    }

    /**
     * 返回第几个月份，不是几月. 季度一年四季， 第一季度：1月-3月， 第二季度：4月-6月， 第三季度：7月-9月， 第四季度：10月-12月
     */
    private static int getQuarterInMonth(int month, boolean isQuarterStart) {
        int months[] = {0, 3, 6, 9};
        if (!isQuarterStart) {
            months = new int[]{2, 5, 8, 11};
        }
        if (month >= 0 && month <= 2) {
            return months[0];
        } else if (month >= 3 && month <= 5) {
            return months[1];
        } else if (month >= 6 && month <= 8) {
            return months[2];
        } else {
            return months[3];
        }
    }


    //----------------------------

    /**
     * 返回date当天开始时刻.
     *
     * @param date
     * @return
     */
    public static long getStartOfDate(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(date);

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    /**
     * 返回date当天最后时刻.
     *
     * @return
     */
    public static long getEndOfDate(Date date) {
        Calendar today = Calendar.getInstance();
        today.setTime(date);

        today.set(Calendar.HOUR_OF_DAY, 24);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, -1);

        return today.getTimeInMillis();
    }


    /**
     * 返回String时刻.
     *
     * @param string
     * @return
     */
    public static long getLongDate(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        return today.getTimeInMillis();
    }

    /**
     * 返回String当天开始时刻.
     *
     * @param string
     * @return
     */
    public static long getStartOfDate(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTimeInMillis();
    }

    /**
     * 返回String当天最后时刻.
     *
     * @return
     */
    public static long getEndOfDate(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        today.set(Calendar.HOUR_OF_DAY, 24);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, -1);
        return today.getTimeInMillis();
    }


    public static Date string2Date(String string) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(string);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Date();
        }
    }

    public static String date2String(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

//    public static String getStrTimerIntervalByType(Resources res, int type) {
//
//        switch (type) {
//            case TYPE_DAY:
//                return res.getString(R.string.time_interval_day);
//            case TYPE_WEEK:
//                return res.getString(R.string.time_interval_week);
//            case TYPE_MONTH:
//                return res.getString(R.string.time_interval_month);
//            case TYPE_SEASON:
//                return res.getString(R.string.time_interval_season);
//            case TYPE_YEAR:
//                return res.getString(R.string.time_interval_year);
//            case TYPE_CUSTOM:
//                return res.getString(R.string.time_interval_custom);
//            case TYPE_ALL:
//                return res.getString(R.string.time_interval_all);
//            default:
//                return res.getString(R.string.time_interval_day);
//        }
//    }

    /**
     * 根据mSearchParam的type设置fromOn、toOn
     */
    public static String[] getFromOnToOnByType(int type) {
        String[] ret = new String[2];
        switch (type) {
            case TYPE_DAY:
                ret[0] = getCurrentDateDefault();
                ret[1] = getCurrentDateDefault();
                break;
            case TYPE_WEEK:
                ret[0] = getFirstDayOfWeekString();
                ret[1] = getLastDayOfWeekString();
                break;
            case TYPE_MONTH:
                ret[0] = getFirstDayOfMonthString();
                ret[1] = getLastDayOfMonthString();
                break;
            case TYPE_SEASON:
                ret[0] = getFirstDayOfSeasonString();
                ret[1] = getLastDayOfSeasonString();
                break;
            case TYPE_YEAR:
                ret[0] = getFirstDayOfYearString();
                ret[1] = getLastDayOfYearString();
                break;
            case TYPE_CUSTOM://如果是自定义该怎么办？//就不能从这里取数据了//暂时先这么返回
                ret[0] = "";
                ret[1] = "";
                break;
            case TYPE_ALL://暂时返回一个大范围数据，查询全部的时候
                ret[0] = getFirstAllDateString();
                ret[1] = getLastAllDateString();
                break;
            default:
                break;
        }

        return ret;
    }

    private static String getFirstAllDateString() {
        return "1990-01-01";
    }

    private static String getLastAllDateString() {
        return "2999-12-31";
    }

    public static int getTypeByFromOnToOn(String fromOn, String toOn) {
        if (fromOn.equals(getCurrentDateDefault()) && toOn.equals(getCurrentDateDefault()))
            return TYPE_DAY;
        else if (fromOn.equals(getFirstDayOfWeekString()) && toOn.equals(getLastDayOfWeekString()))
            return TYPE_WEEK;
        else if (fromOn.equals(getFirstDayOfMonthString()) && toOn.equals(getLastDayOfMonthString()))
            return TYPE_MONTH;
        else if (fromOn.equals(getFirstDayOfSeasonString()) && toOn.equals(getLastDayOfSeasonString()))
            return TYPE_SEASON;
        else if (fromOn.equals(getFirstDayOfYearString()) && toOn.equals(getLastDayOfYearString()))
            return TYPE_YEAR;
        else if (fromOn.equals(getFirstAllDateString()) && toOn.equals(getLastAllDateString()))
            return TYPE_ALL;
        else
            return TYPE_CUSTOM;
    }

    /**
     * 时间间隔合法检查，比较时间大小，from大于to返回false
     */
    public static boolean timeIntervalValid(String from, String to) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fromDate = df.parse(from);
            Date toDate = df.parse(to);
            long interval = toDate.getTime() - fromDate.getTime();
            if (interval < 0)
                return false;
            else
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static String getDateNext(String specifiedDay, int days) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + days);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
                .getTime());
        return dayBefore;
    }

    public static String getMonthNext(String specifiedDay, int months) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month + months);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
                .getTime());
        return dayBefore;
    }

    public static long getDaysSub(String date1, String date2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = df.parse(date1);
            Date d2 = df.parse(date2);

            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            return days;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getFormatDate(Date date, String format) {
        if (android.text.TextUtils.isEmpty(format.trim())) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sf.format(date);
    }

    public static String formatTimeSimpleCountDown(long ms) {
        int ss = 1000;
        long second = ms / ss;
        return second + "s";
    }
}
