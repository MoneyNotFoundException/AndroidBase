package cn.walter.library.mvvmbase.binding.adpter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;

import java.util.List;

/**
 * @author yuxiao
 * @date 2019/3/29
 * 可拖拽的 recyclerView adapter dataBinding
 */
public abstract class BaseDragDataBindingAdapter<T, Binding extends ViewDataBinding> extends BaseItemDraggableAdapter<T, BaseBindingViewHolder<Binding>> {

    public BaseDragDataBindingAdapter(List<T> data) {
        super(data);
    }

    public BaseDragDataBindingAdapter(int layoutResId, List<T> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseBindingViewHolder<Binding> createBaseViewHolder(View view) {
        return new BaseBindingViewHolder<>(view);
    }

    @Override
    protected BaseBindingViewHolder<Binding> createBaseViewHolder(ViewGroup parent, int layoutResId) {
        Binding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        View view;
        if (binding == null) {
            view = getItemView(layoutResId, parent);
        } else {
            view = binding.getRoot();
        }
        BaseBindingViewHolder<Binding> holder = new BaseBindingViewHolder<>(view);
        holder.setBinding(binding);
        return holder;
    }


    @Override
    protected void convert(BaseBindingViewHolder<Binding> helper, T item) {
        convert(helper.getBinding(), item);
        helper.getBinding().executePendingBindings();
    }

    protected abstract void convert(Binding binding, T item);
}
