package cn.walter.library.mvvmbase.component.router;

import android.content.Context;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import cn.walter.library.mvvmbase.R;

/**
 * @author yuxiao
 * @date 2019/1/24
 * router控制器
 */
public class RouterManager {

    private static volatile RouterManager mManager;

    public static RouterManager getInstance() {
        if (mManager == null) {
            synchronized (RouterManager.class) {
                if (mManager == null) {
                    mManager = new RouterManager();
                }
            }
        }

        return mManager;

    }

    private RouterManager() {
    }


    public void inject(Object obj){
        ARouter.getInstance().inject(obj);
    }

    /**
     * 通过boolean少写个动画代码
     * @param path
     * @param needAnim
     * @return
     */
    public Postcard buildPath(String path, boolean needAnim) {
        Postcard build = ARouter.getInstance().build(path);
        if (needAnim) {
            build.withTransition(R.anim.base_anim_enter, R.anim.base_anim_exit);
        }

        return build;
    }

    public Postcard buildPath(String path) {
        return  buildPath(path,true);
    }

    /**
     * '
     * 通过boolean少写个动画代码
     * @param uri
     * @param needAnim
     * @return
     */
    public Postcard buildUri(Uri uri,boolean needAnim) {
        Postcard build = ARouter.getInstance().build(uri);
        if (needAnim) {
            build.withTransition(R.anim.base_anim_enter, R.anim.base_anim_exit);
        }
        return build;
    }

    public Postcard buildUri(Uri uri) {
        return buildUri(uri,true);
    }

    /**
     * 构建fragment
     * @param fragment_path
     * @return
     */
    public Fragment buildFragment(String fragment_path) {
        return (Fragment) ARouter.getInstance().build(fragment_path).navigation();
    }

    /**
     * 打开登录页
     */
    public void openLoginPage() {
        openLoginPage(null);
    }

    public void openLoginPage(Context context) {
        openLoginPage(context,true);
    }

    public void openLoginPage(Context context,boolean needAnim) {
        openLoginPage(context,needAnim,"");
    }

    public void openLoginPage(Context context,boolean needAnim,String phone) {
        RouterManager.getInstance()
            .buildPath(RouterActivityPath.Login.PAGER_LOGIN, needAnim)
            .withString("phone",phone)
            .navigation(context);


    }




}
