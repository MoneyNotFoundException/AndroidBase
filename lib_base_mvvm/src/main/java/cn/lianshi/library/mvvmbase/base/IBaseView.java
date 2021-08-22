package cn.lianshi.library.mvvmbase.base;


/**
 * 页面实现接口
 */
public interface IBaseView {

    /**
     * 初始化页面，为结合懒加载的fragmet特地开辟的方法
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化界面观察者的监听
     */
    void initViewObservable();


}
