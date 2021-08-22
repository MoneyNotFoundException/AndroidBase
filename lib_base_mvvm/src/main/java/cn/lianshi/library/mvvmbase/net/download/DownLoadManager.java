package cn.lianshi.library.mvvmbase.net.download;

import cn.lianshi.library.mvvmbase.base.BaseApplication;
import cn.lianshi.library.mvvmbase.net.RetrofitClient;
import cn.lianshi.library.mvvmbase.net.api.AppNetService;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 文件下载管理，封装一行代码实现下载
 */

public class DownLoadManager {

    private static volatile DownLoadManager sManager;

    public static DownLoadManager getInstance() {
        if (sManager == null) {
            synchronized (DownLoadManager.class) {
                if (sManager == null) {
                    sManager = new DownLoadManager();
                }
            }
        }

        return sManager;
    }

    private DownLoadManager() {
    }

    //下载
    public void downloadFile(String downUrl, final ProgressCallBack callBack) {



    }


}
