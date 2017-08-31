package com.amber.multiselector.utils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间处理工具
 * Created by Nereo on 2015/4/8.
 */
public class TimeUtils {

    public static String timeFormat(long timeMillis, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time){
        return timeFormat(time, "yyyy-MM-dd");
    }

    public static String formatPhotoDate(String path){
        File file = new File(path);
        if(file.exists()){
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }

    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType) {
        SimpleDateFormat sd = new SimpleDateFormat(formatType);
        sd.setTimeZone(TimeZone.getTimeZone("GMT+0"));//**TimeZone时区，加上这句话就解决啦**
        String time = sd.format(new Date(currentTime));//ps.将毫秒转化为时分秒格式
        return time;
    }

}
