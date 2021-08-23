package cn.walter.library.mvvmbase.widget.jsbridge;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.core.content.FileProvider;

import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import cn.walter.library.mvvmbase.R;
import cn.walter.library.mvvmbase.base.BaseApplication;
import cn.walter.library.mvvmbase.bus.rx.RxBus;
import cn.walter.library.mvvmbase.bus.rx.RxBusBaseMessage;
import cn.walter.library.mvvmbase.bus.rx.RxCodeConstants;
import cn.walter.library.mvvmbase.component.router.RouterActivityPath;
import cn.walter.library.mvvmbase.component.router.RouterManager;
import cn.walter.library.mvvmbase.config.Constants;
import cn.walter.library.mvvmbase.utils.system.NetWorkStateUtils;
import cn.walter.library.mvvmbase.utils.ui.DensityUtils;
import cn.walter.library.mvvmbase.utils.ui.DialogUtils;
import cn.walter.library.mvvmbase.utils.ui.ToastUtils;
import rx.functions.Action1;

/**
 * 进度条bridgewebview
 */
@SuppressLint("SetJavaScriptEnabled")
public class LineBridgeWebView extends WebView implements WebViewJavascriptBridge {

    protected static final String DATABASES_SUB_FOLDER = "/databases";

    public interface Listener {
        void onWebViewPageStart(String url, Bitmap favicon);

        void onWebViewPageFinished(WebView view, String url);

        void onWebViewJsAlert(WebView view, String url, String message, JsResult result);

        void onWebViewUpdateVisitedHistory(WebView view, String url, boolean isReload);
    }

    public interface ErrorListener {
        void onWebViewError(WebView view, WebResourceRequest request, WebResourceError error);
    }

    public interface JsConfirmListner {
        void onJsConfirm(WebView view, String url, String message, JsResult result);
    }

    public interface DownloadsListener {

