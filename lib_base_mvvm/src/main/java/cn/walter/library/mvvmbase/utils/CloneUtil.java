package cn.walter.library.mvvmbase.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author crimson
 * @date 2019-09-06
 */
public class CloneUtil {

    public static <T extends Serializable> T clone(T obj){
        T cloneObj=null;
        try{
            //写入字节流
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            //分配内存,写入原始对象,生成新对象
            ByteArrayInputStream bais=new ByteArrayInputStream(baos.toByteArray());//获取上面的输出字节流
            ObjectInputStream ois=new ObjectInputStream(bais);

            //返回生成的新对象
            cloneObj=(T)ois.readObject();
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return cloneObj;
    }

}
