package com.walter.adc.model;

import android.app.Application;

import cn.walter.library.mvvmbase.base.BaseModel;

public class UserInfoModel extends BaseModel {

    public UserInfoModel(Application application) {
        super(application);
    }

//    public Observable<BaseEntity<String>> verifyLivingResult() {
//        return mRetrofitClient.create(UserInfoService.class)
//                .verifyLivingResult()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
}
