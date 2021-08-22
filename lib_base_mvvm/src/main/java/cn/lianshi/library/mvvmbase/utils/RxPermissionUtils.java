package cn.lianshi.library.mvvmbase.utils;

import android.Manifest;
import android.app.Activity;


import androidx.annotation.StringDef;

import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.lianshi.library.mvvmbase.config.AppManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author yuxiao
 * @date 2019/1/28
 * RX permission工具类
 */
public class RxPermissionUtils {

    //目前需要动态获取的权限是这几个,如果想在其他的地方调不同的权限，需添加
    //定位
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    //手机状态
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    //读写外部存储
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    //相机
    public static final String CAMERA = Manifest.permission.CAMERA;
    //读写日历
    public static final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;
    public static final String WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR;
    //读写联系人
    public static final String WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    //录音
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    //读取短信
    public static final String READ_SMS = Manifest.permission.READ_SMS;
    //拨打电话
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;



    @StringDef({ACCESS_COARSE_LOCATION,ACCESS_FINE_LOCATION,READ_PHONE_STATE,WRITE_EXTERNAL_STORAGE,
        CAMERA,READ_CALENDAR,WRITE_CALENDAR,WRITE_CONTACTS,READ_CONTACTS,RECORD_AUDIO,READ_SMS,CALL_PHONE})
    @Retention(RetentionPolicy.SOURCE)
    private  @interface ManifestPermission {}


    private RxPermissionUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     *
     * 检查权限，只返回全部通过为true，其他为false
     * @param permission
     * @return
     */
    public static Observable<Boolean> checkPermission(@ManifestPermission String...permission) {
        Activity activity = AppManager.INSTANCE.currentActivity();
        if (activity==null){
            return Observable.just(false)
                .subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
        }
        return new RxPermissions(activity)
            .request(permission)
            .subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());

    }


    /**
     *
     * 逐个检查权限，依次返回
     * @param permission
     * @return
     */
    public static Observable<Permission> checkEachPermission(@ManifestPermission String...permission) {
        Activity activity = AppManager.INSTANCE.currentActivity();
        if (activity==null){
            return Observable.just(new Permission(WRITE_EXTERNAL_STORAGE,true))
                .subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
        }
        return new RxPermissions(activity)
            .requestEach(permission)
            .subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * rx Transformer中check，相当于request
     * @param permission
     * @return
     */
    public static Observable.Transformer<Object, Permission> composePermissionEach(@ManifestPermission String... permission) {
        return new RxPermissions(AppManager.INSTANCE.currentActivity())
            .ensureEach(permission);
    }

    public static Observable.Transformer<Object, Boolean> composePermission(@ManifestPermission String... permission) {
        return new RxPermissions(AppManager.INSTANCE.currentActivity())
            .ensure(permission);
    }


}
