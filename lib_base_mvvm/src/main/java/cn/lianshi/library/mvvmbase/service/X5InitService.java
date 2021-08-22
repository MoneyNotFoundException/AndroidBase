package cn.lianshi.library.mvvmbase.service;

import android.content.Context;
import android.content.Intent;

import com.tencent.smtt.export.external.DexClassLoaderProviderService;

/**
 * @author yuxiao
 * @date 2019/4/12
 * <p>
 * 适配Android8.0 9.0服务
 * <p>
 * 增加Service声明 ：在AndroidManifest.xml中增加内核首次加载时优化Service声明； 该Service仅在TBS内核首次Dex加载时触发并执行dex2oat任务，任务完成后自动结束
 */
public class X5InitService extends DexClassLoaderProviderService {

    public static final String CHANNEL_ID_STRING = "zjc001";

    @Override
    public void onCreate() {
        super.onCreate();

    }


    /**
     * 开启服务
     *
     * @param context
     */
    public static void startService(Context context) {
        Intent intent = new Intent(context, X5InitService.class);
        context.startService(intent);

    }

}
