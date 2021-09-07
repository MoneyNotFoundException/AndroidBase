package cn.walter.library.mvvmbase.component.base;

import android.app.Application;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
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
