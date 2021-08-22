package cn.lianshi.library.mvvmbase.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import byc.imagewatcher.ImageWatcher;
import cn.lianshi.library.mvvmbase.R;

/**
 * @author yuxiao
 * @date 2018/7/27
 * 仿造微信 图片查看器
 */
public class ImageWatcherUtils {

    public static ImageWatcher initImageWatcher(Activity activity) {
        return ImageWatcher.Helper.with(activity) // 一般来讲， ImageWatcher 需要占据全屏的位置
            .setErrorImageRes(R.mipmap.error_picture) // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
            .setLoader(new ImageWatcher.Loader() {//调用show方法前，请先调用setLoader 给ImageWatcher提供加载图片的实现
                @Override
                public void load(Context context, String url, final ImageWatcher.LoadCallback lc) {
                    Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            lc.onLoadFailed(errorDrawable);
                        }

                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            lc.onLoadStarted(placeholder);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            lc.onResourceReady(resource);
                        }
                    });
                }
            })
            .create();
    }
}
