package cn.walter.library.mvvmbase.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import cn.walter.library.mvvmbase.bus.rx.RxBus;
import cn.walter.library.mvvmbase.bus.rx.RxCodeConstants;
import cn.walter.library.mvvmbase.config.AppManager;

/**
 * @author yuxiao
 * @date 2019/1/26
 * app activity生命周期回调实现
 */
public class AppActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private int mActivityRefCount = 0;//actvity的引用个数

    private boolean isAppOnBackgound = false;//判断app是否出于过后台


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        AppManager.INSTANCE.addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityRefCount++;

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isAppOnBackgound){
            isAppOnBackgound = false;
            RxBus.getDefault().post(RxCodeConstants.APP_LIFECYCLE_FORREGROUND,activity);

        }

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityRefCount--;

        if (mActivityRefCount == 0) {
            //app处于后台
            isAppOnBackgound = true;

            RxBus.getDefault().post(RxCodeConstants.APP_LIFECYCLE_BACKGROUND,activity);
        }


    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        AppManager.INSTANCE.removeActivity(activity);

    }

    public int getActivityRefCount() {
        return mActivityRefCount;
    }
}
