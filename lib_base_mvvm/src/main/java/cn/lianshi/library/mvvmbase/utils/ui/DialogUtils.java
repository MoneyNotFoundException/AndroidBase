package cn.lianshi.library.mvvmbase.utils.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.umeng.commonsdk.debug.W;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.lianshi.library.mvvmbase.R;
import cn.lianshi.library.mvvmbase.utils.rxui.RxViewUtils;
import cn.lianshi.library.mvvmbase.widget.abslistview.CommonAdapter;
import cn.lianshi.library.mvvmbase.widget.abslistview.ViewHolder;
import cn.lianshi.library.mvvmbase.widget.dialog.LikeIosDialog;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by yx on 2016/8/11.
 * dialog工具类
 */
public class DialogUtils {


    /***
     * 获取一个耗时等待对话框
     *
     * @param context
     * @param message
     * @return
     */
    public static Dialog getWaitDialog(Context context, String message) {
        return getWaitDialog(context, message, true);
    }

    public static List<String> createList(String... str) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, str);
        return list;
    }

    public static void showListViewDialog(Context context, List<String> list, String title, OnItemClickListener listener) {
        MaterialDialog materialDialog = new MaterialDialog(context).setTitle(title).setCanceledOnTouchOutside(true);
        ListView listView = new ListView(context);
        listView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setDividerHeight(0);
        listView.setAdapter(new CommonAdapter<String>(context, R.layout.base_item_shaixuan, list) {
            @Override
            protected void convert(ViewHolder viewHolder, String str, int position) {
                viewHolder.setText(R.id.tv_riqi, str);
            }
        });
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            materialDialog.dismiss();
            listener.onItemClick(view1, position);

        });
        materialDialog.setContentView(listView);
        materialDialog.show();

    }


    public static Dialog getWaitDialog(Context context, String message, boolean isCancel) {
        Dialog dialog = new Dialog(context, R.style.progress_dialog);
        dialog.setCancelable(isCancel);
        dialog.setContentView(R.layout.base_custom_loading_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        AppCompatTextView tvMsg = dialog.findViewById(R.id.tv_loading);
        if (isCancel) {
            dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialogInterface.dismiss();
                }
                return false;
            });
        }

        if (!message.isEmpty()) {
            tvMsg.setText(message);
        }
        return dialog;
    }

    /**
     * 获取消息弹出框
     *
     * @param context
     * @param title
     * @param msg
     * @return
     */
    public static MaterialDialog getMessageDialog(Context context, String title, String msg) {
        return getMessageDialog(context, title, msg, true);
    }

    public static MaterialDialog getMessageDialog(Context context, String title, String msg, boolean cancle) {
        return new MaterialDialog(context)
            .setTitle(title)
            .setMessage(msg)
            .setCanceledOnTouchOutside(cancle);

    }

    public static void showMsgDialog(Context context, String title, String text, String confirm, String cancle,
                                     OnDialogConfirmListener confirmListener, OnDialogCancleListener cancleListener) {
        MaterialDialog materialDialog = new MaterialDialog(context).setCanceledOnTouchOutside(true)
            .setTitle(title).setMessage(text);
        materialDialog.setPositiveButton(confirm, v -> {
            materialDialog.dismiss();
            confirmListener.onConfirmClick(v);
        }).setNegativeButton(cancle, v -> {
            cancleListener.onCancleClick(v);
            materialDialog.dismiss();
        });
        materialDialog.show();

    }

    public static void showLogoutDialog(Context context, String title,  String confirm, String cancle,
                                     OnDialogConfirmListener confirmListener, OnDialogCancleListener cancleListener) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
        Window window = dialog.getWindow();      // 得到dialog的窗体
        window.setGravity(Gravity.BOTTOM);
        View view = View.inflate(context, R.layout.base_dialog_logout, null);
        dialog.setContentView(view);
        TextView tvtitle = view.findViewById(R.id.tv_title);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvtitle.setText(title);
        tvConfirm.setText(confirm);
        tvCancel.setText(cancle);

        RxViewUtils.onViewClick(tvConfirm,() ->{
            if(confirmListener != null) {
                dialog.dismiss();
                confirmListener.onConfirmClick(tvConfirm);
            }
        });

        RxViewUtils.onViewClick(tvCancel,() ->{
            if(cancleListener != null) {
                dialog.dismiss();
                cancleListener.onCancleClick(tvCancel);
            }
        });

        dialog.show();
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    public static void showMsgDialog(Context context, String title, String text, String confirm, boolean isCancle,
                                     OnDialogConfirmListener confirmListener) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        MaterialDialog materialDialog = new MaterialDialog(context).setCanceledOnTouchOutside(isCancle).setTitle(title)
            .setMessage(text);
        materialDialog.setPositiveButton(confirm, v -> {
            materialDialog.dismiss();
            confirmListener.onConfirmClick(v);
        });
        materialDialog.show();

    }


    public interface OnDialogConfirmListener {
        void onConfirmClick(View view);
    }

    public interface OnDialogOtherConfirmListener {
        void onOtherConfirmClick(View view, Dialog dialog);
    }

    public interface onDialogYzmChangeListener {
        void onYzmChangeListener(ImageView view);
    }

    public interface OnDialogCancleListener {
        void onCancleClick(View view);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Integer integer);
    }

    public interface OnDialogDismissListener {
        void onDismiss(DialogInterface dialogInterface);

    }

    public static void showCustomFileDialog(Context context, String title, String hint, String etHint, String errorMsg, OnDialogConfirmListener confirmListener) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
        View view = View.inflate(context, R.layout.base_dialog_new_file, null);
        dialog.setContentView(view);
        TextView tvTitle = view.findViewById(R.id.title_text);
        TextView tvHint = view.findViewById(R.id.tv_hint);
        tvTitle.setText(title);
        if (hint.isEmpty()) {
            tvHint.setVisibility(View.GONE);
        } else {
            tvHint.setText(hint);
            tvHint.setVisibility(View.VISIBLE);
        }
        EditText edit = view.findViewById(R.id.et_file_name);
        edit.setHint(etHint);
        Button confirm = view.findViewById(R.id.confirm_button);
        Button cancel = view.findViewById(R.id.cancel_button);
        confirm.setOnClickListener(v -> {
            if (edit.getText().toString().isEmpty()) {
                ToastUtils.showToast(errorMsg);
            } else {
                dialog.dismiss();
                confirmListener.onConfirmClick(edit);
            }
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public static LikeIosDialog likeIosDialog(Context context, String message, String ok) {
        LikeIosDialog.Builder builder = new LikeIosDialog.Builder(context);
        Resources resources = context.getResources();
        builder.setMessage(message);
        builder.setMessageColor(resources.getColor(R.color.base_colorText3));
        builder.setMessageSize(18);
        builder.setPositiveButtonColor(resources.getColor(R.color.base_colorPrimaryDark));
        builder.setPositiveButtonSize(16);
        builder.setNegativeButtonColor(resources.getColor(R.color.base_colorText6));
        builder.setNegativeButtonSize(16);
        builder.setDialogCanceledOnTouchOutside(false);
        //        //                    //点击dialog中的取消按钮
        //        //                    builder.setNegativeButton("取消", (dialog1, v) -> dialog1.dismiss());
        //        //点击dialog中的取消按钮
        builder.setPositiveButton(ok, (dialog1, v) -> {
            dialog1.dismiss();
        });
        LikeIosDialog dialog = builder.build();
        Window window = dialog.getWindow();
        window.setDimAmount(0.5f);//设置昏暗度为0
        dialog.setCancelable(false);
        return dialog;
    }

    public static LikeIosDialog.Builder likeIosDialogBuilder(Context context, String message) {
        LikeIosDialog.Builder builder = new LikeIosDialog.Builder(context);
        Resources resources = context.getResources();
        builder.setMessage(message);
        builder.setMessageColor(resources.getColor(R.color.base_colorText3));
        builder.setMessageSize(18);
        builder.setPositiveButtonColor(resources.getColor(R.color.base_colorPrimaryDark));
        builder.setPositiveButtonSize(16);
        builder.setNegativeButtonColor(resources.getColor(R.color.base_colorText6));
        builder.setNegativeButtonSize(16);
        builder.setDialogCanceledOnTouchOutside(false);
        return builder;
    }

    /**
     * 重命名
     *
     * @param context
     * @param errorMsg
     * @param confirmListener
     */
    public static void showRenameDialog(Context context, String errorMsg, OnDialogConfirmListener confirmListener) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
        View view = View.inflate(context, R.layout.base_dialog_rename, null);
        dialog.setContentView(view);
        EditText edit = view.findViewById(R.id.et_file_name);
        Button confirm = view.findViewById(R.id.confirm_button);
        Button cancel = view.findViewById(R.id.cancel_button);
        confirm.setOnClickListener(v -> {
            if (edit.getText().toString().isEmpty()) {
                ToastUtils.showToast(errorMsg);
            } else {
                dialog.dismiss();
                confirmListener.onConfirmClick(edit);
            }
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }



    public static LikeIosDialog setupNickName(Context context, OnDialogListener onDialogListener) {
        View view = View.inflate(context, R.layout.base_dialog_setup_nickname, null);
        AppCompatEditText mEtName = view.findViewById(R.id.et_name);
        mEtName.setFilters(new InputFilter[]{EdittextUtils.getEditTextInhibitInputSpeChat(), new InputFilter.LengthFilter(6)});
        LikeIosDialog mDialog = new LikeIosDialog.Builder(context)
            .setTitle("设置昵称")
            .setView(view)
            .setDialogCanceledOnTouchOutside(true)
            .setPositiveButton("确定", (dialog, v) -> {

                if (mEtName.getText().toString().trim().isEmpty()) {
                    ToastUtils.showToast(mEtName.getHint().toString());
                    return;
                }


                dialog.dismiss();

                onDialogListener.onDialogListener(mEtName.getText().toString());
            })
            .setNegativeButton("取消", (dialog, v) -> dialog.dismiss())
            .build();
        return mDialog;
    }



    public interface OnDialogListener {
        void onDialogListener(String str);
    }

}
