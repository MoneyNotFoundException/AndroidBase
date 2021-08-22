package cn.lianshi.library.mvvmbase.utils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.lianshi.library.mvvmbase.utils.LogUtils;

/**
 * @author yuxiao
 * @date 2018/9/14
 * 截图工具类
 */
public class CaptureUtils {

    /**
     * 对View进行量测，布局后截图
     * 对webvie不好使，不可见部分为空白
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        view.destroyDrawingCache();
        return bitmap;
    }


    /**
     * 获取整个窗口的截图
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();
        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }

        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        cv.destroyDrawingCache();

        return bmp;
    }

    /**
     * 对单独某个View进行截图
     *
     * @param v
     * @return
     */
    public static Bitmap buildBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        c.save();
        c.restore();
        return screenshot;
    }


    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebView(WebView webView) {
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Picture snapShot = webView.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        canvas.save();
        canvas.restore();
        webView.destroyDrawingCache();
        return bmp;
    }

    /**
     * 合并两张bitmap为一张
     *
     * @param background
     * @param foreground
     * @return Bitmap
     */
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap
            .createBitmap(bgWidth, bgHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2,
            (bgHeight - fgHeight) / 2, null);
        canvas.save();
        canvas.restore();
        return newmap;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 截取scrollview的屏幕
     **/
    public static Bitmap getBitmapByScrollView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }

        LogUtils.w("实际高度:" + h);
        LogUtils.w(" 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
            Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    /**
     *
     * 获取截取后的长截图
     * @param resource
     */
    public static Bitmap getClipLongBitmap(Bitmap resource) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resource.compress(Bitmap.CompressFormat.PNG, 100, baos);

            InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

            //BitmapRegionDecoder newInstance(InputStream is, boolean isShareable)
            //用于创建BitmapRegionDecoder，isBm表示输入流，只有jpeg和png图片才支持这种方式，
            // isShareable如果为true，那BitmapRegionDecoder会对输入流保持一个表面的引用，
            // 如果为false，那么它将会创建一个输入流的复制，并且一直使用它。即使为true，程序也有可能会创建一个输入流的深度复制。
            // 如果图片是逐步解码的，那么为true会降低图片的解码速度。如果路径下的图片不是支持的格式，那就会抛出异常
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(isBm, true);

            final int imgWidth = decoder.getWidth();
            final int imgHeight = decoder.getHeight();

            BitmapFactory.Options opts = new BitmapFactory.Options();

            //计算图片要被切分成几个整块，
            // 如果sum=0 说明图片的长度不足3000px，不进行切分 直接添加
            // 如果sum>0 先添加整图，再添加多余的部分，否则多余的部分不足3000时底部会有空白
            int sum = imgHeight / 3000;

            int redundant = imgHeight % 3000;

            List<Bitmap> bitmapList = new ArrayList<>();

            //说明图片的长度 < 3000
            if (sum == 0) {
                //直接加载
                bitmapList.add(resource);
            } else {
                //说明需要切分图片
                Rect rect = new Rect();
                for (int i = 0; i < sum; i++) {
                    //需要注意：mRect.set(left, top, right, bottom)的第四个参数，
                    //也就是图片的高不能大于这里的4096
                    rect.set(0, i * 3000, imgWidth, (i + 1) * 3000);
                    Bitmap bm = decoder.decodeRegion(rect, opts);
                    bitmapList.add(bm);
                }

                //将多余的不足3000的部分作为尾部拼接
                if (redundant > 0) {
                    rect.set(0, sum * 3000, imgWidth, imgHeight);
                    Bitmap bm = decoder.decodeRegion(rect, opts);
                    bitmapList.add(bm);
                }

            }

            Bitmap bigbitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.RGB_565);
            Canvas bigcanvas = new Canvas(bigbitmap);

            Paint paint = new Paint();
            int iHeight = 0;

            //将之前的bitmap取出来拼接成一个bitmap
            for (int i = 0; i < bitmapList.size(); i++) {
                Bitmap bmp = bitmapList.get(i);
                bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
                iHeight += bmp.getHeight();

                bmp.recycle();
                bmp = null;
            }

            return bigbitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
