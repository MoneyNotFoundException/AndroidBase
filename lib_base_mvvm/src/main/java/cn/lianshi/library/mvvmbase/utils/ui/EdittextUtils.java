package cn.lianshi.library.mvvmbase.utils.ui;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.lianshi.library.mvvmbase.utils.rxui.TextviewTextChangeListener;


/**
 * @author yuxiao
 * @date 2018/4/12
 * 输入框工具类
 */
public class EdittextUtils {

    /**
     * 禁止EditText输入特殊字符
     *
     * @param
     */
    public static InputFilter getEditTextInhibitInputSpeChat() {

        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                String speChat = "[^a-zA-Z0-9\\u4E00-\\u9FA5]";
//                Pattern pattern = Pattern.compile(speChat);
//                Matcher matcher = pattern.matcher(source.toString());
//                if (matcher.find()) return "";

                Pattern emoji = Pattern.compile(
                    "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find()) {
                    return "";
                } else return null;
            }
        };
    }

    public static InputFilter getEditTextLength(int mMax) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int keep = mMax - (dest.length() - (dend - dstart));
                if (keep <= 0) {
                    return "";
                } else if (keep >= end - start) {
                    return null; // keep original
                } else {
                    keep += start;
                    if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                        --keep;
                        if (keep == start) {
                            return "";
                        }
                    }
                    return source.subSequence(start, keep);
                }
            }
        };
    }


    /**
     * 限制输入的小数最多为两位
     *
     * @return
     */
    public static TextWatcher getEditTextWatcherInputDecimal(EditText editText, TextviewTextChangeListener changeListener) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(editText.getText().length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(editText.getText().toString().length());
                }

                if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeListener.onTextChange(s.toString());
            }
        };
    }
}
