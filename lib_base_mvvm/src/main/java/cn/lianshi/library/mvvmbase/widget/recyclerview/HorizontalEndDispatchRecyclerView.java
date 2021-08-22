package cn.lianshi.library.mvvmbase.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * @author yuxiao
 * @date 2018/12/11
 * 到最后一个item拦截事件分发的recyclerview
 */
public class HorizontalEndDispatchRecyclerView extends RecyclerView {

    private boolean interceptTouch = false;

    public HorizontalEndDispatchRecyclerView(Context context) {
        super(context);
    }

    public HorizontalEndDispatchRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // interceptTouch是自定义属性控制是否拦截事件
        if (interceptTouch){
            ViewParent parent =this;
            // 循环查找ViewPager, 请求ViewPager不拦截触摸事件
            while(!((parent = parent.getParent()) instanceof ViewPager)){
                // nop
            }

            parent.requestDisallowInterceptTouchEvent(true);
        }

        return super.dispatchTouchEvent(ev);

    }

    public void setInterceptTouch(boolean interceptTouch) {
        this.interceptTouch = interceptTouch;
    }
}
