package cn.lianshi.library.mvvmbase.net;


import cn.lianshi.library.mvvmbase.base.BaseApplication;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import cn.lianshi.library.mvvmbase.utils.system.NetWorkStateUtils;
import cn.lianshi.library.mvvmbase.utils.ui.ToastUtils;
import rx.Subscriber;

/**
 *
 * 网络请求统一订阅处理类
 *
 */
public abstract class NetWorkSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
//        e.printStackTrace();
        LogUtils.w(e.getMessage());
        if (e instanceof ResponseThrowable) {
            ResponseThrowable rError =  (ResponseThrowable) e;
            ToastUtils.showToast(rError.getMessage());
            return;
        }
        //其他全部甩锅网络异常
        ToastUtils.showToast("网络异常");
    }

    @Override
    public void onStart() {
        super.onStart();
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetWorkStateUtils.isNetworkConnected(BaseApplication.getContext())) {
            ToastUtils.showToast("无网络");
           onCompleted();
        }
    }

    @Override
    public void onNext(T t) {
        onResult(t);
    }

    public abstract void onResult(T t);



}