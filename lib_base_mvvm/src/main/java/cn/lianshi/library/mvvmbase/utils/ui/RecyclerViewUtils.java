package cn.lianshi.library.mvvmbase.utils.ui;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


/**
 * Created by yx on 2018/1/15.
 * recyclerview工具类
 */
public class RecyclerViewUtils {



    /**
     * 判断横向recyclerview滑动最右边
     *
     * @param recyclerView
     * @return
     */
    public static boolean isHorizontalSlideToRight(RecyclerView recyclerView) {
        return recyclerView != null && recyclerView.computeHorizontalScrollExtent() +
            recyclerView.computeHorizontalScrollOffset() >= recyclerView.computeHorizontalScrollRange();
    }

    /**
     * 判断是否滑动到底部
     *
     * @param recyclerView
     * @return
     */
    public static boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        return visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE;
    }

    /**
     * 关闭默认局部刷新动画
     */
    public static void closeDefaultAnimator(RecyclerView recyclerView) {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }
}
