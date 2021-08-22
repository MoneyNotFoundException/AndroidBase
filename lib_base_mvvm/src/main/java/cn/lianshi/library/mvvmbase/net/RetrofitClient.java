package cn.lianshi.library.mvvmbase.net;

import android.content.Context;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.lianshi.library.mvvmbase.config.AppConfig;
import cn.lianshi.library.mvvmbase.net.api.AppNetService;
import cn.lianshi.library.mvvmbase.net.cookie.CookieJarImpl;
import cn.lianshi.library.mvvmbase.net.cookie.store.PersistentCookieStore;
import cn.lianshi.library.mvvmbase.net.interceptor.BaseInterceptor;
import cn.lianshi.library.mvvmbase.net.interceptor.CacheInterceptor;
import cn.lianshi.library.mvvmbase.net.interceptor.LoggerInterceptor;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yx on 2016/7/15.
 * 网络请求构建
 */
public class RetrofitClient {

    private static volatile RetrofitClient instance;

    private static final int CONNECT_TIMEOUT = 30;
    private static final int UTILS_CONNECT_TIMEOUT = 120;

    private static final int CACHE_FILE_SIZE = 20 * 1024 * 1024;//缓存文件大小


    private OkHttpClient mOkHttpClient;
    private OkHttpClient mOkHttpClientUtils;//工具
    private Retrofit mRetrofit;
    private Retrofit mRetrofitUtils;

    //缓存
    private Cache mHttpCache = null;
    private File httpCacheDirectory;


    public static RetrofitClient getInstance(Context context) {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient(context);
                }
            }
        }
        return instance;
    }

    private RetrofitClient(Context context) {
        gradleRetrofit(context);
        gradleGongjuRetrofit(context);
    }


    /**
     * 构建okhttp
     */
    public OkHttpClient gradleOkHttp(Context context) {
        if (mOkHttpClient == null) {
            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(context.getCacheDir(), "app_http_cache");

                if (!httpCacheDirectory.exists()) {
                    httpCacheDirectory.mkdirs();
                }
            }

            try {
                if (mHttpCache == null) {
                    mHttpCache = new Cache(httpCacheDirectory, CACHE_FILE_SIZE);
                }
            } catch (Exception e) {
                LogUtils.w("Could not create http cache" + e.getMessage());
            }

            HttpsSecurityUtils.SSLParams sslParams = HttpsSecurityUtils.getSslSocketFactory();
            HashMap<String, String> headers = new HashMap<>();
            mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new BaseInterceptor(headers))//基础拦截器,可添加header
                .addInterceptor(new CacheInterceptor(context))//缓存拦截器
                .addInterceptor(new LoggerInterceptor(true, true))//log打印
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .cache(mHttpCache)//设置缓存
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(context)))//持久化session
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)//信任证书
                .hostnameVerifier((hostname, session) -> true)
                .build();

        }
        return mOkHttpClient;
    }

    /**
     * 构建社保和公积金okhttp
     * 网络加载有时候较慢,设置120秒超时
     *
     * @param context
     * @return
     */
    private OkHttpClient gradleSbAndGjjOkHttp(Context context) {
        if (mOkHttpClientUtils == null) {
            mOkHttpClientUtils = gradleOkHttp(context)
                .newBuilder()
                .connectTimeout(UTILS_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(UTILS_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(UTILS_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        }
        return mOkHttpClientUtils;
    }


    /**
     * 构建Retrofit
     *
     * @return
     */
    private void gradleRetrofit(Context context) {
        if (mRetrofit == null) {
            OkHttpClient okHttpClient = gradleOkHttp(context);
            mRetrofit = new Retrofit.Builder()
                .baseUrl(HttpUrlApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        }
    }

    /**
     * 共建工具retrofit
     *
     * @param context
     */
    public Retrofit gradleGongjuRetrofit(Context context) {
        if (mRetrofitUtils == null) {
            OkHttpClient okHttpClient = gradleSbAndGjjOkHttp(context);
            mRetrofitUtils = new Retrofit.Builder()
                .baseUrl(HttpUrlApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        }
        return mRetrofitUtils;
    }


    /**
     * 创建Base_url service
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return mRetrofit.create(service);
    }

    /**
     * 创建base_utils_url service
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T createUtilsService(Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return mRetrofitUtils.create(service);
    }

    /**
     * /**
     * execute your customer API
     * For example:
     * MyApiService service =
     * RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
     * <p>
     * RetrofitClient.getInstance(MainActivity.this)
     * .execute(service.lgon("name", "password"), subscriber)
     * * @param subscriber
     */

    public static <T> T execute(Observable<T> observable, Subscriber<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber);

        return null;
    }
}

