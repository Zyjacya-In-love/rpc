package cn.krone.rpc.common.utils;

/**
 * String 工具类
 * form JavaGuide
 * @author zhanghua
 * @createTime 2022/3/6 12:58 上午
 */
public class StringUtil {

    /**
     * 深入判断字符串是否为空，是否 null length == 0，全是空格
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
