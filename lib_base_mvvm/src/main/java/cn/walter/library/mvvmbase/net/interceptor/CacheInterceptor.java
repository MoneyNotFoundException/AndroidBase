package cn.walter.library.mvvmbase.net.interceptor;

import android.content.Context;

import java.io.IOException;

import cn.walter.library.mvvmbase.utils.system.NetWorkStateUtils;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 有网状态下60秒
 * 无网络状态下读取缓存的拦截器
 */
public class CacheInterceptor implements Interceptor {

    private Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetWorkStateUtils.isNetworkConnected(context)) {
            Response response = chain.proceed(request);
            // read from cache for 60 s
            int maxAge = 60;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            //读取缓存信息
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            //set cache times is 3 days
            int maxStale = 60 * 60 * 24 * 3;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    }
}
