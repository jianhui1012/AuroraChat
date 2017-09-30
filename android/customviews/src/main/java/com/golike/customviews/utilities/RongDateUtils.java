package com.golike.customviews.utilities;

import android.content.Context;
import android.provider.Settings;

import com.golike.customviews.R;
import com.golike.customviews.ChatContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by admin on 2017/9/5.
 */

public class RongDateUtils {
    private static final int OTHER = 2014;
    private static final int TODAY = 6;
    private static final int YESTERDAY = 15;

    public RongDateUtils() {
    }

    public static int judgeDate(Date date) {
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);
        Calendar calendarYesterday = Calendar.getInstance();
        calendarYesterday.add(Calendar.DAY_OF_MONTH, -1);
        calendarYesterday.set(Calendar.HOUR_OF_DAY, 0);
        calendarYesterday.set(Calendar.MINUTE, 0);
        calendarYesterday.set(Calendar.SECOND, 0);
        calendarYesterday.set(Calendar.MILLISECOND, 0);
        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.add(Calendar.DAY_OF_MONTH, 1);
        calendarTomorrow.set(Calendar.HOUR_OF_DAY, 0);
        calendarTomorrow.set(Calendar.MINUTE, 0);
        calendarTomorrow.set(Calendar.SECOND, 0);
        calendarTomorrow.set(Calendar.MILLISECOND, 0);
        Calendar calendarTarget = Calendar.getInstance();
        calendarTarget.setTime(date);
        return calendarTarget.before(calendarYesterday)?2014:(calendarTarget.before(calendarToday)?15:(calendarTarget.before(calendarTomorrow)?6:2014));
    }

    private static String getWeekDay(int dayInWeek) {
        String weekDay = "";
        switch(dayInWeek) {
            case 1:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_sunsay_format);
                break;
            case 2:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_monday_format);
                break;
            case 3:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_tuesday_format);
                break;
            case 4:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_wednesday_format);
                break;
            case 5:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_thuresday_format);
                break;
            case 6:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_friday_format);
                break;
            case 7:
                weekDay = ChatContext.getInstance().getResources().getString(R.string.rc_saturday_format);
        }

        return weekDay;
    }

    public static boolean isTime24Hour(Context context) {
        String timeFormat = Settings.System.getString(context.getContentResolver(), "time_12_24");
        return timeFormat != null && timeFormat.equals("24");
    }

    private static String getTimeString(long dateMillis, Context context) {
        if(dateMillis <= 0L) {
            return "";
        } else {
            Date date = new Date(dateMillis);
            String formatTime = null;
            if(isTime24Hour(context)) {
                formatTime = formatDate(date, "HH:mm");
            } else {
                Calendar calendarTime = Calendar.getInstance();
                calendarTime.setTimeInMillis(dateMillis);
                int hour = calendarTime.get(Calendar.HOUR);
                if(calendarTime.get(Calendar.AM_PM) == Calendar.SUNDAY) {
                    if(hour < 6) {
                        if(hour == 0) {
                            hour = 12;
                        }

                        formatTime = ChatContext.getInstance().getResources().getString(R.string.rc_daybreak_format);
                    } else if(hour >= 6 && hour < 12) {
                        formatTime = ChatContext.getInstance().getResources().getString(R.string.rc_morning_format);
                    }
                } else if(hour == 0) {
                    formatTime = ChatContext.getInstance().getResources().getString(R.string.rc_noon_format);
                    hour = 12;
                } else if(hour >= 1 && hour <= 5) {
                    formatTime = ChatContext.getInstance().getResources().getString(R.string.rc_afternoon_format);
                } else if(hour >= 6 && hour <= 11) {
                    formatTime = ChatContext.getInstance().getResources().getString(R.string.rc_night_format);
                }

                int minuteInt = calendarTime.get(Calendar.MINUTE);
                String minuteStr = Integer.toString(minuteInt);
                String timeStr = null;
                if(minuteInt < 10) {
                    minuteStr = "0" + minuteStr;
                }

                timeStr = Integer.toString(hour) + ":" + minuteStr;
                if(context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                    formatTime = formatTime + timeStr;
                } else {
                    formatTime = timeStr + " " + formatTime;
                }
            }

            return formatTime;
        }
    }

    private static String getDateTimeString(long dateMillis, boolean showTime, Context context) {
        if(dateMillis <= 0L) {
            return "";
        } else {
            String formatDate = null;
            Date date = new Date(dateMillis);
            int type = judgeDate(date);
            long time = java.lang.System.currentTimeMillis();
            Calendar calendarCur = Calendar.getInstance();
            Calendar calendardate = Calendar.getInstance();
            calendardate.setTimeInMillis(dateMillis);
            calendarCur.setTimeInMillis(time);
            int month = calendardate.get(Calendar.MONTH);
            int year = calendardate.get(Calendar.YEAR);
            int weekInMonth = calendardate.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            int monthCur = calendarCur.get(Calendar.MONTH);
            int yearCur = calendarCur.get(Calendar.YEAR);
            int weekInMonthCur = calendarCur.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            switch(type) {
                case 6:
                    formatDate = getTimeString(dateMillis, context);
                    break;
                case 15:
                    String formatString = ChatContext.getInstance().getResources().getString(R.string.rc_yesterday_format);
                    if(showTime) {
                        formatDate = formatString + " " + getTimeString(dateMillis, context);
                    } else {
                        formatDate = formatString;
                    }
                    break;
                case 2014:
                    if(year == yearCur) {
                        if(month == monthCur && weekInMonth == weekInMonthCur) {
                            formatDate = getWeekDay(calendardate.get(Calendar.DAY_OF_WEEK));
                        } else if(context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                            formatDate = formatDate(date, "M" + ChatContext.getInstance().getResources().getString(R.string.rc_month_format) + "d" + ChatContext.getInstance().getResources().getString(R.string.rc_day_format));
                        } else {
                            formatDate = formatDate(date, "M/d");
                        }
                    } else if(context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
                        formatDate = formatDate(date, "yyyy" + ChatContext.getInstance().getResources().getString(R.string.rc_year_format) + "M"
                                + ChatContext.getInstance().getResources().getString(R.string.rc_month_format) + "d" + ChatContext.getInstance().getResources().getString(R.string.rc_day_format));
                    } else {
                        formatDate = formatDate(date, "M/d/yy");
                    }

                    if(showTime) {
                        formatDate = formatDate + " " + getTimeString(dateMillis, context);
                    }
            }

            return formatDate;
        }
    }

    public static String getConversationListFormatDate(long dateMillis, Context context) {
        String formatDate = getDateTimeString(dateMillis, false, context);
        return formatDate;
    }

    public static String getConversationFormatDate(long dateMillis, Context context) {
        String formatDate = getDateTimeString(dateMillis, true, context);
        return formatDate;
    }

    public static boolean isShowChatTime(long currentTime, long preTime, int interval) {
        int typeCurrent = judgeDate(new Date(currentTime));
        int typePre = judgeDate(new Date(preTime));
        return typeCurrent == typePre?currentTime - preTime > (long)(interval * 1000):true;
    }

    public static String formatDate(Date date, String fromat) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromat);
        return sdf.format(date);
    }
}
