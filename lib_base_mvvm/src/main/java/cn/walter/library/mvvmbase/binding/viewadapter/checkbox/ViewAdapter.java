package cn.walter.library.mvvmbase.binding.viewadapter.checkbox;

import androidx.databinding.BindingAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cn.walter.library.mvvmbase.binding.command.BindingCommand;




public class ViewAdapter {
    /**
     * @param bindingCommand //绑定监听
     */
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void setCheckedChanged(final CheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bindingCommand.execute(b);
            }
        });
    }
}
