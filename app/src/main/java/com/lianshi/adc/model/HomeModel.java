package com.walter.adc.model;

import android.app.Application;

import cn.walter.library.mvvmbase.base.BaseModel;

public class HomeModel extends BaseModel {
    public HomeModel(Application application) {
        super(application);
    }
//
//    public Observable<BaseEntity<ArrayList<VideoListBean>>> findVideoGoods(int pageSize, String areaId, int categoryId, String time, String frontOrBack, boolean includeSelf) {
//        return mRetrofitClient.create(GoodsService.class)
//                .findVideoGoods(RetrofitUtils.getParamsMap("size", pageSize + "", "areaId", areaId, "categoryId", categoryId + "", "time", time, "frontOrBack", frontOrBack, "includeSelf", includeSelf + ""))
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//    }
}
