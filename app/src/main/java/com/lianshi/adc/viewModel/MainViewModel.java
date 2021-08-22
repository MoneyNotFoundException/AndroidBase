package com.lianshi.adc.viewModel;

import android.app.Application;

import com.lianshi.adc.model.UserInfoModel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import cn.lianshi.library.mvvmbase.base.BaseViewModel;
import cn.lianshi.library.mvvmbase.binding.command.BindingAction;
import cn.lianshi.library.mvvmbase.binding.command.BindingCommand;
import cn.lianshi.library.mvvmbase.bus.event.SingleLiveEvent;

/**
 * Created by Walter on 2020-05-19.
 */
public class MainViewModel extends BaseViewModel<UserInfoModel> {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public SingleLiveEvent testEvent = new SingleLiveEvent();

    public ObservableBoolean testBoolean = new ObservableBoolean(true);
    public ObservableField<String> testStr = new ObservableField<>("test");

    public BindingCommand test = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            testEvent.postValue("");
            testEvent.observe((LifecycleOwner) getContext(), new Observer() {
                @Override
                public void onChanged(Object o) {
                    testBoolean.set(false);
                }
            });
        }
    });

    public SingleLiveEvent languageEvent = new SingleLiveEvent();
    public BindingCommand cn = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            languageEvent.postValue("cn");
        }
    });
    public BindingCommand en = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            languageEvent.postValue("en");

        }
    });
    public SingleLiveEvent restarEvent = new SingleLiveEvent();
    public BindingCommand restar = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            restarEvent.postValue("en");
        }
    });
}
