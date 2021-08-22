package cn.lianshi.library.mvvmbase.binding.viewadapter.smartrefresh;

import androidx.databinding.BindingAdapter;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import cn.lianshi.library.mvvmbase.binding.command.BindingCommand;

/**
 * @author yuxiao
 * @date 2019/2/22
 */
public final class ViewAdapter {

    @BindingAdapter(value = {"onRefreshCommand", "onLoadMoreCommand"},requireAll = false)
    public static void onRefreshCommand(SmartRefreshLayout refreshLayout,
                                        BindingCommand<RefreshLayout> refreshCommand,
                                        BindingCommand<RefreshLayout> loadMoreCommand) {

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (loadMoreCommand != null) {
                    loadMoreCommand.execute(refreshLayout);
                }

            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (refreshCommand != null) {
                    refreshCommand.execute(refreshLayout);

                }
            }
        });

    }
}