        void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent);
    }

    public interface ShouldOverrideListener {
        void onOverrideUrlLoading(WebView view, String url);
    }

    public interface FindRequestUrlListener {
        void onFindUrl(WebView webView, String url);
    }

    public interface OverScrollListener {

        void onOverScrolled(WebView v, boolean onBottom);

    }

    protected Listener mListener;
    protected DownloadsListener mDownloadListener;
    protected JsConfirmListner mJsConfirmListner;
    protected ShouldOverrideListener mOverrideListener;
    protected FindRequestUrlListener mRequestUrlListener;

    protected OverScrollListener mOverScrollListener;


    protected ErrorListener mErrorListener;

    private final String TAG = "BridgeWebView";

    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private List<Message> startupMessage = new ArrayList<Message>();

    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;

    private boolean allowMultiple;

    protected WeakReference<Activity> mActivity;
    protected WeakReference<Fragment> mFragment;
    protected String mLanguageIso3;
    protected static final String LANGUAGE_DEFAULT_ISO3 = "eng";
    protected static final String CHARSET_DEFAULT = "UTF-8";
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    protected static final int REQUEST_CODE_FILE_PICKER = 51426;

    private boolean isShowProgressbar = true;
    private ProgressBar mProgressbar;
    private int mCurrentProgress;
    private boolean isAnimStart;

    private boolean isGameWeb = false;//避屏game ui url

    private boolean isFanyiWeb = false;//闭屏fanyi ui url

    private String notopenUrl;

    private boolean isInterceptUrl = false;//设置是否拦截url

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    public void setWebViewListener(final Activity activity, final Listener listener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }

        mListener = listener;

    }

    public void setJsConfirmListener(Activity activity, JsConfirmListner confirmListner) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        mJsConfirmListner = confirmListner;

    }

    public void setDownloadListener(Activity activity, DownloadsListener downloadListener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        this.mDownloadListener = downloadListener;
    }

    public void setErrorListener(Activity activity, ErrorListener errorListener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        this.mErrorListener = errorListener;
    }


    public void setOverrideListener(Activity activity, ShouldOverrideListener overrideListener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        this.mOverrideListener = overrideListener;
    }

    public void setRequestUrlListener(Activity activity, FindRequestUrlListener requestUrlListener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        this.mRequestUrlListener = requestUrlListener;
    }

    public void setOverScrollListener(Activity activity, OverScrollListener overScrollListener) {
        if (activity != null) {
            mActivity = new WeakReference<>(activity);
        } else {
            mActivity = null;
        }
        this.mOverScrollListener = overScrollListener;
    }

    public void setProgressBarYOffset(int yOffset) {
        if (mProgressbar == null) {
            return;
        }
        mProgressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            DensityUtils.dip2px(getContext(), 3), 0, yOffset));
    }

    public LineBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LineBridgeWebView(Context context) {
        super(context);
        init(context);
    }

    public void setInterceptUrl(boolean interceptUrl) {
        isInterceptUrl = interceptUrl;
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     *                if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    @SuppressWarnings("static-method")
    public void setCookiesEnabled(final boolean enabled) {
        CookieManager.getInstance().setAcceptCookie(enabled);
    }

    @SuppressLint("NewApi")
    public void setThirdPartyCookiesEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled);
        }
    }

    public void setMixedContentAllowed(final boolean allowed) {
        setMixedContentAllowed(getSettings(), allowed);
    }

    @SuppressWarnings("static-method")
    @SuppressLint("NewApi")
    protected void setMixedContentAllowed(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(allowed ? WebSettings.MIXED_CONTENT_ALWAYS_ALLOW : WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
    }

    protected boolean mGeolocationEnabled;

    private boolean isGoToNew = true;

    public void setGoToNew(boolean boo) {
        this.isGoToNew = boo;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setGeolocationEnabled(final boolean enabled) {
        if (enabled) {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setGeolocationEnabled(true);
            setGeolocationDatabasePath();
        }

        mGeolocationEnabled = enabled;
    }

    @SuppressLint("NewApi")
    protected void setGeolocationDatabasePath() {
        final Activity activity;

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11 && mFragment.get().getActivity() != null) {
            activity = mFragment.get().getActivity();
        } else if (mActivity != null && mActivity.get() != null) {
            activity = mActivity.get();
        } else {
            return;
        }

        getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
    }

    @SuppressLint("NewApi")
    protected static void setAllowAccessFromFileUrls(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(allowed);
            webSettings.setAllowUniversalAccessFromFileURLs(allowed);
        }
    }

    protected void init(Context context) {

        mProgressbar = new ProgressBar(context, null,
            android.R.attr.progressBarStyleHorizontal);
        mProgressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            DensityUtils.dip2px(context, 3), 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.base_webview_progress_bar_states);
        mProgressbar.setProgressDrawable(drawable);
        addView(mProgressbar);

        if (context instanceof Activity) {
            mActivity = new WeakReference<>((Activity) context);
        }
        mLanguageIso3 = getLanguageIso3();

        setFocusable(true);
        setFocusableInTouchMode(true);

        setSaveEnabled(true);

        final String filesDir = context.getFilesDir().getPath();
        final String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + DATABASES_SUB_FOLDER;

        final WebSettings webSettings = getSettings();
        webSettings.setAllowFileAccess(false);
        setAllowAccessFromFileUrls(webSettings, false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT < 18) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        if (Build.VERSION.SDK_INT < 19) {
            webSettings.setDatabasePath(databaseDir);
        }
        setMixedContentAllowed(webSettings, true);

        setThirdPartyCookiesEnabled(true);

        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓存大小
        this.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.setVerticalScrollBarEnabled(true);
        this.setVerticalScrollbarOverlay(true);
        setOverScrollMode(OVER_SCROLL_NEVER);
        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalScrollbarOverlay(false);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
//        this.setWebViewClient(generateBridgeWebViewClient());

        this.getSettings().setGeolocationEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }

        if (NetWorkStateUtils.isNetworkConnected(context)) {
            //有网络网络加载
            this.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            this.setVisibility(GONE);
        }

        super.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }

