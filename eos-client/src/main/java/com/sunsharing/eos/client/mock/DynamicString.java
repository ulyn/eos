package com.sunsharing.eos.client.mock;


import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <strong>可替换参数变量的字符串类。</strong>参数的形式为：${参数}<p/>
 * 例如：字符串 "This is a ${SomeThing}." 可替换的部分是：${SomeThing}。其中，SomeThing即为参数。<br/>
 * 如果要将${SomeThing}替换为字符串"Demo"（也就是说替换后的字符串是："This is a Demo."），
 * 我们可以将SomeThing和Demo组成键值对放入Map中进行转换。<p/>
 * 代码示例：<br/>
 * <pre>{@code
 *   String demoStr = "This is a ${SomeThing}.";
 *   DynamicString ds = new DynamicString(demoStr);
 *   Map<String,String> param = new java.util.HashMap<String,String>(4);
 *   param.put("SomeThing", "Demo");
 *   System.out.println(ds.convert(param));
 * }</pre>
 * <strong>注意：反斜杠'/'是转义字符，活用可处理常见问题。</strong>
 * <P>该类创建于 2010-3-3 上午01:06:26
 * @version 1.0.0
 * @author 侯磊
 */
public class DynamicString {
    static org.apache.log4j.Logger logger = Logger.getLogger(DynamicString.class);

    private String dynStr;
    private DynamicPattern header;
    private DynamicPattern tail;

    static abstract class DynamicPattern {
        DynamicPattern next;
        public abstract Object subConvert(Map<String,?> pattern);
    }

    static class StringPattern extends DynamicPattern {
        private String pattern;

        public StringPattern(StringBuilder buff, int start, int end) {
            this.pattern = buff.substring(start, end);
        }
        @Override
        public Object subConvert(Map<String,? extends Object> map) {
            return pattern;
        }
    }

    static class MappedPattern extends DynamicPattern {
        private String key;

        public MappedPattern(StringBuilder buff, int start, int end) {
            this.key = buff.substring(start, end);
        }
        @Override
        public Object subConvert(Map<String,? extends Object> param) {
//            if(param.get(key)==null){
//                logger.warn("所传入的Map中，不含有参数："+key);
//                return "";
//            }
            String o = getKey(key,param);
            return o;
        }

        public String getKey(String key,Object obj)
        {
            String[] keys = key.split("\\.");
            Object objValue = obj;
            for(int i=0;i<keys.length;i++)
            {
                String subKey = keys[i];
                objValue = getSubKey(subKey,objValue);
            }
            if(objValue==null)
            {
                return "";
            }
            return objValue.toString();
        }
        public Object getSubKey(String subKey,Object obj)
        {
            if(obj==null)
            {
                return null;
            }
            //处理Map
            if(obj instanceof Map)
            {
                return ((Map) obj).get(subKey);
            }
            //处理JSON格式
            if(obj instanceof String)
            {
                String str = (String)obj;
                if(!StringUtils.isBlank(str))
                {
                    if(str.startsWith("{") && str.endsWith("}"))
                    {
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(str);
                            Object objValue = jsonObject.get(subKey);
                            if (objValue == null) {
                                return null;
                            } else {
                                return objValue.toString();
                            }
                        }catch (Exception e)
                        {
                            logger.error(str+"",e);
                            return null;
                        }
                    }else
                    {
                        return null;
                    }
                }else
                {
                    return null;
                }
            }
            //处理一般的对象
            Class userCla = (Class) obj.getClass();

       /*
        * 得到类中的所有属性集合
        */
            Field[] fs = userCla.getDeclaredFields();
            for(int i = 0 ; i < fs.length; i++){
                Field f = fs[i];
                f.setAccessible(true); //设置些属性是可以访问的
                if(f.getName().equals(subKey)) {
                    try {
                        Object val = f.get(obj);//得到此属性的值
                        return val;
                    }catch (Exception e)
                    {
                        logger.error("获取属性值报错",e);
                        return null;
                    }
                }
            }
            return null;
        }


    }

    /**可替换参数变量的字符串类。该类适用于多个参数的情况。
     * @param dynStr 模板字符串。
     */
    public DynamicString(String dynStr) {
        this.dynStr = dynStr;
        init();
    }

    /**
     * 生成中间模板，转义字符也要考虑在内。
     */
    private void init() {
        header = tail = null;
        StringBuilder buff = new StringBuilder(dynStr);
        int start = 0, ptr = 0;
        boolean noMatching = true;
        for (ptr = start; ptr < buff.length(); ptr++) {
            if(buff.charAt(ptr)=='$' && buff.charAt(ptr+1)=='{'){
                if(ptr>0 && buff.charAt(ptr-1)=='/'){
                    buff.deleteCharAt(ptr---1);
                    if(ptr>1 && buff.charAt(ptr-1)=='/' && buff.charAt(ptr-2)=='/'){
                        buff.deleteCharAt(ptr---1);continue;
                    }
                    if(!(ptr>0 && buff.charAt(ptr-1)=='/'))
                        continue;
                }
                noMatching=false;
                StringPattern sp = new StringPattern(buff, start, ptr);
                appendPattern(sp);
                start = ptr+2;
                for (ptr += 2; ptr < buff.length(); ptr++) {
                    if (buff.charAt(ptr) == '}') {
                        if(buff.charAt(ptr-1)=='/'){
                            buff.deleteCharAt(ptr---1);
                            if(buff.charAt(ptr-1)!='/')
                                continue;
                        }
                        MappedPattern mp = new MappedPattern(buff, start, ptr);
                        appendPattern(mp);
                        noMatching=true;
                        start = ++ptr;break;
                    }
                }
            }
        }
        if (noMatching && ptr <= buff.length())
            appendPattern(new StringPattern(buff, start, ptr));
    }

    private DynamicString appendPattern(DynamicPattern pattern) {
        if (header == null) {
            header = pattern;
            tail = header;
        } else {
            tail.next = pattern;
            tail = pattern;
        }
        return this;
    }

    /**传入参数变量，得到替换后的结果。
     * @param param 将替换的参数及变量以键值对的形式存放到Map对象中。
     * @return  返回替换回的结果。
     * @exception IllegalArgumentException 当待替换的参数不在Map对象中时，抛出该异常。
     */
    public String convert(Map<String,? extends Object> param) {
        if (header == null)
            return null;
        DynamicPattern ptr = header;
        StringBuilder sb = new StringBuilder();
        while (ptr != null) {
            sb.append(ptr.subConvert(param));
            ptr = ptr.next;
        }
        return sb.toString();
    }



    /**
     * @see #convert(Map)
     */
    public String convert(String key,String value){
        Map<String,String> param = new java.util.HashMap<String,String>(2);
        param.put(key, value);
        return convert(param);
    }

    public static void main(String []a)
    {
        String demoStr = "This is a ${SomeThing.abc}.${ddc}.criss";
        Date d = new Date();
        //for(int i=0;i<1000;i++)
        DynamicString ds = new DynamicString(demoStr);
        Map keyValue = new HashMap();
        keyValue.put("abc","hexin22");
        Map<String,Object> param = new java.util.HashMap<String,Object>(4);
        param.put("SomeThing",keyValue);
        param.put("ddc",keyValue);
        String result = ds.convert(param);
        System.out.println("" + result);
    }
}
