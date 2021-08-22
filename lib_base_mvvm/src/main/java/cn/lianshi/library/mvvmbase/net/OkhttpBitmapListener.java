package cn.lianshi.library.mvvmbase.net;

import android.graphics.Bitmap;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by yx on 2017/6/20.
 * 图片请求返回接口
 */

public interface OkhttpBitmapListener {
    void onRequestStart(Request request, int id);

    void onRequestComplete(int id);

    void onRequestError(Call call, Exception e, int id);

    void onRequestSuccess(Bitmap bitmap, int id);
}
