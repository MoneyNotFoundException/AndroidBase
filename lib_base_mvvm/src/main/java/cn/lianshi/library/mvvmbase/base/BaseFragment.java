package cn.lianshi.library.mvvmbase.base;

import android.app.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.lianshi.library.mvvmbase.R;
import cn.lianshi.library.mvvmbase.bus.Messenger;
import cn.lianshi.library.mvvmbase.config.Constants;
import cn.lianshi.library.mvvmbase.databinding.BaseLayoutAppTitlebarBinding;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import cn.lianshi.library.mvvmbase.utils.ui.DialogUtils;
import cn.lianshi.library.mvvmbase.widget.mdui.ProgressWheel;


/**
 * @author yuxiao
 * @date 2019/1/15
 * 基类 databinding fragment
 * <p>
 * initView();
 * 抽象方法
 * 与 onCreateView 类似.
 * initViews 是只要 Fragment 被创建就会执行的方法.
 * 也就是说如果我们不想用 LazyLoad 模式
 * 则把所有的初始化 和 加载数据方法都写在 initView 即可.
 * <p>
 * initData();
 * 抽象方法
 * 若将代码写在initData中, 则是在Fragment真正显示出来后才会去Load(懒加载).
 * <p>
 * setForceLoad();
 * 忽略isFirstLoad的值，强制刷新数据，前提是Visible & Prepared.
 * 未Visible & Prepared的页面需要注意在RefreshData的时候视图为空的问题, 具体请参见实例代码
 * <p>
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果
 * 需要先hide再show
 * 需要先hide再show
 * 需要先hide再show
 * eg:
 * transaction.hide(aFragment);
 * transaction.show(aFragment);
 */
public abstract class BaseFragment<VB extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment implements IBaseView {

    public VB mVB;
    public VM mVM;
    private int viewModelId;

    /**
     * MVVM DataBinding组件化的大坑，如果是多module下引入布局，会生成多个binding，
     * 合并之后找到的只是个view，必须重新bind一下,而且不能设置ViewModel
     */
    protected BaseLayoutAppTitlebarBinding mTitleBarBinding;

    private Dialog mLoadingDialog;
    private ProgressWheel mProgressWheel;

    /**
     * 是否可见状态 为了避免和{@link Fragment#isVisible()}冲突 换个名字
     */
    private boolean isFragmentVisible;
    /**
     * 标志位，View已经初始化完成。
     * 用isAdded()属性代替
     * isPrepared还是准一些,isAdded有可能出现onCreateView没走完但是isAdded了
     */
    private boolean isPrepared;
    /**
     * 是否第一次加载
     */
    private boolean isFirstLoad = true;
    /**
     * <pre>
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     * 一般用于PagerAdapter需要刷新各个子Fragment的场景
     * 不要new 新的 PagerAdapter 而采取reset数据的方式
     * 所以要求Fragment重新走initData方法
     * </pre>
     */
    private boolean forceLoad = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() > 0) {
            initVariables(bundle);
        }

    }


    @Override
    public void onDestroy() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        viewModelId = initVariableId();
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
        mVB.setVariable(viewModelId, mVM);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(mVM);
        //注入RxLifecycle生命周期
        mVM.injectLifecycleProvider(this);
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack();
        initTitleBarBinding();
        findProgressWheel();
        initView();
        isPrepared = true;
        lazyLoad();
        return mVB.getRoot();
    }

    /**
     * 设置titleBar的返回监听和标题
     */
    private void initTitleBarBinding() {
        View titleBar = mVB.getRoot().findViewById(R.id.title_bar);
        if (titleBar != null) {
            mTitleBarBinding = DataBindingUtil.bind(titleBar);
            if (mTitleBarBinding != null) {
                mTitleBarBinding.ivBack.setOnClickListener(v -> mVM.finishView());
                //这里不能通过autowire去自动加载，没有绑定path，必须通过intent去拿
                Bundle arguments = getArguments();
                if (arguments != null) {
                    String title = arguments.getString(Constants.VIEW_TITLE);
                    if (title != null) {
                        mTitleBarBinding.tvTitlebarTitle.setText(title);
                    }
                }
            }
        }
    }

    /**
     * 寻找页面资源中的progress_wheel
     */
    private void findProgressWheel() {
        mProgressWheel = mVB.getRoot().findViewById(R.id.progress_bar);
    }

    public ProgressWheel getProgressWheel() {
        return mProgressWheel;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        //注册RxBus
        mVM.registerRxBus();
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
                showLoadingDialog(title,false);
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
                ((BaseActivity) getActivity()).finishActivity();
            }
        });
        //关闭上一层
        mVM.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                getActivity().onBackPressed();
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
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtils.getWaitDialog(getActivity(), message, isCancel);
            }
            if (mLoadingDialog != null) {
                AppCompatTextView tvMsg = mLoadingDialog.findViewById(R.id.tv_loading);
                tvMsg.setText(message);
                if (getActivity() != null && !getActivity().isFinishing()) {
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


    /**
     * =====================================================================
     **/

    //刷新布局
    public void refreshLayout() {
        if (mVM != null) {
            mVB.setVariable(viewModelId, mVM);
        }
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     *
     * @param isVisibleToUser 是否显示出来了
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    protected void onInvisible() {
        isFragmentVisible = false;
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (forceLoad || isFirstLoad()) {
                forceLoad = false;
                isFirstLoad = false;
                initData();
            }
        }
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     * Bundle args = getArguments();
     * if (args != null) {
     * args.putParcelable(KEY, info);
     * }
     */
    public void initVariables(Bundle bundle) {
    }


    public boolean isPrepared() {
        return isPrepared;
    }

    /**
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     */
    public void setForceLoad(boolean forceLoad) {
        this.forceLoad = forceLoad;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }


    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

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

    public boolean isBackPressed() {
        return false;
    }



    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return ViewModelProviders.of(fragment).get(cls);

    }
}
