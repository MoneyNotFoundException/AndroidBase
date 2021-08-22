package cn.lianshi.library.mvvmbase.bus.rx.retry;

import java.util.concurrent.TimeUnit;

import cn.lianshi.library.mvvmbase.utils.LogUtils;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author yuxiao
 * @date 2018/7/19
 * rx 重试函数
 */
public class RetryWithDelay implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private int maxRetries;
    private int retryDelaySecond;
    private int retryCount;

    public RetryWithDelay(int maxRetries, int retryDelaySecond) {
        this.maxRetries = maxRetries;
        this.retryDelaySecond = retryDelaySecond;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
            .flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (++retryCount <= maxRetries) {
                        // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                        LogUtils.w("get error, it will try after " + retryDelaySecond
                            + " millisecond, retry count " + retryCount);
                        return Observable.timer(retryDelaySecond,
                            TimeUnit.SECONDS);
                    }
                    // Max retries hit. Just pass the error along.
                    return Observable.error(throwable);
                }
            });
    }
}

