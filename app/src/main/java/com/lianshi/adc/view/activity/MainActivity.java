package com.lianshi.adc.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lianshi.adc.BR;
import com.lianshi.adc.R;
import com.lianshi.adc.databinding.ActivityMainBinding;
import com.lianshi.adc.utils.LauguageUtils;
import com.lianshi.adc.viewModel.MainViewModel;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.Observer;
import byc.imagewatcher.ImageWatcher;
import cn.lianshi.library.mvvmbase.base.BaseActivity;
import cn.lianshi.library.mvvmbase.utils.GsonUtil;
import cn.lianshi.library.mvvmbase.utils.ui.ImageWatcherUtils;

/**
 * Created by Walter on 2020-05-18.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initView() {
        super.initView();
//        changeLanguage(Locale.US);
        Logger.w("wid = " + getResources().getDisplayMetrics().widthPixels + " hei = " + getResources().getDisplayMetrics().heightPixels);
        ImageWatcher imageWatcher = ImageWatcherUtils.initImageWatcher(this);
        Glide.with(mContext).load("https://pic.xlyp.shop/2725a11ceec64e92928f65773eff0d3d.JPG?imageView/0/w/480/h/480").into(mVB.image);
        mVB.image.setOnClickListener(view ->{
            List<ImageView> mapping = new ArrayList<>(); // 这个请自行理解
            mapping.add(mVB.image);
            List<String> dataList = new ArrayList<>();
            dataList.add("https://pic.xlyp.shop/2725a11ceec64e92928f65773eff0d3d.JPG?imageView/0/w/480/h/480");
            imageWatcher.show(mVB.image,mapping,dataList);
        });
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Log.e("LogUtils", "confi  " + configuration.locale.getLanguage());
        mVM.languageEvent.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                String lan = (String) o;
                Locale locale = Locale.SIMPLIFIED_CHINESE;
                if (lan.equals("cn"))
                    locale = Locale.SIMPLIFIED_CHINESE;
                if (lan.equals("en"))
                    locale = Locale.ENGLISH;
        String str = "{\"statusCode\":0,\"errorMessage\":null,\"content\":{\"id\":15961,\"userName\":\"TCrL6eZYsE2qjvPBwQQwqWr2YS7q8rtLUA\",\"areaCode\":\"86\",\"mobile\":\"13412341230\",\"inviteCode\":\"5fp5e5jw\",\"referrer\":null,\"referrerMobile\":null,\"realName\":null,\"idCardType\":null,\"idCard\":null,\"realNameStatus\":0,\"idCardImg1\":null,\"idCardImg2\":null,\"idCardImg3\":null,\"idCardStatus\":0,\"idCardTime\":\"2020-11-24T17:45:19+0800\",\"idCardInfo\":null,\"loginTimes\":1,\"registerIp\":null,\"lastLoginIp\":\"175.0.251.244\",\"registerTime\":\"2020-11-21T11:50:04+0800\",\"lastLoginTime\":\"2020-11-24T17:45:19+0800\",\"status\":0,\"email\":\"\",\"updateTime\":\"2020-11-24T17:45:19+0800\",\"referrerUrl\":\"www.xxx.xx\",\"referrerCount\":0,\"referrerKYCCount\":0,\"referrerReward\":null,\"referrerRewardAll\":null,\"groupType\":\"GROUP_TYPE_BASE\",\"googleAuth\":null,\"autoWithdraw\":2,\"groupName\":null,\"userType\":0,\"isPublishOtc\":0,\"userPrivilege\":0,\"leverageType\":0,\"bindBank\":false,\"bindPaypwd\":false,\"permissions\":[]},\"timestamp\":1606272978,\"elapsedTime\":0}";
                Resources resources = MainActivity.this.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();

                // 应用用户选择语言
                config.locale = locale;
                resources.updateConfiguration(config, dm);

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mVM.restarEvent.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
//                reStar();

                LauguageUtils.changeAppLanguage(MainActivity.this, Locale.US.getLanguage(), MainActivity.class);


            }

        });
        TestClass tc = GsonUtil.fromJson(str,TestClass.class,GsonUtil.DEFAULT_DATE_PATTERN);
        Logger.w("liuqian = " + tc.getContent().getUpdateTime().toString());
        String json = GsonUtil.toJson(new Date(),GsonUtil.DEFAULT_DATE_PATTERN);
        Logger.w("liuqian = " + json);
//
    }

    public void changeLanguage(Locale locale) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        getResources().updateConfiguration(configuration, null);
    }

    public void reStar() {
        //        //重新启动Activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
