package cn.lianshi.library.mvvmbase.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

import cn.lianshi.library.mvvmbase.R;
import cn.lianshi.library.mvvmbase.base.BaseApplication;
import cn.lianshi.library.mvvmbase.utils.LogUtils;
import cn.lianshi.library.mvvmbase.utils.SpUtils;
import cn.lianshi.library.mvvmbase.utils.file.DataCleanUtils;
import cn.lianshi.library.mvvmbase.utils.file.FileUtils;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 */
public class AppConfig {

    private final static String APP_CONFIG = "config";

    public final static String CONF_COOKIE = "cookie";

    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

    //友盟
    public static final String UM_APPID = "5cc117d10cafb248590002f8";

    //微信
    public final static String WEICHAT_APPID = "wx6a903a0041eb4c9e";
    public final static String WEICHAT_SECRET = "2c001668a85c7b5f5df0c3a959a1ba70";
    public final static String WEICHAT_PARTNERID = "1508017411";

    //qq
    public final static String QQ_APPID = "1106324523";
    public final static String QQ_APPKEY = "pAyLEbYu7EGbPE3X";

    //新浪微博
    public final static String SINA_APPID = "2847779027";
    public final static String SINA_APPKEY = "1bca9b4cf62b53821e380646b8bbbbaf";
    public final static String SINA_REDIRECY_URL = "https://api.weibo.com/oauth2/default.html";

    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment
        .getExternalStorageDirectory()
        + File.separator
        + "lianshi"
        + File.separator + "dxp_img" + File.separator;

    // 默认存放头像的路径
    public final static String DEFAULT_SAVE_AVATAR_PATH = Environment
        .getExternalStorageDirectory()
        + File.separator
        + "lianshi"
        + File.separator + "dxp_avatar" + File.separator;

    // 默认存二维码的路径
    public final static String DEFAULT_SAVE_QRCODE_IMAGE_PATH = Environment
        .getExternalStorageDirectory()
        + File.separator
        + "lianshi"
        + File.separator + "QRCode" + File.separator;

    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }


    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);
            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(BaseApplication.getContext()).get();
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(BaseApplication.getContext()).remove(key);
    }


    /**
     * 获取App唯一标识
     */
    public static String getAppId() {
        Context context = BaseApplication.getContext();
        String uniqueID = getAppConfig(context).get(AppConfig.CONF_APP_UNIQUEID);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            getAppConfig(context).set(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取渠道信息
     * 在打包渠道Baiduyingyong和BaiduSousuo的时候要隐藏信用卡信息,可以通过渠道来区分
     */
    public static String getChannelName() {
        ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(BaseApplication.getContext());
        return channelInfo != null ? channelInfo.getChannel() : "DEBUG渠道";
    }


    /**
     * 这个是要即时获取的，如果获取不到就直接传空
     *
     * @return
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceImei() {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) BaseApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceId = tm.getImei();
                } else {
                    deviceId = tm.getDeviceId();
                }
            }
        } catch (Exception e) {
            LogUtils.w("getDeviceImei error:" + e.getMessage());
            return deviceId;
        }

        return deviceId;
    }

    /**
     * 获取设备id
     *
     * @return
     */
    public static String getDeviceId() {
        String deviceId = getDeviceImei();
        LogUtils.w("deviceId  imei -> " + deviceId);
        if (deviceId != null && deviceId.isEmpty()) {
            deviceId = getAppId();
        }
        return deviceId;
    }


    /**
     * 清除app缓存
     */
    public void clearAppCache() {
//        DataCleanManager.cleanDatabases();
        // 清除数据缓存
        DataCleanUtils.cleanInternalCache(BaseApplication.getContext());
        FileUtils.deleteAvata();
//        DataCleanManager.cleanSharedPreference();
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 判断app是否处于后台
     *
     * @return
     */
    public static boolean isAppBackGround() {
        return BaseApplication.isAppBackGround();
    }


}
