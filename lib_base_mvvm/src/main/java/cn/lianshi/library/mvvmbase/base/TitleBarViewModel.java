package cn.lianshi.library.mvvmbase.base;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import cn.lianshi.library.mvvmbase.binding.command.BindingAction;
import cn.lianshi.library.mvvmbase.binding.command.BindingCommand;

/**
 *
 * DataBinding多module绑定的大坑，会生成多个ViewBinding对象，导致无法绑定，目前没有找到解决方法，只能弃用
 * @author yuxiao
 * @date 2019/1/17
 * 标题bar ViewModel
 * Description： 对应include标题的TitleBarViewModel
 * 布局从左往右依次绑定
 *
 */
public class TitleBarViewModel<M extends IBaseModel> extends BaseViewModel<M> {

    //X关闭图标
    public ObservableInt ivCloseObservable = new ObservableInt(View.GONE);

    //日期文字
    public ObservableField<String> dateTitle = new ObservableField<>("");

    //标题文字
    public ObservableField<String> titleText = new ObservableField<>("啦啦");

    //下箭头
    public ObservableInt ivDownwardArrowObservable = new ObservableInt(View.GONE);

    //定位图标
    public ObservableInt ivLocationArrowObservable = new ObservableInt(View.GONE);

    //更多文字
    public ObservableInt tvAddMoreObservable = new ObservableInt(View.GONE);

    //ll_add布局
    public ObservableInt llAddObservable = new ObservableInt(View.GONE);

    //tv_add_car
    public ObservableField<String> tvAddCarObservable = new ObservableField<>("添加");

    //ll_share布局
    public ObservableInt llShareObservable = new ObservableInt(View.GONE);

    //webview显示更多
    public ObservableInt ivLiulanqiObservable = new ObservableInt(View.GONE);

    //iv_gongju_gengxin
    public ObservableInt ivGongjuUpdateObservable = new ObservableInt(View.GONE);

    //iv_gongju_list_icon
    public ObservableInt ivGongjuListIconObservable = new ObservableInt(View.GONE);

    //iv_gongju_add_icon
    public ObservableInt ivGongjuAddIconObservable = new ObservableInt(View.GONE);

    //iv_course_quanping
    public ObservableInt ivCourseQuanpingObservable = new ObservableInt(View.GONE);

    //ll_password
    public ObservableInt llPasswordObservable = new ObservableInt(View.GONE);

    //ll_course
    public ObservableInt llCourseObservable = new ObservableInt(View.GONE);


    public TitleBarViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * 设置标题
     *
     * @param text 标题文字
     */
    public void setTitleText(String text) {
        titleText.set(text);
    }

    /**
     * 设置关闭X图标显示隐藏
     *
     * @param visibility
     */
    public void seIvCloseVisible(int visibility) {
        ivCloseObservable.set(visibility);
    }

    /**
     * 设置日期
     *
     * @param text 如期
     */
    public void setDateText(String text) {
        dateTitle.set(text);
    }


    /**
     * 设置下箭头图标显示隐藏
     *
     * @param visibility
     */
    public void setIvDownwardArrowObservable(int visibility) {
        ivDownwardArrowObservable.set(visibility);
    }

    /**
     * 设置定位图标显示隐藏
     *
     * @param visibility
     */
    public void setIvLocationArrowObservable(int visibility) {
        ivLocationArrowObservable.set(visibility);
    }

    /**
     * 设置更多文字显示隐藏
     *
     * @param visibility
     */
    public void setTvAddMoreObservable(int visibility) {
        tvAddMoreObservable.set(visibility);
    }

    /**
     * 设置ll_add布局显示隐藏
     *
     * @param visibility
     */
    public void setLlAddObservable(int visibility) {
        llAddObservable.set(visibility);
    }

    /**
     * 设置ll_add布局显示隐藏
     *
     * @param visibility
     */
    public void setLlShareObservable(int visibility) {
        llShareObservable.set(visibility);
    }

    /**
     * 设置添加文字
     *
     * @param text 如期
     */
    public void setTvAddCarText(String text) {
        tvAddCarObservable.set(text);
    }

    /**
     * 设置iv_liulanqi布局显示隐藏
     *
     * @param visibility
     */
    public void setIvLiulanqiObservable(int visibility) {
        ivLiulanqiObservable.set(visibility);
    }

    /**
     * 设置iv_gonngju_gengxin布局显示隐藏
     *
     * @param visibility
     */
    public void setIvGonngjuUpdateObservable(int visibility) {
        ivGongjuUpdateObservable.set(visibility);
    }

    /**
     * 设置iv_gonngju_list_icon布局显示隐藏
     *
     * @param visibility
     */
    public void setIvGonngjuListIconObservable(int visibility) {
        ivGongjuListIconObservable.set(visibility);
    }

    /**
     * 设置iv_gonngju_add_icon布局显示隐藏
     *
     * @param visibility
     */
    public void setIvGonngjuAddIconObservable(int visibility) {
        ivGongjuAddIconObservable.set(visibility);
    }


    /**
     * 设置iv_course_quanping布局显示隐藏
     *
     * @param visibility
     */
    public void setIvCourseQuanpingObservable(int visibility) {
        ivCourseQuanpingObservable.set(visibility);
    }


    /**
     * 设置ll_password布局显示隐藏
     *
     * @param visibility
     */
    public void setllPasswordObservable(int visibility) {
        llPasswordObservable.set(visibility);
    }


    /**
     * 设置ll_course布局显示隐藏
     *
     * @param visibility
     */
    public void setllCourseObservable(int visibility) {
        llCourseObservable.set(visibility);
    }


    /**
     * 返回按钮的点击事件,子类可重写
     *
     */
    public BindingCommand onBackClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {
                finishView();
            }
        });
    }

    /**
     * 定位图标点击
     *
     */
    public BindingCommand onIvLocationClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * tv_addmore点击
     *
     */
    public BindingCommand onTvAddMoreClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * ll_share点击
     *
     */
    public BindingCommand onLlShareClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }


    /**
     * iv_liulanqi点击
     *
     */
    public BindingCommand onIvLiulanqiClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_gongju_gengxin点击
     *
     */
    public BindingCommand onIvGongjuUpdateClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_gongju_list_icon点击
     *
     */
    public BindingCommand onIvGongjuListIconClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_gongju_add_icon点击
     *
     */
    public BindingCommand onIvGongjuAddIconClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }



    /**
     * iv_course_quanping点击
     *
     */
    public BindingCommand onIvCourseQuanpingClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_setting点击
     *
     */
    public BindingCommand onIvSettingClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_more点击
     *
     */
    public BindingCommand onIvMoreClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_shoucang点击
     *
     */
    public BindingCommand onIvShoucangClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

    /**
     * iv_fenxiang点击
     *
     */
    public BindingCommand onIvShareClick(){
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {

            }
        });
    }

}
