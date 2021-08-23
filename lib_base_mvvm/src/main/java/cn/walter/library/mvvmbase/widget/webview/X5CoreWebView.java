package cn.walter.library.mvvmbase.widget.webview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.WeakReference;

import cn.walter.library.mvvmbase.R;
import cn.walter.library.mvvmbase.utils.LogUtils;
import cn.walter.library.mvvmbase.utils.system.NetWorkStateUtils;
import cn.walter.library.mvvmbase.utils.ui.DensityUtils;


/**
 * Created by yx on 2017/7/5.
 * X5内核的webview
 */

public class X5CoreWebView extends WebView {

    protected Listener                  mListener;
    protected ErrorListener             mErrorListener;
    protected WeakReference<Activity>   mActivity;
    protected static final String  DATABASES_SUB_FOLDER = "/x5core_databases";
    private                boolean isShowProgressbar    = true;
    private ProgressBar mProgressbar;
    private int         mCurrentProgress;
    private boolean     isAnimStart;

    public void setShowProgressbar(boolean showProgressbar) {
        isShowProgressbar = showProgressbar;
        if(!isShowProgressbar) {
            mProgressbar.setVisibility(GONE);
        }
    }



    public interface Listener {
        void onWebViewPageStart(WebView webView, String url, Bitmap favicon);

        void onWebViewPageFinished(WebView view, String url);

        void onWebViewJsAlert(WebView view, String url, String message, JsResult result);

        void onWebViewUpdateVisitedHistory(WebView view, String url, boolean isReload);

    }

    public interface ErrorListener {
        void onWebViewError(WebView webView, WebResourceRequest resRequest, WebResourceError webResourceError);
    }


    public void setWebViewListener(Activity activity, Listener listener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        mListener = listener;

    }

    public void setWebViewErrorListener(Activity activity, ErrorListener listener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        mErrorListener = listener;
    }


    public X5CoreWebView(Context context) {
        super(context);
        init(context);
    }

    public X5CoreWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public X5CoreWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        mProgressbar = new ProgressBar(context, null,
            android.R.attr.progressBarStyleHorizontal);
        mProgressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            DensityUtils.dip2px(getContext(),3), Gravity.TOP));
        Drawable drawable = context.getResources().getDrawable(R.drawable.base_webview_progress_bar_states);
        mProgressbar.setProgressDrawable(drawable);
        addView(mProgressbar);

        if (isInEditMode()) {
            // do not run the code from this method
            return;
        }

        if (context instanceof Activity) {
            mActivity = new WeakReference<>((Activity) context);
        }


        String filesDir = context.getFilesDir().getPath();
        String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + DATABASES_SUB_FOLDER;
        WebSettings webSettings = getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSavePassword(false);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(databaseDir);
        webSettings.setMixedContentMode(21);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓存大小
        webSettings.setAppCacheEnabled(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        if (NetWorkStateUtils.isNetworkConnected(getContext())) {
            //有网络网络加载
            this.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            this.setVisibility(GONE);
        }

        super.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (isShowProgressbar) {
                    mProgressbar.setAlpha(1.0f);
                    mProgressbar.setVisibility(VISIBLE);
                } else {
                    mProgressbar.setVisibility(GONE);
                }

                if (mListener != null) {
                    mListener.onWebViewPageStart(webView, s, bitmap);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if (mListener != null) {
                    mListener.onWebViewPageFinished(webView, s);
                }
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (mErrorListener != null) {
                    mErrorListener.onWebViewError(webView, webResourceRequest, webResourceError);
                }
            }


            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }


            @Override
            public void doUpdateVisitedHistory(WebView webView, String s, boolean b) {
                super.doUpdateVisitedHistory(webView, s, b);
                if (mListener != null) {
                    mListener.onWebViewUpdateVisitedHistory(webView, s, b);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {


                return super.shouldOverrideUrlLoading(webView, s);
            }
        });

        super.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                jsResult.confirm();
                if (mListener != null) {
                    mListener.onWebViewJsAlert(webView, s, s1, jsResult);
                }
                return true;

            }


            @Override
            public void onProgressChanged(WebView webView, int i) {
                if (isShowProgressbar) {
                    mCurrentProgress = mProgressbar.getProgress();
                    if (i >= 100 && !isAnimStart) {
                        // 防止调用多次动画
                        isAnimStart = true;
                        mProgressbar.setProgress(i);
                        // 开启属性动画让进度条平滑消失
                        startDismissAnimation(mProgressbar.getProgress());
                    } else {
                        // 开启属性动画让进度条平滑递增
                        startProgressAnimation(i);
                    }
                } else {
                    mProgressbar.setVisibility(GONE);
                }

                super.onProgressChanged(webView, i);
            }

            @Override
            public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
                if (mActivity != null && mActivity.get().getComponentName().getClassName().contains("LicenseScoreActivity")) {
                    //表示驾驶证页面，禁止弹出对话框
                    jsPromptResult.cancel();
                    return true;//消费
                }
                return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
            }
        });

    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressbar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1000);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
            int offset = 100 - progress;
            mProgressbar.setProgress((int) (progress + offset * fraction));
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressbar.setProgress(0);
                mProgressbar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressbar, "progress", mCurrentProgress, newProgress);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }


}
