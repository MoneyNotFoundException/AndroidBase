package cn.lianshi.library.mvvmbase.widget.abslistview.base;


import cn.lianshi.library.mvvmbase.widget.abslistview.ViewHolder;

public interface ItemViewDelegate<T>
{

    public abstract int getItemViewLayoutId();

    public abstract boolean isForViewType(T item, int position);

    public abstract void convert(ViewHolder holder, T t, int position);



}
