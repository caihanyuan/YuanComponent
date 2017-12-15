package com.keepmoving.to.yuancomponent.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by gloria on 2017/6/7.
 */

public class StringUtils {
    private static final long ONE_HOUR_TIME_MILS = 3600000;
    private static final long ONE_MINUTE_TIME_MILS = 60000;
    private static final long ONE_DAY_TIME_MILS = ONE_HOUR_TIME_MILS * 24;
    private static final long ONE_WEEK_TIME_MILS = ONE_DAY_TIME_MILS * 7;

    public static boolean isPhoneNumber(String phoneNumber) {
        String regex = "1[3458]\\d{9}$";
        boolean isPhoneNum = Pattern.matches(regex, phoneNumber);

        return isPhoneNum;
    }

    public static boolean isCorrectPassword(String password) {
        String regex = "^(?![a-zA-z]+$)(?!\\d+$)[a-zA-Z\\d]{8,32}$";
        boolean bCorrect = Pattern.matches(regex, password);
        return bCorrect;
    }

    public static boolean isWpsPassword(String password) {
        String regex = "((?=.*\\d)(?=.*\\D)|(?=.*[a-zA-Z])(?=.*[^a-zA-Z]))^.{8,32}$";
        boolean bCorrect = Pattern.matches(regex, password);
        return bCorrect;
    }

    /**
     * 计算两个时间的差值比较
     *
     * @param time
     * @return
     */
    public static String timePastTip(long time) {
        long current = System.currentTimeMillis();
        long past = current - time;
        if (past > ONE_WEEK_TIME_MILS) {
            int week = (int) (past / ONE_WEEK_TIME_MILS);
            return String.format("%d周前", week);
        } else if (past > ONE_DAY_TIME_MILS) {
            int day = (int) (past / ONE_DAY_TIME_MILS);
            return String.format("%d天前", day);
        } else if (past > ONE_HOUR_TIME_MILS) {
            int hour = (int) (past / ONE_HOUR_TIME_MILS);
            return String.format("%d小时前", hour);
        } else if (past > ONE_MINUTE_TIME_MILS) {
            int minute = (int) (past / ONE_MINUTE_TIME_MILS);
            return String.format("%d分钟前", minute);
        } else {
            return "";
        }
    }

    /**
     * 按三位加一个逗号. 如￥50,000.00
     *
     * @param moneyCount
     * @return
     */
    public static String splitMoney(double moneyCount) {
        return splitMoney(moneyCount, 2);
    }

    /**
     * 按三位加一个逗号. 如￥50,000.00...
     *
     * @param moneyCount 数值
     * @param accuracy   精度 大于等于0
     * @return
     */
    public static String splitMoney(double moneyCount, int accuracy) {
        assert accuracy >= 0;

        String preFix = accuracy > 0 ? (moneyCount < 1 ? "0." : "#,###.")
                : (moneyCount < 1 ? "0" : "#,###");

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(preFix);
        while (accuracy > 0) {
            stringBuffer.append("0");
            accuracy--;
        }
        String parttern = stringBuffer.toString();
        DecimalFormat decimalFormat = new DecimalFormat(parttern);
        return decimalFormat.format(moneyCount);
    }

    /**
     * 钱的字符转成数字, 如￥50,000.00 转成 50000.00
     *
     * @param moneyStr
     * @return
     */
    public static double moneyToDouble(String moneyStr) {
        double count = 0.0;
        if (moneyStr != null) {
            moneyStr = moneyStr.replaceAll(",", "");
            count = Double.valueOf(moneyStr);
        }
        return count;
    }

    /**
     * 将时间戳转换成yyyy-MM-dd格式
     *
     * @param time
     * @return
     */
    public static String longToDateString(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 给http地址加上头，如果没有的话
     *
     * @param url
     * @return
     */
    public static String wrapHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            return url;
        }
    }

    /**
     * 判断给定的url是否为网址
     *
     * @param url
     * @return
     */
    public static boolean isHttpUrl(String url) {
        String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
