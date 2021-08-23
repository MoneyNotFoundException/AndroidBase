package cn.walter.library.mvvmbase.bus.rx;

/**
 * Created by yx on 17/5/31.
 * rxbus消息对象
 */
public class RxBusBaseMessage {
    private int code;
    private Object object;

    public RxBusBaseMessage(int code, Object object){
        this.code=code;
        this.object=object;
    }

    public RxBusBaseMessage(){}

    public int getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }
}
