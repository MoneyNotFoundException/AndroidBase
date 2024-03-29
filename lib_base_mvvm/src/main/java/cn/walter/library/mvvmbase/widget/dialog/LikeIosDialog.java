package cn.walter.library.mvvmbase.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import cn.walter.library.mvvmbase.R;


/**
 * @author yuxiao
 * @date 2018/3/30
 * 仿ios dialog
 */

public class LikeIosDialog extends Dialog {

    private OnDialogDismissListener mOnDialogDismissListener;

    private LikeIosDialog(Context mContext, boolean canceledOnTouchOutside, View mContentView, String title, String message, int titleSize, int titleColor, int messageSize, int messageColor, OnClickListener onClickListenerNegativeButton, OnClickListener onClickListenerPositiveButton, String textNegativeButton, String textPositiveButton, int negativeButtonSize, int positiveButtonSize, int negativeButtonColor, int positiveButtonColor, OnDialogDismissListener listener) {
        super(mContext, R.style.IosDialog);
        mOnDialogDismissListener = listener;
        setOnDismissListener(mOnDialogDismissListener);
        createDialog(mContext, canceledOnTouchOutside, mContentView, title, message, titleSize, titleColor, messageSize, messageColor,
            onClickListenerNegativeButton, onClickListenerPositiveButton, textNegativeButton, textPositiveButton, negativeButtonSize,
            positiveButtonSize, negativeButtonColor, positiveButtonColor);
    }

    private void createDialog(Context mContext, boolean canceledOnTouchOutside, View mContentView, String title, String message, int titleSize, int titleColor, int messageSize, int messageColor, final OnClickListener onClickListenerNegativeButton, final OnClickListener onClickListenerPositiveButton, String textNegativeButton, String textPositiveButton, int negativeButtonSize, int positiveButtonSize, int negativeButtonColor, int positiveButtonColor) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(new ViewGroup.LayoutParams(dip2px(mContext, 200), ViewGroup.LayoutParams.WRAP_CONTENT));
        // layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, dip2px(mContext, 25), 0, 0);

        LinearLayout layoutContent = new LinearLayout(mContext);
        layoutContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        layoutContent.setOrientation(LinearLayout.VERTICAL);
        layoutContent.setPadding(0, 0, 0, dip2px(mContext, 20));
        if (!TextUtils.isEmpty(title)) {
            TextView textTitle = new TextView(mContext);
            textTitle.setText(title);
            textTitle.setTextColor(ContextCompat.getColor(mContext, R.color.base_colorText34));
            textTitle.setTextSize(titleSize);
            if (titleColor != -1) {
                textTitle.setTextColor(titleColor);
            }
            textTitle.getPaint().setTypeface(Typeface.DEFAULT_BOLD);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            textTitle.setLayoutParams(lp);
            layoutContent.addView(textTitle);
        }

        if (!TextUtils.isEmpty(message)) {
            TextView textMessage = new TextView(mContext);
            textMessage.setText(message);
            textMessage.setTextColor(ContextCompat.getColor(mContext, R.color.base_colorText6));
            textMessage.setTextSize(messageSize);
            if (messageColor != -1) {
                textMessage.setTextColor(messageColor);
            }
            textMessage.setGravity(Gravity.CENTER);
            textMessage.setPadding(dip2px(mContext, 20), dip2px(mContext, 10), dip2px(mContext, 20), 0);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            textMessage.setLayoutParams(lp);
            layoutContent.addView(textMessage);
        }

        if (mContentView != null) {
            layoutContent.addView(mContentView);

            //如果第一个view是ImageView，就不设置padding
            if (mContentView instanceof ViewGroup) {
                View child = ((ViewGroup) mContentView).getChildAt(0);
                if (child instanceof ImageView) {
                    layout.setPadding(0, 0, 0, 0);
                }
            }

        }

        LinearLayout layoutButton = new LinearLayout(mContext);
        layoutButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        layout.addView(layoutContent);
        layout.addView(layoutButton);
        if (onClickListenerNegativeButton != null) {
            if (onClickListenerPositiveButton == null) { // 只有一个按钮
                layoutButton.setOrientation(LinearLayout.VERTICAL);
                Button negativeButton = new Button(mContext);
                negativeButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
                negativeButton.setTextColor(negativeButtonColor);
                negativeButton.setBackgroundResource(R.drawable.base_ios_dialog_button);
                negativeButton.setText(textNegativeButton);
                if (negativeButtonSize != -1) {
                    negativeButton.setTextSize(negativeButtonSize);
                }
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListenerNegativeButton.onClick(LikeIosDialog.this, v);
                    }
                });
                layoutButton.addView(negativeButton);
            } else {//两个按钮
                layoutButton.setOrientation(LinearLayout.HORIZONTAL);
                Button negativeButton = new Button(mContext);
                negativeButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
                negativeButton.setTextColor(negativeButtonColor);
                negativeButton.setBackgroundResource(R.drawable.base_ios_dialog_left_button);
                negativeButton.setText(textNegativeButton);
                if (negativeButtonSize != -1) {
                    negativeButton.setTextSize(negativeButtonSize);
                }
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListenerNegativeButton.onClick(LikeIosDialog.this, v);
                    }
                });
                layoutButton.addView(negativeButton);

                Button positiveButton = new Button(mContext);
                positiveButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
                positiveButton.setTextColor(positiveButtonColor);
                positiveButton.setBackgroundResource(R.drawable.base_ios_dialog_right_button);
                positiveButton.setText(textPositiveButton);
                if (positiveButtonSize != -1) {
                    positiveButton.setTextSize(positiveButtonSize);
                }
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListenerPositiveButton.onClick(LikeIosDialog.this, v);
                    }
                });
                layoutButton.addView(positiveButton);
            }
        } else {
            if (onClickListenerPositiveButton != null) {  // 只有一个按钮
                layoutButton.setOrientation(LinearLayout.VERTICAL);
                Button positiveButton = new Button(mContext);
                positiveButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(mContext, 44), 1.0f));
                positiveButton.setTextColor(positiveButtonColor);
                positiveButton.setBackgroundResource(R.drawable.base_ios_dialog_button);
                positiveButton.setText(textPositiveButton);
                if (positiveButtonSize != -1) {
                    positiveButton.setTextSize(positiveButtonSize);
                }
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListenerPositiveButton.onClick(LikeIosDialog.this, v);
                    }
                });
                layoutButton.addView(positiveButton);
            }
        }

        final int viewWidth = dip2px(mContext, 300);
        layout.setMinimumWidth(viewWidth);
        setContentView(layout);
        setCanceledOnTouchOutside(canceledOnTouchOutside);
