package cn.lianshi.duanshipin.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.lianshi.library.mvvmbase.bus.rx.RxBus;
import cn.lianshi.library.mvvmbase.bus.rx.RxBusBaseMessage;
import cn.lianshi.library.mvvmbase.bus.rx.RxCodeConstants;
import cn.lianshi.library.mvvmbase.config.AppConfig;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import cn.lianshi.thirdlib.R;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thirdlib_pay_result);

        api = WXAPIFactory.createWXAPI(this, AppConfig.WEICHAT_APPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {

        RxBus.getDefault().post(RxCodeConstants.JUMP_TYPE, RxCodeConstants.WX_RECHARGE);
        LogUtils.w("onPayFinish, errCode = " + resp.errCode);
        finish();

    }
}