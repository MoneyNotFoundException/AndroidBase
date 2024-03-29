package cn.walter.library.mvvmbase.bus.rx.retry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * @author yuxiao
 * @date 2018/7/19
 * rx 广播订阅者.判断有无网络
 */
public class BroadcastObservable implements Observable.OnSubscribe<Boolean> {

    private final Context context;

    public static Observable<Boolean> fromConnectivityManager(Context context) {
        return Observable.create(new BroadcastObservable(context)).share();
    }

    public BroadcastObservable(Context context) {
        this.context = context;
    }

    @Override
    public void call(Subscriber<? super Boolean> subscriber) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                subscriber.onNext(isConnectedToInternet());
            }
        };

        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        subscriber.add(unsubscribeInUiThread(() -> context.unregisterReceiver(receiver)));
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static Subscription unsubscribeInUiThread(final Action0 unsubscribe) {
        return Subscriptions.create(() -> {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                unsubscribe.call();
            } else {
                final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
                inner.schedule(() -> {
                    unsubscribe.call();
                    inner.unsubscribe();
                });
            }
        });
    }

}