//            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//
//                if (mRequestUrlListener != null) {
//                    String path = request.getUrl().getPath();
//                    mRequestUrlListener.onFindUrl(view, path);
//                }
//
//                return super.shouldInterceptRequest(view, request);
//            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (mRequestUrlListener != null) {
                    mRequestUrlListener.onFindUrl(view, url);
                }

//                if (url.contains("https://fanyi-app.baidu.com/transapp/wiseflowdata?")) {
//                    LogUtils.w("shouldInterceptRequest 进来了");
//                    return new WebResourceResponse("","",new ByteArrayInputStream(new byte[1]));
//                }
                return super.shouldInterceptRequest(view, url);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (notopenUrl != null) {
                    String[] urls = notopenUrl.split(",");
                    for (int i = 0; i < urls.length; i++) {
                        if (url.contains(urls[i])) {
                            return true;
                        }
                    }
                }


                try {
                    //当url遇到ali支付的时候不解码
                    if (!url.startsWith("alipay")) {
                        url = URLDecoder.decode(url, "UTF-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
                    handlerReturnData(url);
                    return true;
                } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
                    flushMessageQueue();
                    return true;
                } else {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        if (mOverrideListener != null) {
                            mOverrideListener.onOverrideUrlLoading(view, url);
                            return isInterceptUrl;
                        }
                        return false;
                    }

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mActivity.get().startActivity(intent);
                    } catch (Exception e) {
//                        ToastUtils.showToast("外部错误，请重试");
                    }
                    return true;
                }


            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isShowProgressbar) {
                    mProgressbar.setAlpha(1.0f);
                    mProgressbar.setVisibility(VISIBLE);
                } else {
                    mProgressbar.setVisibility(GONE);
                }

                if (mListener != null) {
                    mListener.onWebViewPageStart(url, favicon);
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (LineBridgeWebView.toLoadJs != null) {
                    BridgeUtil.webViewLoadLocalJs(view, LineBridgeWebView.toLoadJs);
                }

                //
                if (getStartupMessage() != null) {
                    for (Message m : getStartupMessage()) {
                        dispatchMessage(m);
                    }
                    setStartupMessage(null);
                }

                if (mListener != null) {
                    mListener.onWebViewPageFinished(view, url);
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (mListener != null) {
                    mListener.onWebViewUpdateVisitedHistory(view, url, isReload);
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                if (mErrorListener != null) {
                    mErrorListener.onWebViewError(view, request, error);
                }

            }
        });

        super.setWebChromeClient(new WebChromeClient() {

//            public boolean onConsoleMessage(ConsoleMessage cm) {
//                LogUtils.w(cm.message() + " -- From line "
//                    + cm.lineNumber() + " of "
//                    + cm.sourceId());
//                return true;
//            }

            // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, null);
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, null);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Logger.w("wwebview打开文件,4.X以上走了");
                openFileInput(uploadMsg, null, false);
            }

            // file upload callback (Android 5.0 (API level 21) -- current) (public method)
            @SuppressWarnings("all")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Logger.w("webview打开文件,5.0以上走了");
                if (Build.VERSION.SDK_INT >= 21) {
                    final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;

                    openFileInput(null, filePathCallback, allowMultiple);

                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onProgressChanged(WebView view, int i) {
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
                super.onProgressChanged(view, i);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();

                if (mListener != null) {
                    mListener.onWebViewJsAlert(view, url, message, result);
                }

                return true;
            }


            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                if (mJsConfirmListner != null) {
                    mJsConfirmListner.onJsConfirm(view, url, message, result);
                }
                return true;
            }
        });

        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimeType, final long contentLength) {


                final String suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                if (mDownloadListener != null) {
                    mDownloadListener.onDownloadRequested(url, suggestedFilename, mimeType, contentLength, contentDisposition, userAgent);
                }

            }

        });


    }

//    protected BridgeWebViewClient generateBridgeWebViewClient() {
//        return new BridgeWebViewClient(this);
//    }

