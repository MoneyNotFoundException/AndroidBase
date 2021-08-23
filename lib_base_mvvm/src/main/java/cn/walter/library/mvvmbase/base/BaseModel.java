package cn.walter.library.mvvmbase.base;

import android.app.Application;

import cn.walter.library.mvvmbase.database.LitePalManager;
import cn.walter.library.mvvmbase.net.RetrofitClient;

/**
 * @author yuxiao
 * @date 2019/1/16
 * 基类model，获取网络请求client和数据库对象，用于处理数据
 */
public class BaseModel implements IBaseModel {

    protected RetrofitClient mRetrofitClient;

    protected LitePalManager mLitepalManager;

    protected Application mApplication;

    public BaseModel(Application application) {

        this.mApplication = application;

        mRetrofitClient = RetrofitClient.getInstance(application);
        mLitepalManager = LitePalManager.getInstance();

    }

    @Override
    public void onDestroy() {

    }
}
