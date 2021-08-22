package cn.lianshi.library.mvvmbase.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import cn.lianshi.library.mvvmbase.R;
import cn.lianshi.library.mvvmbase.bus.Messenger;
import cn.lianshi.library.mvvmbase.config.Constants;
import cn.lianshi.library.mvvmbase.databinding.BaseLayoutAppTitlebarBinding;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import cn.lianshi.library.mvvmbase.utils.system.AppSystemUtils;
import cn.lianshi.library.mvvmbase.utils.ui.DialogUtils;
import cn.lianshi.library.mvvmbase.widget.mdui.ProgressWheel;


/**
 * @author yuxiao
 * @date 19/1/15
 * 一个拥有DataBinding框架的基Activity
 * 这里根据项目业务可以换成你自己熟悉的BaseActivity, 但是需要继承RxAppCompatActivity,方便LifecycleProvider管理生命周期
 */
public abstract class BaseActivity<VB extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity implements IBaseView {


    public VB mVB;
    public VM mVM;
    private int mViewModelId;

    protected Context mContext;
    protected Bundle savedInstanceState;

    /**
     * MVVM DataBinding组件化的大坑，如果是多module下引入布局，会生成多个binding，
     * 合并之后找到的只是个view，必须重新bind一下,而且不能设置ViewModel
     */
    protected BaseLayoutAppTitlebarBinding mTitleBarBinding;

    private Dialog mLoadingDialog;

    private ProgressWheel mProgressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        mContext = this;
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding(savedInstanceState);
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack();
        setStatusBar();
        //初始化页面
        initView();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        //注册RxBus
        mVM.registerRxBus();
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.base_040406), 0);
//            StatusBarUtil.setLightMode(this);
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.base_040406), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除Messenger注册
        Messenger.getDefault().unregister(mVM);
        //解除ViewModel生命周期感应
        getLifecycle().removeObserver(mVM);
        mVM.removeRxBus();
        mVM = null;
        mVB.unbind();
        if (mTitleBarBinding != null) {
            mTitleBarBinding.unbind();
        }
    }

    /**
     * 注入绑定
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包，基于apt原理
        mVB = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        mViewModelId = initVariableId();
        mVM = initViewModel();
        if (mVM == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            mVM = (VM) createViewModel(this, modelClass);
        }
        //关联ViewModel
        mVB.setVariable(mViewModelId, mVM);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(mVM);
        //注入RxLifecycle生命周期
        mVM.injectLifecycleProvider(this);

        initTitleBarBinding();
        findProgressWheel();
    }

    /**
     * 设置titleBar的返回监听和标题
     */
    private void initTitleBarBinding() {
        View titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            mTitleBarBinding = DataBindingUtil.bind(titleBar);
            if (mTitleBarBinding != null) {
                mTitleBarBinding.llBack.setOnClickListener(v -> mVM.finishView());
                //这里不能通过autowire去自动加载，没有绑定path，必须通过intent去拿
                String title = getIntent().getStringExtra(Constants.VIEW_TITLE);
                if (title != null) {
                    mTitleBarBinding.tvTitlebarTitle.setText(title);
                }
            }
        }

    }

    /**
     * 寻找页面资源中的progress_wheel
     * 因为只有一层 所以可以直接取
     */
    private void findProgressWheel() {
        mProgressWheel = findViewById(R.id.progress_bar);
    }

    public ProgressWheel getProgressWheel() {
        return mProgressWheel;
    }

    //刷新布局
    public void refreshLayout() {
        if (mVM != null) {
            mVB.setVariable(mViewModelId, mVM);
        }
    }


    /**
     * =====================================================================
     **/
    //注册ViewModel与View的契约UI回调事件
    private void registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        mVM.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showLoadingDialog(title);
            }
        });
        mVM.getUC().getShowDialogNoCancelEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showLoadingDialog(title, false);
            }
        });
        //加载对话框消失
        mVM.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                hideLoadingDialog();
            }
        });
        //关闭界面
        mVM.getUC().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                finishActivity();
            }
        });
        //关闭页面并且返回result.ok
        mVM.getUC().getFinishWithOKEvent().observe(this, new Observer<Intent>() {

            @Override
            public void onChanged(@Nullable Intent intent) {
                setResult(Activity.RESULT_OK, intent);
                finishActivity();
            }
        });

        //关闭上一层
        mVM.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                onBackPressed();
            }
        });
        //展示progress wheel
        mVM.getUC().getShowProgressWheelEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                if (mProgressWheel != null) {
                    mProgressWheel.setVisibility(View.VISIBLE);
                }
            }
        });
        //隐藏progress wheel
        mVM.getUC().getHideProgressWheelEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                if (mProgressWheel != null) {
                    mProgressWheel.setVisibility(View.GONE);
                }
            }
        });
    }

    public Dialog showLoadingDialog() {
        return showLoadingDialog(R.string.base_loading);
    }

    public Dialog showLoadingDialog(int resid) {
        return showLoadingDialog(getString(resid));
    }

    public Dialog showLoadingDialog(String message) {
        return showLoadingDialog(message, true);
    }

    public Dialog showLoadingDialog(String message, boolean isCancel) {
        if (!this.isFinishing()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtils.getWaitDialog(this, message, isCancel);
            }
            if (mLoadingDialog != null) {
                AppCompatTextView tvMsg = mLoadingDialog.findViewById(R.id.tv_loading);
                tvMsg.setText(message);
                if (!this.isFinishing()) {
                    //在一种极端的情况下还是会找不到activity，需要再判断下
                    try {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.show();
                        }
                    } catch (Exception e) {
                        LogUtils.w("showLoadingDialog -> " + e.getMessage());
                    }
                }
            }
            return mLoadingDialog;
        }

        return null;
    }

    public void hideLoadingDialog() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            LogUtils.w("mLoadingDialog dismiss error : " + e.getMessage());
        }

    }


    public void finishActivity() {
        AppSystemUtils.hideSoftInput(getWindow().getDecorView());
        finish();
        overridePendingTransition(R.anim.base_anim_enter2, R.anim.base_anim_exit2);
    }

    public boolean isFinish() {
        return this.isFinishing();
    }


    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(Bundle savedInstanceState);

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishActivity();
    }

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    public abstract int initVariableId();

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    public VM initViewModel() {
        return null;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }
}
