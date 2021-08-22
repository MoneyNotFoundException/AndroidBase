package cn.lianshi.library.mvvmbase.database;

import android.content.ContentValues;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.Collection;
import java.util.List;

/**
 * @author yuxiao
 * @date 2019/1/28
 * lite pal 数据库管理器
 *
 * 单个的保存和删除直接可以拿对象操作
 *
 */
public class LitePalManager {

    private static volatile LitePalManager sManager;

    public static LitePalManager getInstance() {
        if (sManager == null) {
            synchronized (LitePalManager.class) {
                if (sManager == null) {
                    sManager = new LitePalManager();
                }
            }
        }

        return sManager;
    }

    private LitePalManager() {

    }

    /**
     * 查询返回所有的数据
     *
     * @param litepalClass
     * @param <T>
     * @return
     */
    public <T extends LitePalSupport> List<T> findAllData(Class<T> litepalClass) {
        return LitePal.findAll(litepalClass);
    }

    /**
     * 查询返回第一个数据
     *
     * @param litepalClass
     * @param <T>
     * @return
     */
    public <T extends LitePalSupport> T findFirstData(Class<T> litepalClass) {
        return LitePal.findFirst(litepalClass);
    }

    /**
     * 根据查询条件返回数据数组
     *
     * @param litepalClass
     * @param conditions
     * @param <T>
     * @return
     */
    public <T extends LitePalSupport> List<T> findDataWithConditions(Class<T> litepalClass, String... conditions) {
        return LitePal.where(conditions).find(litepalClass);
    }

    /**
     * 根据查询条件返回第一个数据
     *
     * @param litepalClass
     * @param conditions
     * @param <T>
     * @return
     */
    public <T extends LitePalSupport> T findFirstDataWithConditions(Class<T> litepalClass, String... conditions) {
        return LitePal.where(conditions).findFirst(litepalClass);
    }

    /**
     * 根据对象字段条件排序返回数据
     * @param litepalClass
     * @param column
     * @param <T>
     * @return
     */
    public <T extends LitePalSupport> List<T> orderDataWithConditons(Class<T> litepalClass, String  column) {
        return LitePal.order(column).find(litepalClass);
    }

    /**
     * 根据对象字段条件排序返回第一个数据
     * @param litepalClass
     * @param column
     * @param <T>
     * @return
     */
    public <T extends LitePalSupport> T orderFirstDataWithConditons(Class<T> litepalClass, String  column) {
        return LitePal.order(column).findFirst(litepalClass);
    }


    /**
     * 删除所有数据
     * @param litepalClass
     *
     */
    public void deleteAllData(Class<? extends LitePalSupport> litepalClass) {
        LitePal.deleteAll(litepalClass);
    }



    /**
     * 根据查询条件删除所有数据
     * @param litepalClass
     * @param conditions
     */
    public void deleteAllData(Class<? extends LitePalSupport> litepalClass,String...conditions) {
       LitePal.deleteAll(litepalClass, conditions);
    }

    /**
     * 保存所有数据
     * @param collection
     */
    public void saveAllData(Collection<? extends LitePalSupport> collection) {
        LitePal.saveAll(collection);
    }

    /**
     * 根据查询条件更新查询到的所有数据
     * @param litepalClass
     * @param conditions
     */
    public void updateAllData(Class<? extends LitePalSupport> litepalClass, String... conditions) {
        LitePal.updateAll(litepalClass, new ContentValues(), conditions);
    }




}
