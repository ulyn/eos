/**
 * @(#)CompatibleTypeUtils
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-10 下午1:12
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.utils;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class CompatibleTypeUtils {
    private CompatibleTypeUtils() {
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 类型转换，将String类型转换为指定类型，当类型为复杂对象时，value为json串
     *
     * @param value
     * @param type
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object compatibleTypeConvert(String value, Class<?> type) {
        if (value == null || type == null) {
            return value;
        }
        if (char.class.equals(type) || Character.class.equals(type)) {
            if (value.length() != 1) {
                throw new IllegalArgumentException(String.format("CAN NOT convert String(%s) to char!" +
                        " when convert String to char, the String MUST only 1 char.", value));
            }
            return value.charAt(0);
        } else if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, value);
        } else if (type == BigInteger.class) {
            return new BigInteger(value);
        } else if (type == BigDecimal.class) {
            return new BigDecimal(value);
        } else if (type == Short.class || type == short.class) {
            return new Short(value);
        } else if (type == Integer.class || type == int.class) {
            return new Integer(value);
        } else if (type == Long.class || type == long.class) {
            return new Long(value);
        } else if (type == Double.class || type == double.class) {
            return new Double(value);
        } else if (type == Float.class || type == float.class) {
            return new Float(value);
        } else if (type == Byte.class || type == byte.class) {
            return new Byte(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return new Boolean(value);
        } else if (type == Date.class) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(value);
            } catch (ParseException e) {
                throw new IllegalStateException("Failed to parse date " + value + " by format " + DATE_FORMAT + ", cause: " + e.getMessage(), e);
            }
        } else if (type == Class.class) {
            try {
                return JSONObject.parseObject(value, type);
            } catch (Exception e) {
                throw new RuntimeException("类型转换出错，请确保value值是json格式：" + value, e);
            }
        }
        return value;
    }

    /**
     * 将对象转换为字符串
     *
     * @param o
     * @return
     */
    public static String objectToString(Object o) {
        String value = "";
        Class type = o.getClass();
        if (type == Class.class) {
            value = JSONObject.toJSONString(o);
        } else {
            value = o.toString();
        }
        return value;
    }
}