//        return this;
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnClickListener {
        void onClick(LikeIosDialog dialog, View v);
    }

    public interface OnDialogDismissListener extends OnDismissListener {
        void beforeDismiss(DialogInterface dialog);
    }

    @Override
    public void dismiss() {
        if (mOnDialogDismissListener != null) {
            mOnDialogDismissListener.beforeDismiss(this);
        }
        super.dismiss();
    }

    public static class Builder {
        private Context mContext;
        private boolean canceledOnTouchOutside = true;
        private View mContentView;
        //标题，内容
        private String title, message;
        private int titleSize = 18, titleColor = -1;
        private int messageSize = 16, messageColor = -1;

        //点击事件，按钮
        private OnClickListener onClickListenerNegativeButton, onClickListenerPositiveButton;
        private String textNegativeButton, textPositiveButton;
        private int negativeButtonSize = -1, positiveButtonSize = -1;
        private int negativeButtonColor = Color.parseColor("#439AFC");
        private int positiveButtonColor = negativeButtonColor;

        private OnDialogDismissListener mOnDialogDismissListener;

        private LikeIosDialog mDialog;

        public LikeIosDialog build() {
            mDialog = new LikeIosDialog(mContext, canceledOnTouchOutside, mContentView, title, message, titleSize, titleColor, messageSize, messageColor,
                onClickListenerNegativeButton, onClickListenerPositiveButton, textNegativeButton, textPositiveButton, negativeButtonSize,
                positiveButtonSize, negativeButtonColor, positiveButtonColor, mOnDialogDismissListener);
            return mDialog;
        }

        public void show() {
            if (mDialog == null) {
                build();
            }
            mDialog.show();
            mDialog = null;
        }

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int titleId) {
            this.title = mContext.getString(titleId);
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setTitleSize(int size) {
            this.titleSize = size;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int messageId) {
            this.message = mContext.getString(messageId);
            return this;
        }

        public Builder setMessageSize(int size) {
            this.messageSize = size;
            return this;
        }

        public Builder setMessageColor(int titleColor) {
            this.messageColor = titleColor;
            return this;
        }

        /**
         * 设置左边按钮---取消
         */
        public Builder setNegativeButton(String text, OnClickListener onClickListener) {
            this.textNegativeButton = text;
            this.onClickListenerNegativeButton = onClickListener;
            return this;
        }

        /**
         * 设置左边按钮---取消
         */
        public Builder setNegativeButton(int textId, OnClickListener onClickListener) {
            this.textNegativeButton = mContext.getString(textId);
            this.onClickListenerNegativeButton = onClickListener;
            return this;
        }

        public Builder setNegativeButtonColor(int color) {
            negativeButtonColor = color;
            return this;
        }

        public Builder setNegativeButtonSize(int size) {
            negativeButtonSize = size;
            return this;
        }

        /**
         * 设置右边按钮 ---确定
         */
        public Builder setPositiveButton(String text, OnClickListener onClickListener) {
            this.textPositiveButton = text;
            this.onClickListenerPositiveButton = onClickListener;
            return this;
        }

        /**
         * 设置右边按钮 ---确定
         */
        public Builder setPositiveButton(int textId, OnClickListener onClickListener) {
            this.textPositiveButton = mContext.getString(textId);
            this.onClickListenerPositiveButton = onClickListener;
            return this;
        }

        public Builder setPositiveButtonColor(int color) {
            positiveButtonColor = color;
            return this;
        }

        public Builder setPositiveButtonSize(int size) {
            positiveButtonSize = size;
            return this;
        }

        /**
         * 设置能否点击外部取消弹出框
         */
        public Builder setDialogCanceledOnTouchOutside(boolean can) {
            this.canceledOnTouchOutside = can;
            return this;
        }

        /**
         * 设置显示的View
         */
        public Builder setView(View v) {
            this.mContentView = v;
            return this;
        }

        /**
         * 设置Dialog消失监听
         */
        public Builder setOnDialogDismissListener(OnDialogDismissListener listener) {
            this.mOnDialogDismissListener = listener;
            return this;
        }
    }
}
