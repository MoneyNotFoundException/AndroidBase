package cn.walter.library.mvvmbase.utils.ui;

import android.content.Context;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import cn.walter.library.mvvmbase.R;

/**
 * @author yuxiao
 * @date 2019/3/6
 * 选择器工具
 */
public class PickerViewUtils {

    /**
     *获取时间选择器
     * @param context
     * @param listener
     * @return
     */
    public static TimePickerView.Builder getTimePickerView(Context context, TimePickerView.OnTimeSelectListener listener) {
        return new TimePickerView.Builder(context,listener)
            .setType(new boolean[]{true, true, true, true, true, true})// 默认全部显示
            .setCancelText("取消")//取消按钮文字
            .setSubmitText("确定")//确认按钮文字
            .setContentSize(18)//滚轮文字大小
            .setTitleSize(18)//标题文字大小
            .setSubCalSize(16)//确定和取消文字大小
            .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(false)//是否循环滚动
            .setTitleColor(context.getResources().getColor(R.color.base_colorText34))//标题文字颜色
            .setSubmitColor(context.getResources().getColor(R.color.base_colorPrimaryDark))//确定按钮文字颜色
            .setCancelColor(context.getResources().getColor(R.color.base_colorText6))//取消按钮文字颜色
            .isCenterLabel(false)
            .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
            .isDialog(false);
    }

    /**
     * 获取条件选择器
     * @param context
     * @param listener
     * @return
     */
    public static OptionsPickerView.Builder getOptionPickerView(Context context, OptionsPickerView.OnOptionsSelectListener listener){
        return new OptionsPickerView.Builder(context, listener)
            .setSubmitText("确定")//确定按钮文字
            .setCancelText("取消")//取消按钮文字
            .setSubCalSize(16)//确定和取消文字大小
            .setTitleSize(18)//标题文字大小
            .setTitleColor(context.getResources().getColor(R.color.base_colorText34))//标题文字颜色
            .setSubmitColor(context.getResources().getColor(R.color.base_colorPrimaryDark))//确定按钮文字颜色
            .setCancelColor(context.getResources().getColor(R.color.base_colorText6))//取消按钮文字颜色
            .setContentTextSize(18)//滚轮文字大小
            .setCyclic(false, false, false)//循环与否
            .isCenterLabel(false)
            .setSelectOptions(0, 0, 0)  //设置默认选中项
            .setOutSideCancelable(true)//点击外部dismiss default true
            .isDialog(false);
    }

}
