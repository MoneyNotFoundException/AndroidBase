package cn.lianshi.library.mvvmbase.binding.adpter;

import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @author yuxiao
 * @date 19/1/18
 * recyclerView  adapter viewHolder dataBinding
 * @param <Binding>
 */
public class BaseBindingViewHolder<Binding extends ViewDataBinding> extends BaseViewHolder {
    private Binding mBinding;

    public BaseBindingViewHolder(View view) {
        super(view);
    }

    public Binding getBinding() {
        return mBinding;
    }

    public void setBinding(Binding binding) {
        mBinding = binding;
    }


}
