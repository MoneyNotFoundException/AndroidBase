package cn.walter.thirdlib.manager;

import android.content.Context;
import android.os.Message;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.walter.library.mvvmbase.bus.handler.TaskHandler;
import cn.walter.library.mvvmbase.bus.rx.RxBus;
import cn.walter.library.mvvmbase.bus.rx.RxBusBaseMessage;
import cn.walter.library.mvvmbase.bus.rx.RxCodeConstants;
import cn.walter.library.mvvmbase.config.AppConfig;
import cn.walter.library.mvvmbase.utils.LogUtils;
import cn.walter.library.mvvmbase.utils.ui.ToastUtils;
import cn.walter.thirdlib.pojo.WeChatPayEntity;
import rx.Subscription;

/**
 * @author yuxiao
 * @date 2018/11/29
 * 应用支付控制器,因为有rx和handler的存在，所以在用完之后要销毁
 */
public class AppPayManager {


    private static volatile AppPayManager sPayManager;

    private static final int ALI_PAY_FLAG = 1;

    private OnPayStatusCallBack mPayStatusCallBack;

    private static AliPayHandler mAliPayHandler;

    private WeChatPayFlag payFlag;


    /**
     * 微信支付分类
     * 违章代缴
     * vip支付
     * 音频支付
     * 网页游戏积分支付
     * 文娱充值
     */
    public enum WeChatPayFlag {
        IDPHOTO_PAY,
        CAR_BREAK_RULES,
        VIP_PAY,
        XIMALAYA_PAY,
        WEBGAME_PAY,
        JIFEN_PAY,
        RECREATION
    }

    public static AppPayManager getInstance() {
        if (sPayManager == null) {
            synchronized (AppPayManager.class) {
                if (sPayManager == null) {
                    sPayManager = new AppPayManager();
                }
            }
        }

        return sPayManager;
    }

    private AppPayManager() {

    }


    /**
     * 微信支付注册rx
     */
    private Subscription mWeChatSubs = RxBus.getDefault()
        .toObservable(RxCodeConstants.JUMP_TYPE, RxBusBaseMessage.class)
        .subscribe(message -> {
            int messageCode = message.getCode();
            switch (messageCode) {
                case RxCodeConstants.WX_RECHARGE:
                    LogUtils.w("mWeChatSubs WX_CHA_WEI_ZHANG_PAY");
                    BaseResp baseResp = (BaseResp) message.getObject();
                    String errCode = String.valueOf(baseResp.errCode);
                    switch (errCode) {
                        case "0":
                            //支付成功
                            if (mPayStatusCallBack != null) {
                                switch (payFlag) {
                                    default:
                                        if (mPayStatusCallBack != null) {
                                            mPayStatusCallBack.onPaySuccess();
                                        }
                                        break;
                                }
                            }
                            break;
                        case "-1"://  "支付错误";
                        case "-2": // "用户取消支付";
                            if (mPayStatusCallBack != null) {
                                switch (payFlag) {
                                    default:
                                        if (mPayStatusCallBack != null) {
                                            mPayStatusCallBack.onPayFail();

                                        }
                                        break;
                                }

                            }
                            break;

                    }
                    break;
            }
        });


    /**
     * 打开微信支付
     *
     * @param context
     * @param flag
     */
    public void openWeChatPay(Context context, WeChatPayEntity entity, WeChatPayFlag flag, OnPayStatusCallBack payStatusCallBack) {
        this.payFlag = flag;
        this.mPayStatusCallBack = payStatusCallBack;
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, AppConfig.WEICHAT_APPID);
        wxapi.registerApp(AppConfig.WEICHAT_APPID);
        if (!wxapi.isWXAppInstalled()) {
            ToastUtils.showToast("请先安装微信.");
            return;
        }

        if (!wxapi.isWXAppSupportAPI()) {
            ToastUtils.showToast("当前微信版本不支持支付功能.");
            return;
        }

        PayReq request = new PayReq();
        request.appId = AppConfig.WEICHAT_APPID;
        request.partnerId = AppConfig.WEICHAT_PARTNERID;
        request.prepayId = entity.getPrepayId();
        request.packageValue = entity.getPackageX();
        request.nonceStr = entity.getNonceStr();
        request.timeStamp = entity.getTimeStamp();
        request.sign = entity.getPaySign();
        wxapi.sendReq(request);


    }

    public void destroy() {

        if (mWeChatSubs != null) {
            mWeChatSubs.unsubscribe();
            mWeChatSubs = null;
        }

        if (mAliPayHandler != null) {
            mAliPayHandler.removeCallbacksAndMessages(null);
            mAliPayHandler = null;
        }

        if (mPayStatusCallBack != null) {
            mPayStatusCallBack = null;
        }

        sPayManager = null;


    }

    /**
     * 静态阿里支付内部类，解决内存泄漏
     */
    private class AliPayHandler extends TaskHandler<AppPayManager> {


        public AliPayHandler(AppPayManager appPayManager) {
            super(appPayManager);
        }

        @Override
        public void onTaskOk(AppPayManager object, Message msg) {
            super.onTaskOk(object, msg);
            if (ALI_PAY_FLAG == msg.arg1) {

            }
        }

    }

    public interface OnPayStatusCallBack {

        void onPaySuccess();

        void onPayFail();

    }


}