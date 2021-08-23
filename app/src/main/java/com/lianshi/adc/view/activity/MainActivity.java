package com.walter.adc.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.walter.adc.BR;
import com.walter.adc.R;
import com.walter.adc.databinding.ActivityMainBinding;
import com.walter.adc.viewModel.MainViewModel;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.Observer;
import byc.imagewatcher.ImageWatcher;
import cn.walter.library.mvvmbase.base.BaseActivity;
import cn.walter.library.mvvmbase.utils.GsonUtil;
import cn.walter.library.mvvmbase.utils.ui.ImageWatcherUtils;

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
    }

}
