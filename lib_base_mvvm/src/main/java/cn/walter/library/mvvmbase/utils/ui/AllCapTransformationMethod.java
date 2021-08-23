package cn.walter.library.mvvmbase.utils.ui;

import android.text.method.ReplacementTransformationMethod;

/**
 * Created by yx on 2018/03/23.
 * TextView、EditText中的大小写转换
 */
public class AllCapTransformationMethod extends ReplacementTransformationMethod {

    @Override
    protected char[] getOriginal() {
        char[] originalCharArr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return originalCharArr;
    }

    @Override
    protected char[] getReplacement() {
        char[] replacementCharArr = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return replacementCharArr;
    }

}