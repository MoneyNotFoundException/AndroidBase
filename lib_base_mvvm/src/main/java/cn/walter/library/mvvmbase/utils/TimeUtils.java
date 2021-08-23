package cn.walter.library.mvvmbase.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author yuxiao
 * @date 2019/3/6
 * 时间获取工具类
 */
public class TimeUtils {


    /**
     * 年月日转时间戳
     *
     * @param date
     * @return
     */
    public static long getTimeStamp(String date, FormatType type) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat(type == null ? "yyyy年MM月dd日 HH:mm" : type.getFormat());
            return sf.parse(date).getTime();// 日期转换为时间戳
        } catch (Exception e) {
            return 0;
        }

    }


    /**
     * 获取年份
     *
     * @return
     */
    public static String getYear() {
        return getYear(null);
    }

    public static String getYear(Date d) {
        Calendar c = Calendar.getInstance();
        if (d != null) {
            c.setTime(d);
        }
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        return year;

    }

    /**
     * 获取月份
     *
     * @return
     */
    public static String getMonth(boolean needzero) {

        return getMonth(needzero, null);
    }

    public static String getMonth(boolean needzero, Date d) {
        Calendar c = Calendar.getInstance();
        if (d != null) {
            c.setTime(d);
        }
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String month = "";
        if (c.get(Calendar.MONTH) + 1 < 10) {
            // 获取当前月份
            month = needzero ? "0" + String.valueOf(c.get(Calendar.MONTH) + 1) : String.valueOf(c.get(Calendar.MONTH) + 1);
        } else {
            month = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        }

        return month;
    }


    /**
     * 获取几号
     *
     * @return
     */
    public static String getDay(boolean needZero) {
        return getDay(needZero, null);
    }

    public static String getDay(boolean needZero, Date d) {
        Calendar c = Calendar.getInstance();
        if (d != null) {
            c.setTime(d);
        }
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String day = "";
        // 获取当前月份的日期号码
        if (c.get(Calendar.DAY_OF_MONTH) < 10) {
            day = needZero ? "0" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) : String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        } else {
            day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        }
        return day;
    }

    public static String getDay() {
        return getDay(true);
    }

    /**
     * 获取星期数
     *
     * @return
     */
    public static String getWeek() {
        return getWeek(null);
    }

    public static String getWeek(Date d) {
        Calendar c = Calendar.getInstance();
        if (d != null) {
            c.setTime(d);
        }
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String week = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(week)) {
            week = "日";
        } else if ("2".equals(week)) {
            week = "一";
        } else if ("3".equals(week)) {
            week = "二";
        } else if ("4".equals(week)) {
            week = "三";
        } else if ("5".equals(week)) {
            week = "四";
        } else if ("6".equals(week)) {
            week = "五";
        } else if ("7".equals(week)) {
            week = "六";
        }
        return week;
    }

    /**
     * 获取月份日期和星期
     *
     * @return
     */
    public static String getMonthDayWeek() {
        String month = getMonth(true);
        String day = getDay();
        String week = getWeek();
        return month + "-" + day + "  周" + week;
    }

    /**
     * 获取今天日期
     */
    public static String getTodayTime(boolean needZero) {
        String year = getYear();
        String month = getMonth(needZero);
        String day = getDay(needZero);
        return year + "-" + month + "-" + day;
    }

    public static String getTodayTime() {
        return getTodayTime(true);
    }


    /**
     * 获取今日中文日期
     */
    public static String getTodayZhTime() {
        String year = getYear();
        String month = getMonth(true);
        String day = getDay(true);
        return year + "年" + month + "月" + day + "日";
    }

    public static String getTodayTimeForSlash() {
        String year = getYear();
        String month = getMonth(true);
        String day = getDay(true);
        return year + "/" + month + "/" + day;
    }


    /**
     * 比较两个日期的大小
     *
     * @param date1
     * @return 1表示date1大于当前时间，-1表示date1小于当前时间，0表示相等
     */
    public static int compareCurrentDate(String date1) {
        return compareCurrentDate(date1, "yyyy-MM-dd HH:mm");

    }

    public static int compareCurrentDate(String date1, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = new Date();
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前格式化系统时间
     *
     * @param formatType
     * @return
     */
    public static String getFormatDate(FormatType formatType) {

        SimpleDateFormat df = new SimpleDateFormat(formatType.getFormat());

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date time = c.getTime();
        return df.format(time);

    }

    public static String getFormatDate(FormatType formatType, Date date) {
        SimpleDateFormat df = new SimpleDateFormat(formatType.getFormat());
        return df.format(date);

    }

    /**
     * 将月转化为英文
     *
     * @param month
     * @return
     */
    public static String formatMonthToEng(String month) {
        if ("01".equals(month)||"1".equals(month)){
            return "Jan";
        }else if ("02".equals(month)||"2".equals(month)){
            return "Feb";
        }else if ("03".equals(month)||"3".equals(month)){
            return "Mar";
        }else if ("04".equals(month)||"4".equals(month)){
            return "Apr";
        }else if ("05".equals(month)||"5".equals(month)){
            return "May";
        }else if ("06".equals(month)||"6".equals(month)){
            return "Jun";
        }else if ("07".equals(month)||"7".equals(month)){
            return "Jul";
        }else if ("08".equals(month)||"8".equals(month)){
            return "Aug";
        }else if ("09".equals(month)||"9".equals(month)){
            return "Sep";
        }else if ("10".equals(month)){
            return "Otc";
        }else if ("11".equals(month)){
            return "Nov";
        }else if ("12".equals(month)){
            return "Dec";
        }

        return "Otc";

    }



    public enum FormatType {

        YEAR_TO_DAY("yyyy-MM-dd"),
        YEAR_TO_MINUTE("yyyy-MM-dd HH:mm"),
        YEAR_TO_SECOND("yyyy-MM-dd HH:mm:ss"),
        YEAR_TO_SECOND_NO_SYMBOL("yyyyMMddHHmmss"),
        MONTH_TO_DAY("MM-dd"),
        MONTH_TO_MINUTE("MM-dd HH:mm"),
        MONTH_TO_SECOND("MM-dd HH:mm:ss");

        private String format;

        FormatType(String format) {
            this.format = format;

        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }


}
