package cn.walter.thirdlib;

import android.app.Application;

import cn.walter.library.mvvmbase.component.base.IModuleInit;
import cn.walter.thirdlib.config.ThirdLibConfig;

/**
 * @author yuxiao
 * @date 2019/1/16
 * 三方库组件初始化
 */
public class ThirdLibModuleInit implements IModuleInit {

    @Override
    public boolean onInitAhead(Application application) {
        //初始化三方库
        ThirdLibConfig.initThirdLib(application);
        return false;
    }

    @Override
    public boolean onInitLow(Application application) {

        return false;
    }
}
