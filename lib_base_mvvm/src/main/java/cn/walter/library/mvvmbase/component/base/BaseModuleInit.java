package cn.walter.library.mvvmbase.component.base;

import android.app.Application;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.smtt.export.external.DexClassLoaderProviderService;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.zxy.recovery.core.Recovery;

import org.litepal.LitePal;

import java.util.HashMap;

import cn.walter.library.mvvmbase.BuildConfig;
import cn.walter.library.mvvmbase.utils.LogUtils;
import cn.walter.library.mvvmbase.utils.SpUtils;

/**
 * 基础库自身初始化操作
 */

public class BaseModuleInit implements IModuleInit {

    @Override
    public boolean onInitAhead(Application application) {

        //初始化阿里路由框架
        if (BuildConfig.DEBUG) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
        //LitePal初始化
        LitePal.initialize(application);
        LogUtils.initLog();        //开启打印日志
        SpUtils.init(application);//初始化sp工具

        LogUtils.e("基础层初始化 -- onInitAhead");
        return false;
    }


    @Override
    public boolean onInitLow(Application application) {
        initX5Core(application);//腾讯tbs  X5内核
        initRecover(application);
        LogUtils.e("基础层初始化 -- onInitLow");
        return false;
    }


    /**
     * X5内核初始化
     * X5WebView首次初始化X5内核耗时，会产生卡顿现象的解决办法
     * 设置开启优化方案// 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案
     * 并申明service
     *
     * @param application
     */
    private void initX5Core(Application application) {

        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        QbSdk.initTbsSettings(map);

        //3.由于coloros的OPPO手机自动熄屏一段时间后，会启用系统自带的电量优化管理，禁止一切自启动的APP（用户设置的自启动白名单除外），需要try catch
        try {

//            X5InitService.startService(application);
            Intent intent = new Intent(application, DexClassLoaderProviderService.class);
            application.startService(intent);


        } catch (Exception e) {
            LogUtils.w("X5init Service error -> ");
            e.printStackTrace();
        }


        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                LogUtils.w(" onViewInitFinished is : " + arg0);

            }

            @Override
            public void onCoreInitFinished() {
                LogUtils.w(" onCoreInitFinished ");

            }
        };

        QbSdk.initX5Environment(application, cb);

    }

    /**
     * bug捕捉
     *
     * @param application
     */
    private void initRecover(Application application) {
        if (BuildConfig.DEBUG) {
            Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .recoverEnabled(true)
//                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//                .skip(TestActivity.class)
                .init(application);
        }
    }
}
