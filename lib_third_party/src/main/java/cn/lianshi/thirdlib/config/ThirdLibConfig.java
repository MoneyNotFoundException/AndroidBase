package cn.lianshi.thirdlib.config;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cn.lianshi.library.mvvmbase.config.AppConfig;


/**
 * @author lianshi
 * @date 2019/1/26
 */
public class ThirdLibConfig {

    public static void initThirdLib(Context context) {
        //友盟
        initUmeng(context);

    }


    /**
     * 初始化友盟
     *
     * @param context
     */
    private static void initUmeng(Context context) {
        //友盟统计,友盟渠道设置
        UMConfigure.setLogEnabled(false);
        String channelName = AppConfig.getChannelName();
        UMConfigure.init(context, AppConfig.UM_APPID, channelName, UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        // 支持在子进程中统计自定义事件
//        UMConfigure.setProcessEvent(true);
        //日志加密
        UMConfigure.setEncryptEnabled(true);

        UMShareAPI umShareAPI = UMShareAPI.get(context);
        UMShareConfig sconfig = new UMShareConfig();
        sconfig.isNeedAuthOnGetUserInfo(true);
        umShareAPI.setShareConfig(sconfig);
        PlatformConfig.setWeixin(AppConfig.WEICHAT_APPID, AppConfig.WEICHAT_SECRET);//微信 appid appsecret
        PlatformConfig.setQQZone(AppConfig.QQ_APPID, AppConfig.QQ_APPKEY); // QQ和Qzone appid appkey
        PlatformConfig.setSinaWeibo(AppConfig.SINA_APPID, AppConfig.SINA_APPKEY, AppConfig.SINA_REDIRECY_URL); // QQ和Qzone appid appkey


    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
