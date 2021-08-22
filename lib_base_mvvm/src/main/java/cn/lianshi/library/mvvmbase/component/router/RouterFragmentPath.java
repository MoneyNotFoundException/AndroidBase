package cn.lianshi.library.mvvmbase.component.router;

/**
 * 用于组件开发中，ARouter多Fragment跳转的统一路径注册
 * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
 */

public class RouterFragmentPath {

    /**
     * 登录组件
     */
    public static class Main {

        private static final String MAIN = "/main";

        /*我的页面*/
        public static final String PAGER_MIME = MAIN + "/mime";
    }

}
