package cn.lianshi.library.mvvmbase.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.HashSet;
import java.util.Set;

public class PinYinUtils {

    //pinyin4j格式类
    private HanyuPinyinOutputFormat format = null;
    //拼音字符串数组
    private String[] pinyin;


    //通过构造方法进行初始化
    public PinYinUtils(){

        format = new HanyuPinyinOutputFormat();
        /*
         * 设置需要转换的拼音格式
         * 以天为例
         * HanyuPinyinToneType.WITHOUT_TONE 转换为tian
         * HanyuPinyinToneType.WITH_TONE_MARK 转换为tian1
         * HanyuPinyinVCharType.WITH_U_UNICODE 转换为tiān
         *
         */
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pinyin = null;
    }


    /**
	 * 讲一个包含汉字的字符串转成拼音
	 * @param string 包含汉字的字符串
	 * @return 拼音
	 */
	public static String getPinyin(String string) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();// 控制输出格式
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 去除音调
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE); // 字母大写
		StringBuilder sb = new StringBuilder();
		// 得到字符数组
		char[] charArray = string.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			// 跳过空格
			if(Character.isWhitespace(c)){
				continue;
			}
			// 字母数字特殊字符不需要转换, 直接拼接
			if(c >= -128 && c < 127){
				sb.append(c);
			}else {
				// 可能是汉字
				try {
					String s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
					sb.append(s);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 字符串集合转换字符串(逗号分隔)
	 * @author wyh
	 * @param stringSet
	 * @return
	 */
	public static String makeStringByStringSet(Set<String> stringSet){
		StringBuilder str = new StringBuilder();
		int i=0;
		for(String s : stringSet){
			if(i == stringSet.size() - 1){
				str.append(s);
			}else{
				str.append(s + ",");
			}
			i++;
		}
		return str.toString().toUpperCase();
	}

	/**
	 * 获取拼音集合
	 * @author wyh
	 * @param src
	 * @return Set<String>
	 */
	public static Set<String> getPinyinToSet(String src){
		if(src!=null && !src.trim().equalsIgnoreCase("")){
			char[] srcChar ;
			srcChar=src.toCharArray();
			//汉语拼音格式输出类
			HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();

			//输出设置，大小写，音标方式等
			hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
			hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

			String[][] temp = new String[src.length()][];
			for(int i=0;i<srcChar.length;i++){
				char c = srcChar[i];
				//是中文或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
				if(String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")){
					try{
						temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hanYuPinOutputFormat);
					}catch(BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				}else if(((int)c>=65 && (int)c<=90) || ((int)c>=97 && (int)c<=122)){
					temp[i] = new String[]{String.valueOf(srcChar[i])};
				}else{
					temp[i] = new String[]{""};
				}
			}
			String[] pingyinArray = Exchange(temp);
			Set<String> pinyinSet = new HashSet<>();
			for(int i=0;i<pingyinArray.length;i++){
				pinyinSet.add(pingyinArray[i]);
			}
			return pinyinSet;
		}
		return null;
	}

	/**
	 * 递归
	 * @author wyh
	 * @param strJaggedArray
	 * @return
	 */
	public static String[] Exchange(String[][] strJaggedArray){
		String[][] temp = DoExchange(strJaggedArray);
		return temp[0];
	}

	/**
	 * 递归
	 * @author wyh
	 * @param strJaggedArray
	 * @return
	 */
	private static String[][] DoExchange(String[][] strJaggedArray){
		int len = strJaggedArray.length;
		if(len >= 2){
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1*len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for(int i=0;i<len1;i++){
				for(int j=0;j<len2;j++){
					temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
					Index ++;
				}
			}
			String[][] newArray = new String[len-1][];
			for(int i=2;i<len;i++){
				newArray[i-1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		}else{
			return strJaggedArray;
		}
	}




    /**
     * 对单个字进行转换
     * @param pinYinStr 需转换的汉字字符串
     * @return 拼音字符串数组
     */
    public String getCharPinYin(char pinYinStr){

        try
        {
            //执行转换
            pinyin = PinyinHelper.toHanyuPinyinStringArray(pinYinStr, format);

        } catch (BadHanyuPinyinOutputFormatCombination e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //pinyin4j规则，当转换的符串不是汉字，就返回null
        if(pinyin == null){
            return null;
        }

        //多音字会返回一个多音字拼音的数组，pinyiin4j并不能有效判断该字的读音
        return pinyin[0];
    }


    /**
     * 对单个字进行转换
     * @param pinYinStr
     * @return
     */
    public String getStringPinYin(String pinYinStr){
        StringBuffer sb = new StringBuffer();
        String tempStr = null;
        //循环字符串
        for(int i = 0; i<pinYinStr.length(); i++)
        {

            tempStr = this.getCharPinYin(pinYinStr.charAt(i));
            if(tempStr == null)
            {
                //非汉字直接拼接
                sb.append(pinYinStr.charAt(i));
            }
            else
            {
                sb.append(tempStr);
            }
        }

        return sb.toString();

    }



}
