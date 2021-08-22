package cn.lianshi.library.mvvmbase.binding.viewadapter.image;


import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.lianshi.library.mvvmbase.utils.ui.GlideCircleTransform;
import jp.wasabeef.glide.transformations.BlurTransformation;


public final class ViewAdapter {

    @BindingAdapter(value = {"url", "placeholderRes","imgStyle"}, requireAll = false)
    public static void setImageUri(ImageView imageView, String url, int placeholderRes,String imgStyle) {
        //使用Glide框架加载图片
        DrawableRequestBuilder<String> builder = Glide.with(imageView.getContext())
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .centerCrop()
            .placeholder(placeholderRes);

        if (imgStyle != null) {
            switch (imgStyle) {
                case "0":
                    //圆形
                    builder.bitmapTransform(new GlideCircleTransform(imageView.getContext()));
                    break;
                case "1":
                    //高斯模糊
                    builder.bitmapTransform(new BlurTransformation(imageView.getContext(), 25, 5));
                    break;
                default:
                    break;
            }

        }

        builder.into(imageView);

    }

    @BindingAdapter(value = {"imageRes"},requireAll = false)
    public static void setImageResource(ImageView imageView, int resId){
        imageView.setImageResource(resId);
    }


}

