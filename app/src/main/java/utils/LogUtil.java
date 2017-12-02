package utils;


import android.util.Log;

import com.tanyang.favoritecode.BuildConfig;

/**
 * ClassName: LogUtil
 * Description:日志工具类
 */
public class LogUtil {

    public static final String S_BY_S_AT_S = "%s \n(by: %s at: %s)";
    public static final String TAG_MSG_AT_S = "[%s] %s \n(at: %s)";
    public static final String S_AT_S = "%s \n(at: %s)";
    public static final String S_BY_S = "%s \n(by: %s)";
    public static final String S_E_BY_S_AT_S = "%s =>[ %s ] \n(by: %s at: %s)";


    private static String sTag = "tanyang";


    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR = 4;
    public static final int LEVEL_FATAL = 5;
    private static int sLevel = LEVEL_VERBOSE;

    /**
     * set log level, the level lower than this level will not be logged
     *
     * @param level
     */
    public static void setLogLevel(int level) {
        sLevel = level;
    }

    private static boolean sIsLogEnable = BuildConfig.DEBUG;

    public static void v(String msg) {
        if (sLevel > LEVEL_VERBOSE) {
            return;
        }
        if (sIsLogEnable) {
            Log.v(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }

    public static void d(String msg) {
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        if (sIsLogEnable) {
            Log.d(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }

    public static void d2(String msg) {
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        if (sIsLogEnable) {
            Log.d(sTag, msg);
        }
    }

    public static void d2(String tag, String msg) {
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        if (sIsLogEnable) {
            Log.d(sTag, msg);
        }
    }


    public static void e2(String tag, String msg) {
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        if (sIsLogEnable) {
            Log.e(sTag, msg);
        }
    }

    public static void i(String msg) {
        if (sLevel > LEVEL_INFO) {
            return;
        }
        if (sIsLogEnable) {
            Log.i(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }

    public static void w(String msg) {
        if (sLevel > LEVEL_WARNING) {
            return;
        }
        if (sIsLogEnable) {
            Log.w(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }

    public static void e(String msg) {
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        if (sIsLogEnable) {
            Log.e(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }

    public static void e(Throwable e) {
        if (sLevel > LEVEL_DEBUG) {
            return;
        }

        String msg = "";
        if (null != e) {
            msg = e.getMessage();
        }

        if (msg == null) {
            msg = "";
        }

        if (sIsLogEnable) {
            Log.d(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }


    public static void f(String msg) {
        if (sLevel > LEVEL_FATAL) {
            return;
        }
        if (sIsLogEnable) {
            Log.wtf(sTag, String.format(S_AT_S, msg, getStackTraceMsg()));
        }
    }


    public static void v(String tag, String msg) {
        if (sLevel > LEVEL_VERBOSE) {
            return;
        }
        if (sIsLogEnable) {
            Log.v(sTag, String.format(TAG_MSG_AT_S, tag,  msg,  getStackTraceMsg()));
        }
    }

    public static void d(String tag, String msg) {
        if (sLevel > LEVEL_DEBUG) {
            return;
        }
        if (sIsLogEnable) {
            Log.d(sTag, String.format(TAG_MSG_AT_S, tag,  msg,  getStackTraceMsg()));
        }
    }

    public static void i(String tag, String msg) {

        if (sLevel > LEVEL_INFO) {
            return;
        }
        if (sIsLogEnable) {
            Log.i(sTag, String.format(TAG_MSG_AT_S, tag,  msg,  getStackTraceMsg()));
        }
    }

    public static void w(String tag, String msg) {
        if (sLevel > LEVEL_WARNING) {
            return;
        }
        if (sIsLogEnable) {
            Log.w(sTag, String.format(TAG_MSG_AT_S, tag,  msg,  getStackTraceMsg()));
        }
    }

    public static void e(String tag, String msg) {
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        if (sIsLogEnable) {
            Log.e(sTag, String.format(TAG_MSG_AT_S, tag,  msg,  getStackTraceMsg()));
        }
    }


    public static void e(String tag, String msg, Throwable e) {
        if (sLevel > LEVEL_ERROR) {
            return;
        }
        if (sIsLogEnable) {
            Log.e(sTag, String.format(S_E_BY_S_AT_S, msg, e.getMessage(), tag, getStackTraceMsg()));
        }
    }


    public static void f(String tag, String msg) {
        if (sLevel > LEVEL_FATAL) {
            return;
        }
        if (sIsLogEnable) {
            Log.wtf(sTag, String.format(TAG_MSG_AT_S, tag,  msg,  getStackTraceMsg()));
        }
    }

    /**
     * 定位代码位置，只能在该类内部使用
     */
    private static String getStackTraceMsg() {
        if (!BuildConfig.DEBUG){
            return "";
        }

        String fileInfo = "";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 6) {
            StackTraceElement stackTrace = stackTraceElements[6];
            fileInfo = String.format(" %s(%s:%s) ",  stackTrace.getMethodName(),stackTrace.getFileName(), stackTrace
                    .getLineNumber());
            return fileInfo;
        }
        return fileInfo;
    }

    /**
     * 定位代码位置，只能在该类内部使用
     */
//    private static String getStackTraceMsg() {
//        String fileInfo = "";
//
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        if (stackTraceElements != null && stackTraceElements.length > 7) {
//            StringBuilder sb = new StringBuilder();
//
//            for (int i = 4; i < stackTraceElements.length; i++) {
//
////                if (i >= 7) break;//只获取第4-6
//
//                StackTraceElement st = stackTraceElements[i];
//                String el = String.format("%s(%s:%s)", st.getMethodName(), st.getFileName(), st
//                        .getLineNumber());
//                if (i > 4) {
//                    sb.append(",");
//                }
//                sb.append(el);
//            }
//            fileInfo = sb.toString();
//        }
//
//        return fileInfo;
//    }

}
