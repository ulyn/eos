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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.common.rpc.RpcException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * 判断一个类是否为基本数据类型。
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    public static boolean isBaseDataType(Class clazz)
    {
        return  (
                clazz.equals(String.class) ||
                        clazz.equals(Integer.class)||
                        clazz.equals(Byte.class) ||
                        clazz.equals(Long.class) ||
                        clazz.equals(Double.class) ||
                        clazz.equals(Float.class) ||
                        clazz.equals(Character.class) ||
                        clazz.equals(Short.class) ||
                        clazz.equals(BigDecimal.class) ||
                        clazz.equals(BigInteger.class) ||
                        clazz.equals(Boolean.class) ||
                        clazz.equals(Date.class) ||
//                                clazz.equals(DateTime.class) ||
                        clazz.isPrimitive()
        );
    }

    /**
     * 类型转换，将String类型转换为指定类型，当类型为复杂对象时，value为json串
     *
     * @param value
     * @param type
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object compatibleTypeConvert(String value, Class<?> type) {
        if (value == null || type == null || String.class.equals(type)) {
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
        } else if (type instanceof Class) {
            try {
                return JSONObject.parseObject(value, type);
            } catch (Exception e) {
                throw new RuntimeException("类型转换出错，请确保value值是json格式：" + value, e);
            }
        }
        return value;
    }

    public static <T> T expectConvert(Object value,Class<T> type){
        return (T)ConvertUtils.convert(value,type);
    }

    /**
     * 将对象转换为字符串
     *
     * @param o
     * @return
     */
    public static String toMyString(Object o) {
        if(o == null){
            return null;
        }else if(isBaseDataType(o.getClass())){
            return o.toString();
        }else {
            return JSONObject.toJSONString(o
                    ,SerializerFeature.WriteMapNullValue
                    ,SerializerFeature.WriteClassName);
        }
    }


    /**
     * 尝试转换字符串为JSON对象
     * 对于前端来讲，字符串返回的都尝试转换下JSON
     * @param value
     * @return
     */
    public static Object tryConvertStrToObject(Object value) {
        if(value instanceof String){
            try{
                Object result = JSON.parse((String)value);
                if(!JSON.class.isInstance(result)){
                    return value;
                }else{
                    return result;
                }
            }catch (Exception e){
                //转换失败就直接返回
                return value;
            }
        }
        return value;
    }

    public static void main(String[] args) {
        Object value = 1;
        System.out.println(expectConvert(value,int.class));
        System.out.println(expectConvert(value,Integer.class));
        System.out.println(expectConvert(value,String.class));
        System.out.println(expectConvert(value,void.class));
        System.out.println(expectConvert(value,Boolean.class));

        System.out.println(tryConvertStrToObject("[]").getClass());
        System.out.println(tryConvertStrToObject("{}").getClass());
        System.out.println(tryConvertStrToObject("123").getClass());
        System.out.println(tryConvertStrToObject("{123}").getClass());
    }

}

