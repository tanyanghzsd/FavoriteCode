package utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.PowerManager;
import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by tanyang on 2017/12/2.
 * 各种单位的尺寸转换
 */

public class DisplayUtil {

    public static float sDensity = 1.0f;
    public static int sDensityDpi;
    public static float sFontDensity;

    public static int sWidthPixels = -1;
    public static int sHeightPixels = -1;
    public static int sRealWidthPixels = -1;
    public static int sRealHeightPixels = -1;

    private static Class<?> sClass = null;
    private static Method sMethodForWidth = null;
    private static Method sMethodForHeight = null;

    // 在某些机子上存在不同的density值，所以增加两个虚拟值
    public static float sVirtualDensity = -1;
    public static float sVirtualDensityDpi = -1;

    public static int sTouchSlop;

    private static Point sOutSize = new Point();
    /**
     * dip/dp转像素
     *
     * @param dipValue
     *            dip或 dp大小
     * @return 像素值
     */
    public static int dip2px(float dipValue) {
        return (int) (dipValue * sDensity + 0.5f);
    }

    /**
     * 像素转dip/dp
     *
     * @param pxValue
     *            像素大小
     * @return dip值
     */
    public static int px2dip(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp 转 px
     *
     * @param spValue
     *            sp大小
     * @return 像素值
     */
    public static int sp2px(float spValue) {
        final float scale = sDensity;
        return (int) (scale * spValue);
    }

    /**
     * px转sp
     *
     * @param pxValue
     *            像素大小
     * @return sp值
     */
    public static int px2sp(float pxValue) {
        final float scale = sDensity;
        return (int) (pxValue / scale);
    }


    public static void setVirtualDensity(float density) {
        sVirtualDensity = density;
    }

    public static void setVirtualDensityDpi(float densityDpi) {
        sVirtualDensityDpi = densityDpi;
    }


    /**
     * 根据TextSize计算字体的高度
     * @param fontSize
     * @return
     */
    public static double getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + dip2px(1);
    }

    /**
     * 屏幕是否亮起来
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    //自定义View使用
    public static int getSizeFromMeasureSpec(int measureSpec, int defaultSize) {
        int result = 0;
        int mode = View.MeasureSpec.getMode(measureSpec);
        int size = View.MeasureSpec.getSize(measureSpec);
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = defaultSize;
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(defaultSize, size);
            }
        }
        return result;
    }
}
