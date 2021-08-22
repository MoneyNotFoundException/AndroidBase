package cn.lianshi.library.mvvmbase.base;

import android.app.Application;

import android.content.Context;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.lianshi.library.mvvmbase.bus.event.SingleLiveEvent;
import cn.lianshi.library.mvvmbase.utils.LogUtils;


/**
 * @author yuxiao
 * @date 19/1/15
 * 基类ViewModel
 */
public class BaseViewModel<M extends IBaseModel> extends AndroidViewModel implements IBaseViewModel {

    private UIChangeLiveData uc;
    private LifecycleProvider lifecycle;

    protected M mModel;


    public BaseViewModel(@NonNull Application application) {
        super(application);
        initModel();
    }

    /**
     * 反射获取model
     */
    protected void initModel() {
        if (mModel != null) {
            return;
        }
        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            //如果没有指定泛型参数，则默认使用BaseModel
            modelClass = BaseModel.class;
        }
        try {
            mModel = (M) modelClass.getConstructor(Application.class).newInstance(getApplication());
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }

    }


    /**
     * 注入RxLifecycle生命周期
     *
     * @param lifecycle
     */
    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = lifecycle;
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycle;
    }

    /**
     * 获取上下文，如果不是activiyt或者fragment 上下文 那就返回全局上下文
     *
     * @return
     */
    public Context getContext() {
        if (lifecycle instanceof RxAppCompatActivity) {
            return (Context) lifecycle;
        } else if (lifecycle instanceof RxFragment) {
            return ((RxFragment) lifecycle).getContext();
        }
        return BaseApplication.getContext();
    }

    public UIChangeLiveData getUC() {
        if (uc == null) {
            uc = new UIChangeLiveData();
        }
        return uc;
    }

    public void showLoadingDialog() {
        showLoadingDialog("加载中");
    }

    public void showLoadingDialog(String title) {
        uc.showDialogEvent.postValue(title);
    }

    public void showLoadingNoCancelDialog(String title) {
        uc.showDialogNoCancelEvent.postValue(title);
    }

    public void hideLoadingDialog() {
        uc.dismissDialogEvent.call();
    }

    /**
     * 关闭界面
     */
    public void finishView() {
        uc.finishEvent.call();
    }

    public void finishViewWithOk(Intent intent) {
        uc.finishWithOkEvent.postValue(intent);
    }

    /**
     * 返回上一层
     */
    public void onBackPressed() {
        uc.onBackPressedEvent.call();
    }

    public void showProgressWheel() {
        uc.showProgressWheelEvent.call();
    }

    public void hideProgressWheel() {
        uc.hideProgressWheelEvent.call();
    }

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {

    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (mModel != null) {
            mModel.onDestroy();
            mModel = null;
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void registerRxBus() {
    }

    @Override
    public void removeRxBus() {
    }


    public final class UIChangeLiveData extends SingleLiveEvent {

        private SingleLiveEvent<String> showDialogEvent;
        private SingleLiveEvent<String> showDialogNoCancelEvent;
        private SingleLiveEvent<Void> dismissDialogEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Intent> finishWithOkEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;
        private SingleLiveEvent<Void> showProgressWheelEvent;
        private SingleLiveEvent<Void> hideProgressWheelEvent;

        public SingleLiveEvent<String> getShowDialogEvent() {
            return showDialogEvent = createLiveData(showDialogEvent);
        }

        public SingleLiveEvent<String> getShowDialogNoCancelEvent() {
            return showDialogNoCancelEvent = createLiveData(showDialogNoCancelEvent);
        }

        public SingleLiveEvent<Void> getDismissDialogEvent() {
            return dismissDialogEvent = createLiveData(dismissDialogEvent);
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public SingleLiveEvent<Intent> getFinishWithOKEvent() {
            return finishWithOkEvent = createLiveData(finishWithOkEvent);
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        public SingleLiveEvent<Void> getShowProgressWheelEvent() {
            return showProgressWheelEvent = createLiveData(showProgressWheelEvent);
        }

        public SingleLiveEvent<Void> getHideProgressWheelEvent() {
            return hideProgressWheelEvent = createLiveData(hideProgressWheelEvent);
        }


        private SingleLiveEvent createLiveData(SingleLiveEvent liveData) {
            if (liveData == null) {
                liveData = new SingleLiveEvent();
            }
            return liveData;
        }


        @Override
        public void observe(LifecycleOwner owner, Observer observer) {
            super.observe(owner, observer);
        }
    }


}
