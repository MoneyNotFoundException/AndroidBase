package cn.lianshi.library.mvvmbase.component.config;

/**
 *
 * 组件生命周期反射类名管理，在这里注册需要初始化的组件，通过反射动态调用各个组件的初始化方法
 * 注意：以下模块中初始化的Module类不能被混淆
 */

public class ModuleLifecycleReflect {

    //基础模块
    private static final String BASE_INIT = "cn.lianshi.library.mvvmbase.component.base.BaseModuleInit";

    //三方库模块
    private static final String THIRD_LIB_INIT = "cn.lianshi.thirdlib.ThirdLibModuleInit";

    //登录验证模块
    private static final String USER_LOGIN_INIT = "cn.lianshi.login.UserLoginModuleInit";

    public static String[] initModuleNames = {BASE_INIT, USER_LOGIN_INIT,THIRD_LIB_INIT};
}
