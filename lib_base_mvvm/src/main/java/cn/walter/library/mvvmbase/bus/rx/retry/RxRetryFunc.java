package cn.walter.library.mvvmbase.bus.rx.retry;

import android.content.Context;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author yuxiao
 * @date 2018/7/19
 * rx 重试函数
 *
 */
public class RxRetryFunc implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int maxTimeout;
    private final TimeUnit timeUnit;
    private final Observable<Boolean> isConnected;
    private final int startTimeOut;
    private int timeout;

    public RxRetryFunc(Context context, int startTimeOut, int maxTimeout, TimeUnit timeUnit) {
        this.startTimeOut = startTimeOut;
        this.maxTimeout = maxTimeout;
        this.timeUnit = timeUnit;
        this.timeout = startTimeOut;
        isConnected = getConnectedObservable(context);
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.flatMap((Throwable throwable) -> {
            if (throwable instanceof UnknownHostException) {
                return isConnected;
            } else {
                return Observable.error(throwable);
            }
        }).compose(attachIncementalTimeout());
    }

    private Observable.Transformer<Boolean, Boolean> attachIncementalTimeout() {
        return observable -> observable.timeout(timeout, timeUnit)
            .doOnError(throwable -> {
                if (throwable instanceof TimeoutException) {
                    timeout = timeout > maxTimeout ? maxTimeout : timeout + startTimeOut;
                }
            });
    }

    private Observable<Boolean> getConnectedObservable(Context context) {
        return BroadcastObservable.fromConnectivityManager(context)
            .distinctUntilChanged()
            .filter(isConnected -> isConnected);
    }

}
