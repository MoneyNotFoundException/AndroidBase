package cn.lianshi.library.mvvmbase.utils.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.lianshi.library.mvvmbase.R;
import cn.lianshi.library.mvvmbase.base.BaseApplication;


/**
 * Created by yx on 2016/7/22.
 * 自定义单例吐司
 */
public class ToastUtils {

    private static Toast sToast;
    private static Toast sTopToast;

    public static void showToast(String content){
        Context context = BaseApplication.getContext();
        if (context==null){
            return;
        }
        showToast(context,content);
    }

    private static void showToast(Context context,String content){
        View view = LayoutInflater.from(context).inflate(R.layout.base_toast_layout,null);
        TextView tvToast = (TextView) view.findViewById(R.id.tv_toast);
        if(sToast == null) {
            sToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            sToast.setGravity(Gravity.BOTTOM,0, DensityUtils.dip2px(context,80));
        }
        tvToast.setText(content);
        sToast.setView(view);
        sToast.show();
    }

    public static void showPeriodToast(Context context,String content,String color){
        View view = LayoutInflater.from(context).inflate(R.layout.base_toast_layout_3,null);
        TextView tvToast = (TextView) view.findViewById(R.id.tv_toast);
        if(sToast == null) {
            sToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            sToast.setGravity(Gravity.BOTTOM,0, DensityUtils.dip2px(context,80));
        }
        tvToast.setText(content);
        sToast.setView(view);
        sToast.show();
    }

    public static void showToast(Context context,String content,String num){
        View view = LayoutInflater.from(context).inflate(R.layout.base_toast_layout_2,null);
        TextView tvToast = (TextView) view.findViewById(R.id.tv_toast);
        TextView tvToast2 = (TextView) view.findViewById(R.id.tv_num);
        if(sToast == null) {
            sToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            sToast.setGravity(Gravity.CENTER,0, DensityUtils.dip2px(context,80));
        }
        tvToast.setText(content);
        tvToast2.setText(num);
        sToast.setView(view);
        sToast.show();
    }

    public static void showTopToast(Context context,String content,int yOffset){
        if (content==null){
            return;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.base_toast_layout_3,null);
        TextView tvToast = (TextView) view.findViewById(R.id.tv_toast);
        if(sToast == null) {
            sToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            sToast.setGravity(Gravity.BOTTOM,0, DensityUtils.dip2px(context,80));
        }
        tvToast.setText(content);
        sToast.setView(view);
        sToast.show();
    }

}
