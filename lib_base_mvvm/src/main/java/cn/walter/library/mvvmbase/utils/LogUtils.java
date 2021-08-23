package cn.walter.library.mvvmbase.utils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cn.walter.library.mvvmbase.BuildConfig;


/**
 * Created by yx on 2017/12/15.
 * Log打印工具类
 */

public class LogUtils {

    private static final String DEFAULT_TAG = "DOTA_FANS";

    public static void initLog() {
        //logger
        Logger.init(DEFAULT_TAG).logLevel(LogLevel.FULL)//BuildConfig.DEBUG ? LogLevel.FULL :LogLevel.NONE
            .hideThreadInfo().methodCount(0);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Logger.d(msg);
        }
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG) {
            Logger.w(msg);
        }
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            Logger.e(msg);
        }
    }

    public static void wtf(String msg) {
        if (BuildConfig.DEBUG) {
            Logger.wtf(msg);
        }
    }
}
