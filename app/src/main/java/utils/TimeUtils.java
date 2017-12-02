package utils;

/**
 * Created by tanyang on 2017/12/2.
 */

import android.text.TextUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * <br>类描述:时间转换工具类
 * <br>功能详细描述:
 *
 * @author  rongjinsong
 * @date  [2014年11月4日]
 */
public class TimeUtils {
    public final static String LONGEST_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String SHORT_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";
    public final static String HOUR_MINUTE_FORMAT = "HH:mm";
    public final static String LONG_OTHER_FORMAT = "MM/dd";

    private static SimpleDateFormat sFormatter = new SimpleDateFormat();

    // ///////////////////////////////////////////////////////////////
    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static Date getNowDateLongest() {
        return getNowDate(LONGEST_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        return getNowDate(LONG_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static Date getNowDateShort() {
        return getNowDate(SHORT_FORMAT);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static Date getNowTimeShort() {
        return getNowDate(TIME_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @param timeFormat
     *            返回时间格式
     */
    public static Date getNowDate(String timeFormat) {
        Date currentTime = new Date();
        Date currentTime_2 = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            String dateString = sFormatter.format(currentTime);
            ParsePosition pos = new ParsePosition(0);
            currentTime_2 = sFormatter.parse(dateString, pos);
        }
        return currentTime_2;
    }

    // /////////////////////////////////////////////////////////////////////////////
    /**
     * 获取现在时间
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String getStringDateLongest() {
        return getStringDate(LONGEST_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        return getStringDate(LONG_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        return getStringDate(SHORT_FORMAT);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        return getStringDate(TIME_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @param timeFormat
     */
    public static String getStringDate(String timeFormat) {
        Date currentTime = new Date();
        String dateString = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            dateString = sFormatter.format(currentTime);
        }
        return dateString;
    }

    // //////////////////////////////////////////////////////////////////////////////
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param strDate
     * @return
     */
    public static Date strToLongDateLongest(String strDate) {
        return strToDate(strDate, LONGEST_FORMAT);
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToLongDate(String strDate) {
        return strToDate(strDate, LONG_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToShortDate(String strDate) {
        return strToDate(strDate, SHORT_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToTimeDate(String strDate) {
        return strToDate(strDate, TIME_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm
     *
     * @param strDate
     * @return
     */
    public static Date strToHourMinuteDate(String strDate) {
        return strToDate(strDate, HOUR_MINUTE_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 MM/dd
     *
     * @param strDate
     * @return
     */
    public static Date strToLongOtherDate(String strDate) {
        return strToDate(strDate, LONG_OTHER_FORMAT);
    }

    /**
     * 按指定的时间格式字符串转换为时间
     *
     * @param strDate
     * @param timeFormat
     * @return
     */
    public static Date strToDate(String strDate, String timeFormat) {
        Date strtodate = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            ParsePosition pos = new ParsePosition(0);
            strtodate = sFormatter.parse(strDate, pos);
        }
        return strtodate;
    }

    // ///////////////////////////////////////////////////////////////////////////////
    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param dateDate
     * @return
     */
    public static String dateToLongestStr(Date dateDate) {
        return dateToStr(dateDate, LONGEST_FORMAT);
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToLongStr(Date dateDate) {
        return dateToStr(dateDate, LONG_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @return
     */
    public static String dateToShortStr(Date dateDate) {
        return dateToStr(dateDate, SHORT_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm:ss
     *
     * @return
     */
    public static String dateToTimeStr(Date dateDate) {
        return dateToStr(dateDate, TIME_FORMAT);
    }


    /**
     * 将短时间格式字符串转换为时间 HH:mm
     *
     * @return
     */
    public static String dateToHourMinuteStr(Date dateDate) {
        return dateToStr(dateDate, HOUR_MINUTE_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 MM/dd
     *
     * @return
     */
    public static String dateToLongOtherStr(Date dateDate) {
        return dateToStr(dateDate, LONG_OTHER_FORMAT);
    }

    /**
     * 按指定的时间格式时间转换为字符串
     *
     * @param dateDate
     * @param timeFormat
     * @return
     */
    public static String dateToStr(Date dateDate, String timeFormat) {
        String dateString = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            dateString = sFormatter.format(dateDate);
        }
        return dateString;
    }

    public static String longToStr(long m, String timeFormat) {
        String dateString = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            dateString = sFormatter.format(new Date(m));
        }
        return dateString;
    }


    /**
     * 功能简述: 根据年月日获取当前的星期
     * 功能详细描述:
     * 注意:
     *
     * @return 0～6 对应 周日～周一，年月日无效（-10000）时会返回-1
     */
    public static int getWeek(Date date) {
        int week = -1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//也可将此值当参数传进来
        String pTime = format.format(date);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                week = 0;
                break;
            case 2:
                week = 1;
                break;
            case 3:
                week = 2;
                break;
            case 4:
                week = 3;
                break;
            case 5:
                week = 4;
                break;
            case 6:
                week = 5;
                break;
            case 7:
                week = 6;
                break;
            default:
                break;
        }
        return week;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param date 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     */
    public static boolean isToday(Date date) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static String trimTime(String dateTime) {
        String time = dateTime;
        try {
            if (!TextUtils.isEmpty(dateTime)) {
                Date date = strToDate(dateTime, "yyyy-MM-dd'T'HH:mm:ssZ");
                time = dateToStr(date, "HH:mm:ss");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }
    // ////////////////////////////////////////////////////////////////////////////////
}
