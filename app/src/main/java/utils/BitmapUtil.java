package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by tanyang on 2017/12/2.
 * Bitmap处理工具类
 */

public class BitmapUtil {

    /**
     * 生成一个二维码图像
     *
     * @param url       传入的字符串，通常是一个URL
     * @param QR_WIDTH  宽度（像素值px）
     * @param QR_HEIGHT 高度（像素值px）
     * @return
     */
//    public static final Bitmap create2DCoderBitmap(String url, int QR_WIDTH,
//                                                   int QR_HEIGHT) {
//        try {
//            // 判断URL合法性
//            if (url == null || "".equals(url) || url.length() < 1) {
//                return null;
//            }
//            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
//            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//            // 图像数据转换，使用了矩阵转换
//            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
//                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
//            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
//            // 下面这里按照二维码的算法，逐个生成二维码的图片，
//            // 两个for循环是图片横列扫描的结果
//            for (int y = 0; y < QR_HEIGHT; y++) {
//                for (int x = 0; x < QR_WIDTH; x++) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * QR_WIDTH + x] = 0xff000000;
//                    } else {
//                        pixels[y * QR_WIDTH + x] = 0xffffffff;
//                    }
//                }
//            }
//            // 生成二维码图片的格式，使用ARGB_8888
//            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
//                    Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
//            // 显示到一个ImageView上面
//            // sweepIV.setImageBitmap(bitmap);
//            return bitmap;
//        } catch (WriterException e) {
//            Log.i("log", "生成二维码错误" + e.getMessage());
//            return null;
//        }
//    }

    private static final int BLACK = 0xff000000;

    /**
     * 生成一个二维码图像
     *
     * @param url            传入的字符串，通常是一个URL
     * @param widthAndHeight 图像的宽高
     * @return
     */
//    public static Bitmap createQRCode(String url, int widthAndHeight)
//            throws WriterException {
//        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        BitMatrix matrix = new MultiFormatWriter().encode(url,
//                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
//        int width = matrix.getWidth();
//        int height = matrix.getHeight();
//        int[] pixels = new int[width * height];
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = BLACK;
//                }
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        return bitmap;
//    }

    /**
     * 通过流解析图片
     *
     * @param context Context
     * @param resId   资源ID
     */
    public static Bitmap decodeResourceByStream(Context context, @DrawableRes @RawRes int resId) {
        InputStream inputStream = context.getApplicationContext().getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * 通过流解析图片
     *
     * @param context Context
     * @param resId   资源ID
     * @param options 配置项
     */
    public static Bitmap decodeResourceByStream(Context context, @DrawableRes @RawRes int resId, BitmapFactory.Options options) {
        InputStream inputStream = context.getApplicationContext().getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    // 回收imageview的图片资源
    public static void recycleImageViewBitmap(ImageView imageView) {
        if (imageView != null) {
            BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
            recycleBitmapDrawable(bd);
        }
    }

    // 回收view的背景资源
    public static void recycleBackgroundBitmap(View view) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(null);
            } else {
                view.setBackgroundResource(0);
            }
            BitmapDrawable bd = (BitmapDrawable) view.getBackground();
            recycleBitmapDrawable(bd);
        }
    }

    private static void recycleBitmapDrawable(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            recycleBitmap(bitmap);
        }
        bitmapDrawable = null;
    }

    private static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}