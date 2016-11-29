package com.horem.parachute.util;


import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by user on 2015/9/17.
 */
public class MyTimeUtil {

    /**
     * 返回2015-03-06 12:03
     * @param str
     * @return
     */
    public static String formatTime(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        StringBuilder stringBuilder = new StringBuilder();
        //str = str.substring(2);
        String[] strs = str.split("T");
        stringBuilder.append(strs[0]).append(" ")
                .append(strs[1].substring(0, strs[1].lastIndexOf(":")));
        return stringBuilder.toString();
    }

    /**
     *返回 2015-03-06 12:03:14
     * @param str
     * @return
     */
    public static String formatTimeWhole(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        StringBuilder stringBuilder = new StringBuilder();
        //str = str.substring(2);
        String[] strs = str.split("T");
        stringBuilder.append(strs[0]).append(" ")
                .append(strs[1].split("\\.")[0]);
        return stringBuilder.toString();
    }

    /**
     *返回 03月06日 12:03
     * @param str
     * @return
     */
    public static String formatTimeZh(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = str.split("T");
        String[] subStr = strs[0].split("-");
        stringBuilder.append(subStr[1]+"月"+subStr[2]+"日").append(" ")
                .append(strs[1].substring(0,strs[1].lastIndexOf(":")));
        return stringBuilder.toString();
    }
    /**
     *返回 03月06日 12:03
     * @param str
     * @return
     */
    public static String formatTimeZhShort(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        if(str != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] strs = str.split("T");
            String[] subStr = strs[0].split("-");
            stringBuilder.append(subStr[1]+"月"+subStr[2]+"日");
            return stringBuilder.toString();
        }
        return "";
    }





    public static String toDateStr(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        StringBuilder stringBuilder = new StringBuilder();
        //str = str.substring(2);
        String[] strs = str.split("T");
        stringBuilder.append(strs[0]).append(" ")
                .append(strs[1]);
        return stringBuilder.toString();
    }
    private static final ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy");
        }
    };
    public static Date toDate(String sdate) {
        return toDate(sdate, dateFormater.get());
    }

    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException var3) {
            return null;
        }
    }
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        if(TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08")) {
            defaultVaule = true;
        } else {
            defaultVaule = false;
        }

        return defaultVaule;
    }

    /**
     *
     * @param sdate = df.format(date) (其中Date date)
     *        sdate =  getDataTime("yyyy-MM-dd HH:mm:ss",data.getTime()))
*              sdate = formatTimeZh(String str) {(str = 2015-03-06T12:03:14.517)
     * @return
     */
    public static String friendlyTime(String sdate) {
        String[] value = sdate.split(" ");
        StringBuilder builder = new StringBuilder(value[1]);

        Date time = null;
        if(isInEasternEightZones()) {
            time = toDate(sdate);
        } else {
            time = transformTime(toDate(sdate), TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
        }

        if(time == null) {
            return "Unknown";
        } else {
            String ftime = "";
            Calendar cal = Calendar.getInstance();

            String curDate = (dateFormater2.get()).format(cal.getTime());//当前时间
            String paramDate = (dateFormater2.get()).format(time);//比较时间

            //如果是同一天
            if(curDate.equals(paramDate)) {
                int lt1 = (int)((cal.getTimeInMillis() - time.getTime()) / 3600000L);
                if(lt1 == 0) {
                    ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000L, 1L) + "分钟前";
                } else {
                    ftime = lt1 + "小时前";
                }

                return ftime;
            } else {
                long lt = time.getTime() / 86400000L;
                long ct = cal.getTimeInMillis() / 86400000L;
                int days = (int)(ct - lt);
                if(days == 0) {
                    int hour = (int)((cal.getTimeInMillis() - time.getTime()) / 3600000L);
                    if(hour == 0) {
                        ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000L, 1L) + "分钟前";
                    } else {
                        ftime = hour + "小时前";
                    }
                } else if(days == 1) {
                    ftime = "昨天 " + builder.substring(0,builder.lastIndexOf(":"));
                } else if(days == 2) {
                    ftime = "前天 "+ builder.substring(0,builder.lastIndexOf(":"));
                /*} else if(days > 2 && days < 31) {
                    ftime = days + "天前";
                } else if(days >= 31 && days <= 62) {
                    ftime = "一个月前";
                } else if(days > 62 && days <= 93) {
                    ftime = "2个月前";
                } else if(days > 93 && days <= 124) {
                    ftime = "3个月前";*/
                } else {
//                    ftime = (dateFormater2.get()).format(time) +" "+ builder.substring(0,builder.lastIndexOf(":")).toString();
//                    ftime = (dateFormater2.get()).format(time);
                    int curYear = cal.get(Calendar.YEAR);
                    cal.setTime(time);
                    int paramYear = cal.get(Calendar.YEAR);
                    if (curYear == paramYear) {
                        ftime = (dateFormater2.get()).format(time);
                        StringBuilder tmpValue = new StringBuilder(ftime);
                        tmpValue.replace(4,5,"年");
                        tmpValue.replace(7,8,"月");
                        tmpValue.append("日");
                        Log.d("time",tmpValue.toString());
                        ftime = tmpValue.substring(5,tmpValue.length());
                    } else {
                        ftime = (dateFormater2.get()).format(time);
                        StringBuilder tmpValue = new StringBuilder(ftime);
                        tmpValue.replace(4,5,"年");
                        tmpValue.replace(7,8,"月");
                        tmpValue.append("日");
                        ftime = tmpValue.toString();
                    }
                }
                return ftime;
            }
        }
    }
    public static Date transformTime(Date date, TimeZone oldZone, TimeZone newZone) {
        Date finalDate = null;
        if(date != null) {
            int timeOffset = oldZone.getOffset(date.getTime()) - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - (long)timeOffset);
        }

        return finalDate;
    }
    /**
     * 15-03-06
     * @param str
     * @return
     */
    public static String formatTimeWithoutHour(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        StringBuilder stringBuilder = new StringBuilder();
        str = str.substring(2);// 15-03-06T12:03:14.517
        String[] strs = str.split("T");// 15-03-06 12:03:14.517
        stringBuilder.append(strs[0]);
        return stringBuilder.toString();
    }

    /**
     * 15-03-06 12:03:14
     * @param str
     * @return
     */
    public static String formatTimeStandard(String str) {
        // 服务器发过来的时间格式：2015-03-06T12:03:14.517
        StringBuilder stringBuilder = new StringBuilder();
//        str = str.substring(2);// 15-03-06T12:03:14.517
        String[] strs = str.split("T");// 2015-03-06 12:03:14.517
        stringBuilder.append(strs[0]);
        String[] strs2 = strs[1].split(":");
        stringBuilder.append(" "+strs2[0]+":"+strs2[1]+":"+Math.round(Double.valueOf(strs2[2])+0.5));
        return stringBuilder.toString();// 2015-03-06 12:03:14
    }
    public static String hxDateTimeString(String specialDate) {  //将日期转换描述字符串

        long tmp = 0;

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        //获取现在的时间
        long nowDate = date.getTime();
        //获取指定的时间
        String oldTime = formatTimeStandard(specialDate);
        try {
            long oldDate = sdf.parse(oldTime).getTime();
            tmp = (nowDate - oldDate)/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //昨天的时间
        Calendar yesterday = Calendar.getInstance(Locale.CHINA);
        yesterday.add(Calendar.MONTH, -1);

        if (tmp < (nowDate - yesterday.getTime().getTime())) {
            //print("今天")
            //确定计算的时间 和当前的时间的差值
            if (tmp < 60) {
                return "刚刚";
            } else if (tmp < 60 * 60) {
                return (tmp / 60) + "分钟前";
            }
            return (tmp / 3600) + "小时前";
        } else if (tmp < (nowDate - yesterday.getTime().getTime() + 24 * 60 * 60)) { //表示今天

            SimpleDateFormat yester = new SimpleDateFormat("昨天 HH:mm");
            try {

                return yester.format(sdf.parse(oldTime));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else
            return oldTime;

        return oldTime;
    }
    @SuppressLint("SimpleDateFormat")
    public static String formstTimeToMonthAndDay(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = str.split("T");
        stringBuilder.append(strs[0]).append(" ").append(strs[1]);
        String timeStr = stringBuilder.toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日");
        Date date = null;
        try {
            date = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return format2.format(date);
        } else {
            return timeStr;
        }

    }

    // 聊天记录里的时间显示格式
    @SuppressWarnings("deprecation")
    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public static String formstTimeToDefaultTime(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = str.split("T");
        stringBuilder.append(strs[0]).append(" ").append(strs[1]);
        String timeStr = stringBuilder.toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format3 = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat format4 = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = null;
        Date date1 = null;
        Date curDate = new Date(System.currentTimeMillis());
        try {
            date = format.parse(timeStr);
            date1 = format2.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null && curDate != null) {
            int curYear = curDate.getYear();
            int date_year = date.getYear();
            int date_hour = date.getHours();
            int date_minute = date.getMinutes();
            String dateStr = "";// 年月日
            String period = "";// 时间段
            String time = "";// 时间
            if (date_hour >= 5 && date_hour < 12) {
                period = "早上";
                time = String.format("%02d:%02d", date_hour, date_minute);
            } else if (date_hour == 12) {
                period = "中午";
                time = String.format("%02d:%02d", date_hour, date_minute);
            } else if (date_hour >= 13 && date_hour <= 18) {
                period = "下午";
                time = String.format("%02d:%02d", date_hour - 12, date_minute);
            } else if (date_hour > 18 && date_hour <= 23) {
                period = "晚上";
                time = String.format("%02d:%02d", date_hour - 12, date_minute);
            } else {
                period = "凌晨";
                time = String.format("%02d:%02d", date_hour, date_minute);
            }
            if (date_year == curYear) {
                GregorianCalendar cal1 = new GregorianCalendar();
                GregorianCalendar cal2 = new GregorianCalendar();
                cal1.setTime(date1);
                cal2.setTime(curDate);
                long dateMillis = cal1.getTimeInMillis();
                long curDateMillis = cal2.getTimeInMillis();
                double dayCount = 0;
                int daycount_int = 0;
                if (curDateMillis > dateMillis) {
                    dayCount = (curDateMillis - dateMillis)
                            / (1000 * 3600 * 24);
                    daycount_int = (int) dayCount;
                } else {
                    dayCount = (dateMillis - curDateMillis)
                            / (1000 * 3600 * 24);
                    daycount_int = -(int) dayCount;
                }
                if (dayCount <= 2) {
                    if (daycount_int == 0) {
                        dateStr = "今天";
                        String returnStr = String.format("%s%s", period, time);
                        return returnStr;
                    } else if (daycount_int == -1) {
                        dateStr = "明天";
                    } else if (daycount_int == 1) {
                        dateStr = "昨天";
                    } else {
                        dateStr = format3.format(date);
                    }
                } else {
                    dateStr = format3.format(date);
                }

            } else {
                dateStr = format4.format(date);
            }
            String returnStr = String.format("%s %s%s", dateStr, period, time);
            return returnStr;
        } else {
            return timeStr;
        }

    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public static String formstTimeToDefaultTimeHome(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = str.split("T");
        stringBuilder.append(strs[0]).append(" ").append(strs[1]);
        String timeStr = stringBuilder.toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format3 = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat format4 = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = null;
        Date date1 = null;
        Date curDate = new Date(System.currentTimeMillis());
        try {
            date = format.parse(timeStr);
            date1 = format2.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null && curDate != null) {
            int curYear = curDate.getYear();
            int date_year = date.getYear();
            int date_hour = date.getHours();
            int date_minute = date.getMinutes();
            String dateStr = "";// 年月日
            String period = "";// 时间段
            String time = "";// 时间
            if (date_hour >= 5 && date_hour < 12) {
                period = "早上";
                time = String.format("%02d:%02d", date_hour, date_minute);
            } else if (date_hour == 12) {
                period = "中午";
                time = String.format("%02d:%02d", date_hour, date_minute);
            } else if (date_hour >= 13 && date_hour <= 18) {
                period = "下午";
                time = String.format("%02d:%02d", date_hour - 12, date_minute);
            } else if (date_hour > 18 && date_hour <= 23) {
                period = "晚上";
                time = String.format("%02d:%02d", date_hour - 12, date_minute);
            } else {
                period = "凌晨";
                time = String.format("%02d:%02d", date_hour, date_minute);
            }
            if (date_year == curYear) {
                GregorianCalendar cal1 = new GregorianCalendar();
                GregorianCalendar cal2 = new GregorianCalendar();
                cal1.setTime(date1);
                cal2.setTime(curDate);
                long dateMillis = cal1.getTimeInMillis();
                long curDateMillis = cal2.getTimeInMillis();
                double dayCount = 0;
                int daycount_int = 0;
                if (curDateMillis > dateMillis) {
                    dayCount = (curDateMillis - dateMillis)
                            / (1000 * 3600 * 24);
                    daycount_int = (int) dayCount;
                } else {
                    dayCount = (dateMillis - curDateMillis)
                            / (1000 * 3600 * 24);
                    daycount_int = -(int) dayCount;
                }
                if (dayCount <= 2) {
                    if (daycount_int == 0) {
                        dateStr = "今天";
                        String returnStr = String.format("%s%s", period, time);
                        return returnStr;
                    } else if (daycount_int == -1) {
                        dateStr = "明天";
                        return dateStr;
                    } else if (daycount_int == 1) {
                        dateStr = "昨天";
                        return dateStr;
                    } else {
                        dateStr = format3.format(date);
                        return dateStr;
                    }
                } else {
                    dateStr = format3.format(date);
                    return dateStr;
                }

            } else {
                dateStr = format4.format(date);
                return dateStr;
            }

        } else {
            return timeStr;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean IsGreaterThanOneMinutes(String previousTime,
                                                  String nowTime) {
        boolean isGreater = false;
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = previousTime.split("T");
        stringBuilder.append(strs[0]).append(" ").append(strs[1]);
        String timeStr_previous = stringBuilder.toString();

        StringBuilder stringBuilder1 = new StringBuilder();
        String[] strs1 = nowTime.split("T");
        stringBuilder1.append(strs1[0]).append(" ").append(strs1[1]);
        String timeStr_now = stringBuilder1.toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date_previous = null;
        Date date_now = null;
        try {
            date_previous = format.parse(timeStr_previous);
            date_now = format.parse(timeStr_now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long diff = date_now.getTime() - date_previous.getTime();
        long min = diff % nd % nh / nm;
        if (min > 1) {
            isGreater = true;
        }

        return isGreater;
    }
}
