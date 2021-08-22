package cn.lianshi.thirdlib.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.IdRes;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;

import cn.lianshi.library.mvvmbase.net.HttpUrlApi;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yx on 2017/7/13.
 * 友盟分享工具类
 */

public class UmShareUtils {

    private Activity mContext;
    private String mTitle;
    private String mContents;
    private String mTargetUrl;
    private Bitmap mBitmap;
    private UMImage mUmImage;
    private SHARE_MEDIA[] mDisplaylist;
    private SHARE_MEDIA mPlatform;
    private UMShareListener mUMShareListener;


    public static class Builder {

        private Activity mContext;
        private String mTitle;
        private String mContents;
        private String mTargetUrl;
        private UMImage mUmImage;
        private Bitmap mBitmap;
        private SHARE_MEDIA[] mDisplaylist;
        private SHARE_MEDIA mPlatform;
        private UMShareListener umShareListener;


        public Builder(Activity context) {

            this.mContext = context;
        }

        /**
         * 设置分享标题
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {

            this.mTitle = title;
            return this;
        }


        /**
         * @param bitmap 分享图片
         * @return
         */
        public UmShareUtils.Builder setBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                this.mBitmap = bitmap;
            }

            return this;
        }

        /**
         * 设置分享内容
         *
         * @param contents
         * @return
         */
        public Builder setContents(String contents) {

            this.mContents = contents;
            return this;
        }

        /**
         * 设置分享链接
         *
         * @param targetUrl
         * @return
         */
        public Builder setTargetUrl(String targetUrl) {

            this.mTargetUrl = targetUrl;
            return this;
        }

        /**
         * 分享图标的远程url
         *
         * @param imageUrl
         * @return
         */
        public Builder setImageUrl(String imageUrl) {

            if (!TextUtils.isEmpty(imageUrl)) {

                this.mUmImage = new UMImage(mContext, imageUrl);

            }
            return this;
        }

        /**
         * 使用本地分享图标
         *
         * @param imageId
         * @return
         */
        public Builder setImageUrl(@IdRes int imageId) {
            if (imageId != 0) {

                this.mUmImage = new UMImage(mContext, imageId);
            }
            return this;
        }

        public Builder setLongImageBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                this.mUmImage = new UMImage(mContext, bitmap);
                UMImage umThumb = new UMImage(mContext, bitmap);
                mUmImage.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
                mUmImage.setThumb(umThumb);

            }
            return this;
        }

        public Builder setImageBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                this.mUmImage = new UMImage(mContext, bitmap);
            }
            return this;
        }

        /**
         * 设置分享平台
         *
         * @param displaylist
         * @return
         */
        public Builder setShareList(SHARE_MEDIA[] displaylist) {

            if (displaylist == null) {
                throw new NullPointerException("mDisplaylist is null...");
            }

            this.mDisplaylist = displaylist;

            return this;
        }

        /**
         * 设置分享平台
         *
         * @return
         */
        public Builder setAllShareList() {

            this.mDisplaylist = new SHARE_MEDIA[]{SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA};

            return this;
        }

        /**
         * 添加一个自定义分享平台
         *
         * @param platform
         * @return
         */
        public Builder setPlatform(SHARE_MEDIA platform) {

            this.mPlatform = platform;

            return this;
        }

        //设置分享监听回调
        public Builder setUmShareListener(UMShareListener shareListener) {
            this.umShareListener = shareListener;
            return this;
        }

        public UmShareUtils build() {
            return new UmShareUtils(this);
        }
    }

    private UmShareUtils(Builder builder) {

        this.mContext = builder.mContext;
        this.mContents = builder.mContents;
        this.mBitmap = builder.mBitmap;
        this.mTargetUrl = builder.mTargetUrl;
        this.mTitle = builder.mTitle;
        this.mUmImage = builder.mUmImage;
        this.mDisplaylist = builder.mDisplaylist;
        this.mPlatform = builder.mPlatform;
        this.mUMShareListener = builder.umShareListener;


    }


    /**
     * 展示分享dialog并分享
     */
    public void show() {

        show(false);


    }

    public void show(boolean isVideo) {
        if (mDisplaylist == null) {
            throw new NullPointerException("mDisplaylist is null...");
        }

        if (isVideo) {
            new ShareAction(mContext)
                .withMedia(getUmVideo())
                .setDisplayList(mDisplaylist)
                .setCallback(mUMShareListener)
                .open();
        } else {
            new ShareAction(mContext)
                .withMedia(getUmWeb())
                .setDisplayList(mDisplaylist)
                .setCallback(mUMShareListener)
                .open();
        }

    }


    public void showLongImage() {

        new ShareAction(mContext)
            .setDisplayList(mDisplaylist)
            .setCallback(mUMShareListener)
            .withMedia(mUmImage)
            .open();

    }


    public static SHARE_MEDIA[] getShareMediaList() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
            new SHARE_MEDIA[]{SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE} :
            new SHARE_MEDIA[]{SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA};
    }

    /**
     * 分享单个平台
     */
    public void share() {
        new ShareAction(mContext)
            .withMedia(getUmWeb())
            .setPlatform(mPlatform)
            .setCallback(mUMShareListener)
            .share();
    }

    //获取um分享链接设置
    private UMWeb getUmWeb() {
        UMWeb web = new UMWeb(mTargetUrl);
        web.setTitle(mTitle);//标题
        web.setThumb(mUmImage);  //缩略图
        web.setDescription(mContents);//描述
        return web;
    }

    private UMVideo getUmVideo() {
        UMVideo video = new UMVideo(mTargetUrl);
        return video;
    }


    public void showBitmap() {
        if (this.mDisplaylist == null) {
            throw new NullPointerException("mDisplaylist is null...");
        } else {
            Observable.just(new UMImage(this.mContext, this.mBitmap)).map((umImage) -> {
                umImage.setTitle(this.mTitle);
                umImage.setThumb(this.mUmImage);
                umImage.setDescription(this.mContents);
                return umImage;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<UMImage>() {
                public void onCompleted() {
                }

                public void onError(Throwable e) {
                    LogUtils.e("getUmBitmap-->" + e);
                }

                public void onNext(UMImage umImage) {
                    new ShareAction(mContext)
                        .withMedia(umImage)
                        .setDisplayList(mDisplaylist)
                        .open();
                }
            });
        }
    }


}
