package com.lianshi.adc.config;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.bumptech.glide.Glide;

import cn.lianshi.library.mvvmbase.base.BaseApplication;
import cn.lianshi.library.mvvmbase.component.config.ModuleLifecycleConfig;
import cn.lianshi.library.mvvmbase.utils.LogUtils;

/**
 * Created by yx on 2016/7/12.
 * 启动Application
 */
public class RxApplication extends BaseApplication {

    public static RxApplication instance;

    private Context mContext;
    private Resources mResources;
    private boolean isupdateRes = false;




    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //初始化组件(靠前)
        ModuleLifecycleConfig.getInstance().initModuleAhead(this);

        init();
        initLogin();

        //初始化组件(靠后)
        ModuleLifecycleConfig.getInstance().initModuleLow(this);

    }

    public static synchronized RxApplication getInstance() {
        return instance;
    }


    private void init() {

        mContext = getApplicationContext();
        mResources = mContext.getResources();



    }

    /**
     * 初始化登录状态
     */
    public void initLogin() {


    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtils.w("onTerminate");
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }


    public synchronized Context getmContext() {
        return mContext;
    }

    public synchronized Resources getmResources() {

        try {
            if (!isupdateRes) {
                Configuration configuration = new Configuration();
                configuration.setToDefaults();
                if (mResources == null) {
                    mResources = getApplicationContext().getResources();
                }
                mResources.updateConfiguration(configuration, mResources.getDisplayMetrics());
                isupdateRes = true;
            }

        } catch (Exception e) {
            LogUtils.w("getmResources error -> " + e.getMessage());
        }


        return mResources;
    }

}