//	@Override
//	public void setWebChromeClient(final WebChromeClient client) {
////		new BridgeWebViewClient(this) = client;
//	}

    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
//        System.out.println("openFileInput方法走了吗???888");
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);


//        if (allowMultiple) {
//            if (Build.VERSION.SDK_INT >= 18) {
//                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            }
//        }

//        i.setType(mUploadableFileTypes);

//        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
//            mFragment.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
//        } else
//          if (mActivity != null && mActivity.get() != null) {
//            mActivity.get().startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
//        }
        this.allowMultiple = allowMultiple;
        showSelectDialog();
    }

    private Intent albumIntent, cameraIntent;
    private int CAMERA_REQUEST = 438;
    private File mPhotoFile;

    private void showSelectDialog() {
        new RxPermissions(mActivity.get())
            .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean granted) {
                    if (granted) {
                        List<String> listView = DialogUtils.createList("拍摄照片", "从相册选择");
                        DialogUtils.showListViewDialog(mActivity.get(), listView, "请选择", new DialogUtils.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, Integer integer) {
                                mPhotoFile = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                                if (integer == 0) {
                                    new RxPermissions(mActivity.get())
                                        .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .subscribe(new Action1<Boolean>() {
                                            @Override
                                            public void call(Boolean granted) {
                                                if (granted) {
                                                    if (cameraIntent == null) {
                                                        cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            Uri uri = FileProvider.getUriForFile(mActivity.get(),
                                                                mActivity.get().getPackageName() + ".provider", mPhotoFile);
                                                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                                        } else {
                                                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                                                        }

                                                    }
                                                    if (allowMultiple) {
                                                        if (Build.VERSION.SDK_INT >= 18) {
                                                            cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                        }
                                                    }
                                                    if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
                                                        mFragment.get().startActivityForResult(Intent.createChooser(cameraIntent, getFileUploadPromptLabel()), CAMERA_REQUEST);
                                                    } else if (mActivity != null && mActivity.get() != null) {
                                                        mActivity.get().startActivityForResult(Intent.createChooser(cameraIntent, getFileUploadPromptLabel()), CAMERA_REQUEST);
                                                    }
                                                } else {
                                                    ToastUtils.showToast("权限被拒绝了,无法启动相机!");
                                                }
                                            }
                                        });
                                } else if (integer == 1) {
                                    new RxPermissions(mActivity.get())
                                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .subscribe(new Action1<Boolean>() {
                                            @Override
                                            public void call(Boolean granted) {
                                                if (granted) {
                                                    if (albumIntent == null) {
                                                        albumIntent = new Intent(Intent.ACTION_PICK);
                                                    }
                                                    /* 开启Pictures画面Type设定为image */
                                                    albumIntent.setType("image/*");

                                                    if (allowMultiple) {
                                                        if (Build.VERSION.SDK_INT >= 18) {
                                                            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                        }
                                                    }
                                                    if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
                                                        mFragment.get().startActivityForResult(Intent.createChooser(albumIntent, getFileUploadPromptLabel()), mRequestCodeFilePicker);
                                                    } else if (mActivity != null && mActivity.get() != null) {
                                                        mActivity.get().startActivityForResult(Intent.createChooser(albumIntent, getFileUploadPromptLabel()), mRequestCodeFilePicker);
                                                    }
                                                } else {
                                                    ToastUtils.showToast("权限被拒绝了,无法启动相册!");
                                                }
                                            }
                                        });
                                }
                            }
                        });
                    }
                }
            });


    }

    /**
     * Provides localizations for the 25 most widely spoken languages that have a ISO 639-2/T code
     *
     * @return the label for the file upload prompts as a string
     */
    protected String getFileUploadPromptLabel() {
        try {
            if (mLanguageIso3.equals("zho")) return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2");
            else if (mLanguageIso3.equals("spa")) return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==");
            else if (mLanguageIso3.equals("hin"))
                return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=");
            else if (mLanguageIso3.equals("ben"))
                return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=");
            else if (mLanguageIso3.equals("ara"))
                return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==");
            else if (mLanguageIso3.equals("por")) return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv");
            else if (mLanguageIso3.equals("rus"))
                return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==");
            else if (mLanguageIso3.equals("jpn"))
                return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==");
            else if (mLanguageIso3.equals("pan"))
                return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=");
            else if (mLanguageIso3.equals("deu")) return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=");
            else if (mLanguageIso3.equals("jav")) return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=");
            else if (mLanguageIso3.equals("msa")) return decodeBase64("UGlsaWggc2F0dSBmYWls");
            else if (mLanguageIso3.equals("tel"))
                return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=");
            else if (mLanguageIso3.equals("vie"))
                return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==");
            else if (mLanguageIso3.equals("kor"))
                return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=");
            else if (mLanguageIso3.equals("fra"))
                return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy");
            else if (mLanguageIso3.equals("mar"))
                return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==");
            else if (mLanguageIso3.equals("tam"))
                return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=");
            else if (mLanguageIso3.equals("urd"))
                return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==");
            else if (mLanguageIso3.equals("fas"))
                return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==");
            else if (mLanguageIso3.equals("tur")) return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==");
            else if (mLanguageIso3.equals("ita")) return decodeBase64("U2NlZ2xpIHVuIGZpbGU=");
            else if (mLanguageIso3.equals("tha"))
                return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH");
            else if (mLanguageIso3.equals("guj"))
                return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=");
        } catch (Exception ignored) {
        }

        // return English translation by default
        return "Choose a file";
    }

    protected static String decodeBase64(final String base64) throws IllegalArgumentException, UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return new String(bytes, CHARSET_DEFAULT);
    }

    protected static String getLanguageIso3() {
        try {
            return Locale.getDefault().getISO3Language().toLowerCase(Locale.US);
        } catch (MissingResourceException e) {
            return LANGUAGE_DEFAULT_ISO3;
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (mPhotoFile != null) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(mActivity.get(),
                            BaseApplication.getContext().getPackageName() + ".provider", mPhotoFile);
                    } else {
                        uri = Uri.fromFile(mPhotoFile);
                    }
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(uri);
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;
                        try {
                            if (uri != null) {
                                dataUris = new Uri[]{uri};
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
//                            ToastUtils.showToast("找不到图片!");
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        } else if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                            } else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
//                            ToastUtils.showToast("找不到图片!");
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                } else {
//                    ToastUtils.showToast("找不到图片!");
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }


    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null) {
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }

    private float downX;
    private float downY;

    private boolean isSScroll = true;

    public void setScroll(boolean isScroll) {
        isSScroll = isScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSScroll) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = ev.getX();
                    downY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x1 = ev.getX();
                    float y1 = ev.getY();
                    //就是不让webview滑动
                    if (Math.abs(x1) != Math.abs(downX) || Math.abs(y1) != Math.abs(downY)) {
                        return false;

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) mProgressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (mOverScrollListener != null) {
            // clampedY=true的前提下，scrollY=0时表示滑动到顶部，scrollY!=0时表示到底部
            mOverScrollListener.onOverScrolled(this, scrollY != 0 && clampedY);
        }
    }

    public boolean isShowProgressbar() {
        return isShowProgressbar;
    }

    public void setShowProgressbar(boolean showProgressbar) {
        isShowProgressbar = showProgressbar;
    }

    public boolean isGameWeb() {
        return isGameWeb;
    }

    public void setGameWeb(boolean gameWeb) {
        isGameWeb = gameWeb;
    }

    public boolean isFanyiWeb() {
        return isFanyiWeb;
    }

    public void setFanyiWeb(boolean fanyiWeb) {
        isFanyiWeb = fanyiWeb;
    }

    public String getNotopenUrl() {
        return notopenUrl;
    }

    public void setNotopenUrl(String notopenUrl) {
        this.notopenUrl = notopenUrl;
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

    public boolean onBackPressed() {
        if (canGoBack()) {
            goBack();
            return false;
        } else {
            return true;
        }
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
