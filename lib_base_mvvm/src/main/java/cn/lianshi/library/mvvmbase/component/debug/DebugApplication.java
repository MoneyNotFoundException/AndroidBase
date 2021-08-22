package cn.lianshi.library.mvvmbase.component.debug;

import android.content.Context;


import androidx.multidex.MultiDex;

import cn.lianshi.library.mvvmbase.base.BaseApplication;
import cn.lianshi.library.mvvmbase.component.config.ModuleLifecycleConfig;

/**
 *
 * debug包下的代码不参与编译，仅作为独立模块运行时初始化数据
 */

public class DebugApplication extends BaseApplication {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);//分包

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化组件(靠前)
        ModuleLifecycleConfig.getInstance().initModuleAhead(this);
        //....
        //初始化组件(靠后)
        ModuleLifecycleConfig.getInstance().initModuleLow(this);
    }
}
