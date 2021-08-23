package cn.walter.library.mvvmbase.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StringCheckUtils
 * Created by jinqiu_xiong on 2017/8/30.
 */

public class StringCheckUtils {
    /**
     * 判断手机号是否合法
     */
    public static boolean isMobile(String mobile) {
        //不同国家先检查是否是数字
        if (TextUtils.isEmpty(mobile))
            return false;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 判断座机号
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNumber(String phone) {
        /*
            0xxx-xxxxxxxx
            (010|02\\d|0[3-9]\\d{2})
        */
        String phoneRegex = "(010|02[0-9]|0[3-9][0-9]{2})[-]{0,1}[0-9]{7,8}";
        if (TextUtils.isEmpty(phone))
            return false;
        else
            return phone.matches(phoneRegex);
    }

    /**
     * 判断座机号或手机号
     *
     * @param no
     * @return
     */
    public static boolean isPhoneOrMobile(String no) {
        return isMobile(no) || isPhoneNumber(no);
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email))
            return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public static  boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * 是否包含中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str){
        if (TextUtils.isEmpty(str))
            return false;
        Pattern p = Pattern.compile("^((?!(\\*|//)).)+[\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }



    public static String  persion(double number ) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(number);
    }

    /**
     * 半角转换为全角
     *
     * @param str
     * @return
     */
    public static String ToDBC(String str) {
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * 规则2：至少包含大小写字母及数字中的两种
     * 是否包含
     *
     * @param str
     * @return
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = isDigit && isLetter && str.matches(regex);
        return isRight;
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 验证是否是数字串
     *
     * @param src
     * @return
     */
    public static boolean verifyNumber(String src) {
        if (TextUtils.isEmpty(src)) {
            return false;
        }
        return Pattern.compile("[0-9]+").matcher(src).matches();
    }

    /**
     * 验证是否是日期
     */
    public static boolean verifyDateTime(String src) {
        if (TextUtils.isEmpty(src)) {
            return false;
        }
        return Pattern.compile("((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))").matcher(src).matches();
    }

    /**
     * null值替换为空
     */

    public static String isEmptyStr(String string) {
        if ("null".equals(string) || null == string) {
            return "";
        }
        return string;
    }

    //验证是否是网址
    public static boolean verifySiteUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return Pattern.compile("[a-zA-z]+://[^\\s]*").matcher(url).matches();
    }

    //转为千分位，并带2位小数点
    public static String formatMoneyThousand(float money) {
        //return DecimalFormat.getNumberInstance().format(money);
        DecimalFormat format = new DecimalFormat("###,###,###,###,###,###,##0.00");
        return format.format(money);
    }

    //转为千分位，并带2位小数点
    public static String formatMoneyThousand(double money) {
        //return DecimalFormat.getNumberInstance().format(money);
        DecimalFormat format = new DecimalFormat("###,###,###,###,###,###,##0.00");
        return format.format(money);
    }

    public static String formatMoneyThousand(BigDecimal money) {
        //return DecimalFormat.getNumberInstance().format(money);
        DecimalFormat format = new DecimalFormat("###,###,###,###,###,###,##0.00");
        return format.format(money);
    }

    public static String formatMoney(float money) {
        //return DecimalFormat.getNumberInstance().format(money);
        NumberFormat format = new DecimalFormat("#0.00");
        return format.format(money);
    }

    public static String formatMoney(double money) {
        //return DecimalFormat.getNumberInstance().format(money);
        NumberFormat format = new DecimalFormat("#0.00");
        return format.format(money);
    }

    public static String formatMoneyBlock(double money) {
        NumberFormat format = new DecimalFormat("#0.00");
        format.setRoundingMode(RoundingMode.DOWN);
        return format.format(money);
    }

    public static String formatMoney(BigDecimal money) {
        DecimalFormat format = new DecimalFormat("#0.00");
        return format.format(money);
    }
    public static String formatNumber(double number) {
        DecimalFormat format = new DecimalFormat("#0.0000");
        return format.format(number);
    }


    //    public static String formatMoney(float money){
    //        return formatMoney(money, 2);
    //    }
    //
    public static String formatMoney(float money, int point) {
        return String.format("%." + point + "f", money);
    }

    public static int stringToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public static boolean onlyZCK(String text){
        Matcher m=Pattern.compile("[A-Za-z\\u4e00-\\u9fa5]*$").matcher(text);
        return m.matches();
    }
    public static boolean onlyZSY(String text){
        Matcher m=Pattern.compile("[A-Za-z0-9\\-_]+$").matcher(text);
        return m.matches();
    }
    public static boolean onlyZSCYK(String text){
        Matcher m=Pattern.compile("[A-Za-z0-9\\u4e00-\\u9fa5\\-_]*$").matcher(text);
        return m.matches();
    }
    public static boolean onlyZC(String text){
        Matcher m=Pattern.compile("[A-Za-z\\u4e00-\\u9fa5]+$").matcher(text);
        return m.matches();
    }
    public static boolean onlyZSC(String text){
        Matcher m=Pattern.compile("[A-Za-z0-9\\u4e00-\\u9fa5]+$").matcher(text);
        return m.matches();
    }
    public static boolean onlyZS(String text){
        Matcher m=Pattern.compile("[A-Za-z0-9]+$").matcher(text);
        return m.matches();
    }

    public static String formatSixNumber(double number) {
        DecimalFormat format = new DecimalFormat("#0.000000");
        return format.format(number);
    }

    public static String formatEightNumber(double number) {
        DecimalFormat format = new DecimalFormat("#0.00000000");
        return format.format(number);
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */


    public static boolean isInteger(double d) {
        return d % 1 ==0;
    }

    public static String formatDate(String lastTime) {
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat f2 = new SimpleDateFormat("MM-dd HH:mm");
        try {
            Date date = f.parse(lastTime);
            return f2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatPlain(double d) {
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(4);//这里是小数位
        return df.format(d);
    }

    public static String formatPhone(String account) {
        if(account.length() == 11 && !account.contains("@")) {
            return account.substring(0,3) + "****" + account.substring(7);
        } else if(account.contains("@")) {
            return account.substring(0,1) + "***" + account.substring(account.indexOf("@") - 1);
        } else {
            return "****";
        }
    }
}
