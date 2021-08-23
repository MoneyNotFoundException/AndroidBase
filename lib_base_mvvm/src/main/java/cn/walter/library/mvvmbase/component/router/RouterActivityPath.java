package cn.walter.library.mvvmbase.component.router;

/**
 * 用于组件开发中，ARouter单Activity跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 */

public class RouterActivityPath {

    //登录拦截器定义
    public static final int LOGIN_INTERCEPTOR = 1;


    public static final class Main {
        private static final String MAIN = "/main";

        //设置页面
        public static final String PAGER_SETTING = MAIN + "/PagerSetting";
        //修改登录密码
        public static final String PAGER_CHANGE_PASSWORD = MAIN + "/PagerChangePassword";
        //修改绑定
        public static final String PAGER_CHANGE_BIND = MAIN + "/PagerChangeBind";
        //我的名片
        public static final String PAGER_MIME_NAME_CARD = MAIN + "/PagerMimeNameCard";
        //二维码扫描
        public static final String PAGER_CUSTOM_SCAN = MAIN + "/PagerCustomeScan";

        //关于鲜乐豆
        public static final String PAGER_ABOUT_XLD = MAIN + "/PagerAboutXld";

        //资产页面
        public static final String PAGER_ASSEST_WALLET = MAIN + "/PagerAssestWallet";
    }

    /**
     * 登录组件
     */
    public static class Login {

        private static final String LOGIN = "/login";
        /*登录界面*/
        public static final String PAGER_LOGIN = LOGIN + "/PagerLogin";
        /*验证码登录*/
        public static final String PAGER_YZM_LOGIN = LOGIN + "/PagerYzmLogin";
        /*登录补充信息*/
        public static final String PAGER_LOGIN_SUPPLEMENT = LOGIN + "/PagerLoginSupplement";
    }
}
