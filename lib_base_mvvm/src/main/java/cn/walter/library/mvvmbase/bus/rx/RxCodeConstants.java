package cn.walter.library.mvvmbase.bus.rx;

/**
 * Created by yx on 2017/5/31.
 * rxbus消息常量
 */

public interface RxCodeConstants {

    int JUMP_TYPE = 0;//code多类型接收

    //下载进度条
    int JUMP_TYPE_RETROFILT__DOWNLOAD = 100;

    //bottomDialog 返回键点击
    int TYPE_BOTTOM_DIALOG_BACK_KEYEVENT_TOUCH = 1000;

    int APP_LIFECYCLE_FORREGROUND = 2000;//app回到前台
    int APP_LIFECYCLE_BACKGROUND = 2001;//app处于后台

    int WX_RECHARGE = 3000;//微信支付
}
