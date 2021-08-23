package cn.walter.library.mvvmbase.base;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.litepal.LitePalApplication;

import cn.walter.library.mvvmbase.R;
import cn.walter.library.mvvmbase.utils.ui.DensityUtils;


/**
 * base Application
 *
 * @date 19/1/16
 */

public class BaseApplication extends LitePalApplication {

    //static 代码段可以防止内存泄露
    static {

        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//                layout.setPrimaryColorsId(R.color.base_colorPrimaryDark, android.R.color.white);//全局设置主题颜色
            ClassicsHeader header = new ClassicsHeader(context);
            header.setArrowResource(R.drawable.base_head_down_arrow);
            header.setTextSizeTitle(14);
            header.setEnableLastTime(false);
            header.setFinishDuration(0);
            header.setMinimumHeight(DensityUtils.dp2px(context, 60));
            return header;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter footer = new ClassicsFooter(context);
            footer.setTextSizeTitle(14);//同上
            footer.setMinimumHeight(DensityUtils.dp2px(context, 40));
            footer.setFinishDuration(0);
            footer.setEnabled(false);
            layout.setEnableLoadMore(false);
            return footer;
        });

    }


    private static AppActivityLifecycle mAppActivityLifecycle;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);//分包

    }


    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycle();
    }


    /**
     * 注册app activity生命周期
     */
    private void registerActivityLifecycle() {
        mAppActivityLifecycle = new AppActivityLifecycle();
        registerActivityLifecycleCallbacks(mAppActivityLifecycle);
    }

    /**
     * 判断app是否处于后台
     * @return
     */
    public static boolean isAppBackGround() {
        return mAppActivityLifecycle.getActivityRefCount() == 0;
    }

    /**
     * 获取activity引用个数
     * @return
     */
    public static int getActivityRefCount() {
        return mAppActivityLifecycle.getActivityRefCount();
    }



}
