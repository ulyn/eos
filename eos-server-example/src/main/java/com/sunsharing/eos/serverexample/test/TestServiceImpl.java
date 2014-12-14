package com.sunsharing.eos.serverexample.test;

import com.sunsharing.eos.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by criss on 14-2-11.
 */
@Service
public class TestServiceImpl implements TestService {


    /**
     * 测试Int类型
     *
     * @param i 随便
     * @return ${success} 如果i=1成功返回
     * 100
     * ${error} 如果i==-1返回-1
     * -1
     */
    @Override
    public int testInt(int i) {
        if (i == -1) {
            return -1;
        } else return 100;
    }

    /**
     * 测试Double类型
     *
     * @param d 随便
     * @return ${success} 如果d=1成功返回
     * 101.0
     * ${error} 如果d=-1异常返回
     * -1.0
     */
    @Override
    public double testDouble(double d) {
        if (d == -1) {
            return -1.0;
        }
        return 101.0;
    }

    /**
     * 测试Float类型
     *
     * @param f 随便
     * @return ${success} 如果f=1成功返回
     * 1.0
     * ${error} 如果f=-1异常返回
     * -1.0
     */
    @Override
    public float testFloat(float f) {
        if (f == -1) {
            return -1.0f;
        }
        return 0;
    }

    /**
     * 测试Float类型
     *
     * @param s
     * @param sw
     * @return ${success} 如果s="1"成功返回
     * 1
     * ${error} 如果s="-1"异常返
     * -1
     */
    @Override
    public String testString(String s, String sw) {
        if ("-1".equals(s)) {
            return "-1";
        }
        return "1";
    }

    /**
     * 测试Map类型
     *
     * @param m
     * @param l2
     * @return ${success} 入参包含l2参数
     * {"test1":"test2","test2":"test3"}
     * ${error} 入参不包含l2参数
     * {"error":"error1","error1":"error2"}
     */
    @Override
    public Map testMap(Map m, String l2) {
        if (StringUtils.isBlank(l2)) {
            return new HashMap() {{
                put("error", "error1");
                put("error1", "error2");
            }};
        }
        return new HashMap() {{
            put("test1", "test2");
            put("test2", "test3");
        }};
    }

    /**
     * 测试list类型
     *
     * @param list
     * @return ${success} 所有都返回
     * [{"test":"test1","test2":"test3","test3":"test4"}]
     */
    @Override
    public List testListMap(List list) {
        System.out.println("list:" + list);
        List list1 = new ArrayList();
        list1.add(new HashMap() {{
            put("test", "test1");
            put("test2", "test3");
            put("test3", "test4");
        }});
        return list1;
    }

    /**
     * 测试void
     *
     * @param name 没有入参
     */
    @Override
    public void testVoid(String name) {
        System.out.println("name：" + name);
    }
}
