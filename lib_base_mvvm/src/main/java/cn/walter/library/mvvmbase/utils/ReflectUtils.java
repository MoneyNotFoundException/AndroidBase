package cn.walter.library.mvvmbase.utils;

import java.lang.reflect.Field;

/**
 * @author yuxiao
 * @date 2019/2/25
 * 反射工具类
 */
public class ReflectUtils {

    /**
     * 暴力反射获取成员变量
     * @param <T> 成员变量对象转换
     * @param clazzName class name
     * @param fieldName 成员变量名
     * @param obj 反射的实体
     */
    public static <T> T getDeclaredField(String clazzName, Object obj,String fieldName) {
        try {
            Class<?> clazz = Class.forName(clazzName);
            Field field = clazz.getDeclaredField(fieldName);//变量名称
            field.setAccessible(true);//开放权限
            return (T) field.get(obj);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }




}
