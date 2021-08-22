package cn.lianshi.library.mvvmbase.net;


import android.graphics.Bitmap;

import androidx.collection.ArrayMap;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.lianshi.library.mvvmbase.utils.GsonUtil;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * Created by yx on 2016/10/17.
 * retrofit网络请求加解密类
 */

public class RetrofitUtils {

    /**
     * 获取参数map集合
     *
     * @param str
     * @return
     */
    public static Map<String, String> getParamsMap(String... str) {
        if (str.length < 2 && str.length % 2 != 0) {
            throw new IllegalStateException("参数个数必须是2的倍数!!!");
        }
        ArrayMap<String, String> map = new ArrayMap<>();
        for (int i = 0; i < str.length; i++) {
            if (i % 2 == 1) {
                map.put(str[i - 1], str[i]);
            }
        }
        return map;
    }

    public static Map<String, String> constructMap(String... str) {
        return getParamsMap(str);
    }

    /**
     * obj转json字符串
     *
     * @param obj
     * @return
     */
    public static String paramsToJsonString(Object... obj) {

        if (obj.length == 1 && obj[0].equals("")) {
            //返回空json
            return "{}";
        }

        if (obj.length % 2 != 0) {
            throw new IllegalStateException("参数个数必须是2的倍数!!!");
        }


        StringBuffer sb = new StringBuffer();

        int i = 0;
        sb.append("{");
        for (Object o : obj) {
            i++;
            if (o instanceof Boolean || o instanceof Integer) {
                sb.append(o);
            } else {
                sb.append("\"");
                sb.append(o);
                sb.append("\"");
            }
            if (i % 2 == 1) {
                sb.append(":");
            } else {
                if (i != obj.length) {
                    sb.append(",");
                }
            }
        }
        sb.append("}");
        return sb.toString();

    }

    /**
     * 判断返回的额json里面是否包含token
     * 如果token失效，就重新登录
     *
     * @param json
     */
    private static void judgeToken(String json) {
//        LogUtils.w("json -> " + json);
        if (json != null && json.contains("\"token\",\"msg\"")) {



        }

    }

    /**
     * 将返回的实体转化成String
     *
     * @return
     */
    public static Func1<ResponseBody, String> mapToString() {
        return new Func1<ResponseBody, String>() {
            @Override
            public String call(ResponseBody responseBody) {
                String string = "";
                try {
                    string = responseBody.string();
                    judgeToken(string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return string;
            }
        };
    }


    /**
     * 将String json 转化为实体
     *
     * @param tClazz
     * @param <T>
     * @return
     */
    public static <T> Func1<String, T> mapJsonStringToEntity(Class<T> tClazz) {
        return new Func1<String, T>() {
            @Override
            public T call(String json) {
                return GsonUtil.fromJson(json, tClazz);
            }
        };

    }

    /**
     * okhttp get图片请求
     *
     * @param lisenter
     */
    public static void getBitmapHttpRequest(OkhttpBitmapListener lisenter, String url) {

        GetBuilder builder = OkHttpUtils.get().url(url);
        RequestCall requestCall = builder.build();
        requestCall.execute(new BitmapCallback() {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                lisenter.onRequestStart(request, id);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                lisenter.onRequestComplete(id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                lisenter.onRequestError(call, e, id);
            }

            @Override
            public void onResponse(Bitmap response, int id) {
                Logger.wtf("bitmap == null + " + String.valueOf(response == null));
                lisenter.onRequestSuccess(response, id);

            }
        });

    }

}
