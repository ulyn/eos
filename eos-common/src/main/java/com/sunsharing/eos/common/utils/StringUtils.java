package com.sunsharing.eos.common.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * StringUtils
 */

public final class StringUtils {

    private static final Logger logger = Logger.getLogger(StringUtils.class);

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)"); //key value pair pattern.

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    /**
     * is empty string.
     *
     * @param str source string.
     * @return is empty.
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }


    /**
     * is not empty string.
     *
     * @param str source string.
     * @return is not empty.
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * @param s1
     * @param s2
     * @return equals
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 == null && s2 == null)
            return true;
        if (s1 == null || s2 == null)
            return false;
        return s1.equals(s2);
    }

    /**
     * is integer string.
     *
     * @param str
     * @return is integer
     */
    public static boolean isInteger(String str) {
        if (str == null || str.length() == 0)
            return false;
        return INT_PATTERN.matcher(str).matches();
    }

    public static int parseInteger(String str) {
        if (!isInteger(str))
            return 0;
        return Integer.parseInt(str);
    }

    /**
     * Returns true if s is a legal Java identifier.<p>
     * <a href="http://www.exampledepot.com/egs/java.lang/IsJavaId.html">more info.</a>
     */
    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * translat.
     *
     * @param src  source string.
     * @param from src char table.
     * @param to   target char table.
     * @return String.
     */
    public static String translat(String src, String from, String to) {
        if (isEmpty(src)) return src;
        StringBuilder sb = null;
        int ix;
        char c;
        for (int i = 0, len = src.length(); i < len; i++) {
            c = src.charAt(i);
            ix = from.indexOf(c);
            if (ix == -1) {
                if (sb != null)
                    sb.append(c);
            } else {
                if (sb == null) {
                    sb = new StringBuilder(len);
                    sb.append(src, 0, i);
                }
                if (ix < to.length())
                    sb.append(to.charAt(ix));
            }
        }
        return sb == null ? src : sb.toString();
    }

    /**
     * split.
     *
     * @param ch char.
     * @return string array.
     */
    public static String[] split(String str, char ch) {
        List<String> list = null;
        char c;
        int ix = 0, len = str.length();
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (c == ch) {
                if (list == null)
                    list = new ArrayList<String>();
                list.add(str.substring(ix, i));
                ix = i + 1;
            }
        }
        if (ix > 0)
            list.add(str.substring(ix));
        return list == null ? EMPTY_STRING_ARRAY : (String[]) list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * join string.
     *
     * @param array String array.
     * @return String.
     */
    public static String join(String[] array) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : array)
            sb.append(s);
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, char split) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array String array.
     * @param split split
     * @return String.
     */
    public static String join(String[] array, String split) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(split);
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String join(Collection<String> coll, String split) {
        if (coll.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : coll) {
            if (isFirst) isFirst = false;
            else sb.append(split);
            sb.append(s);
        }
        return sb.toString();
    }

    public static String genUUID() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }

    public static String getClassPath() throws Exception {

        String keyfilePath = URLDecoder.decode(StringUtils.class.getProtectionDomain().
                getCodeSource().getLocation().getFile(), "UTF-8");
        keyfilePath = keyfilePath.replaceAll("\\\\", "/");
        File temp = new File(keyfilePath);
        if (temp.isFile() && keyfilePath.endsWith("jar") == true) {
            keyfilePath = keyfilePath.substring(0, keyfilePath.lastIndexOf("/")) + "/";
        } else if (keyfilePath.indexOf("classes") != -1) {
            keyfilePath = keyfilePath.substring(0, keyfilePath.indexOf("classes") + 7) + "/";
        }
        return keyfilePath;
    }

    public static String transXMLCDADA(String input) {
        if (input.indexOf("&") != -1
                || input.indexOf("<") != -1
                || input.indexOf(">") != -1
                || input.indexOf("\"") != -1
                || input.indexOf("'") != -1
                ) {
            return "<![CDATA[" + input + "]]>";
        } else {
            return input;
        }
    }

    public static String getCurrentDateHour() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        return format.format(d);
    }

    public static String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(d);
    }

    public static String getCurrentDisTime() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(d);
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }


    /**
     * 通过byte数组取到int
     *
     * @param bb 第几位开始
     * @return
     */
    public static int getInt(byte[] bb) {
        return (int) ((((bb[0] & 0xff) << 24)
                | ((bb[1] & 0xff) << 16)
                | ((bb[2] & 0xff) << 8) | ((bb[3] & 0xff) << 0)));
    }

    /**
     * 转换long型为byte数组
     *
     * @param x
     */
    public static byte[] longToBytes(long x) {
        byte[] bb = new byte[8];
        bb[0] = (byte) (x >> 56);
        bb[1] = (byte) (x >> 48);
        bb[2] = (byte) (x >> 40);
        bb[3] = (byte) (x >> 32);
        bb[4] = (byte) (x >> 24);
        bb[5] = (byte) (x >> 16);
        bb[6] = (byte) (x >> 8);
        bb[7] = (byte) (x >> 0);
        return bb;
    }

    /**
     * 通过byte数组取到long
     *
     * @param bb
     * @return
     */
    public static long bytesTolong(byte[] bb) {
        return ((((long) bb[0] & 0xff) << 56)
                | (((long) bb[1] & 0xff) << 48)
                | (((long) bb[2] & 0xff) << 40)
                | (((long) bb[3] & 0xff) << 32)
                | (((long) bb[4] & 0xff) << 24)
                | (((long) bb[5] & 0xff) << 16)
                | (((long) bb[6] & 0xff) << 8) | (((long) bb[7] & 0xff) << 0));
    }

    /**
     * 转换int为byte数组
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putInt(byte[] bb, int x, int index) {
        bb[index] = (byte) (x >>> 24);
        bb[index + 1] = (byte) (x >>> 16);
        bb[index + 2] = (byte) (x >>> 8);
        bb[index + 3] = (byte) x;
    }

    /**
     * 通过byte数组取到int
     *
     * @param bb
     * @param index 第几位开始
     * @return
     */
    public static int getInt(byte[] bb, int index) {
        return (bb[index] & 0xff) << 24 |
                (bb[index + 1] & 0xff) << 16 |
                (bb[index + 2] & 0xff) << 8 |
                bb[index + 3] & 0xff;
    }

    /**
     * 转换long型为byte数组
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putLong(byte[] bb, long x, int index) {
        for (int i = 0; i < 8; i++) {
            bb[index + i] = (byte) (x >>> (56 - (i * 8)));
        }
    }

    /**
     * 通过byte数组取到long
     *
     * @param bb
     * @param index
     * @return
     */
    public static long getLong(byte[] bb, int index) {
        return ((long) bb[index] & 0xff) << 56 |
                ((long) bb[index + 1] & 0xff) << 48 |
                ((long) bb[index + 2] & 0xff) << 40 |
                ((long) bb[index + 3] & 0xff) << 32 |
                ((long) bb[index + 4] & 0xff) << 24 |
                ((long) bb[index + 5] & 0xff) << 16 |
                ((long) bb[index + 6] & 0xff) << 8 |
                (long) bb[index + 7] & 0xff;
    }

    /**
     * 将String写入byte数组
     *
     * @param bb
     * @param str
     * @param index
     */
    public static void putString(byte[] bb, String str, int index) {
        if (str != null) {
            try {
                byte[] cc = str.getBytes("UTF-8");
                int maxLen = cc.length;
                if (maxLen > bb.length - index) {
                    maxLen = bb.length - index;
                }
                for (int i = 0; i < maxLen; i++) {
                    bb[i + index] = cc[i];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取byte数组下长度的字符串值trim值
     *
     * @param bb
     * @param len
     * @param index
     * @return
     */
    public static String getString(byte[] bb, int len, int index) {
        try {
            byte[] bytes = new byte[len];
            if (len > bb.length - index) {
                len = bb.length - index;
            }
            for (int i = 0; i < len; i++) {
                bytes[i] = bb[i + index];
            }
            return new String(bytes, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String transToDbDate(long datetime) {
        Date d = new Date(datetime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmms");
        String dd = sdf.format(d);
        return dd;
    }

    private StringUtils() {
    }
}