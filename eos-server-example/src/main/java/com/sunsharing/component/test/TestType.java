package com.sunsharing.component.test;

import com.sunsharing.eos.common.annotation.EosService;

import java.util.List;
import java.util.Map;

/**
 * EOS测试类型
 */
@EosService(version="1.3")
public interface TestType {
    /**
     * 测试Int类型
     * @param i 随便
     * @return
     * ${success} 如果i=1成功返回
     * 100
     * ${error} 如果i==-1返回-1
     * -1
     */
    int testInt(int i);

    /**
     * 测试Double类型
     * @param d 随便
     * @return
     * ${success} 如果d=1成功返回
     * 101.0
     * ${error} 如果d=-1异常返回
     * -1.0
     */
    double testDouble(double d);

    /**
     * 测试Float类型
     * @param f 随便
     * @return
     * ${success} 如果f=1成功返回
     * 1.0
     * ${error} 如果f=-1异常返回
     * -1.0
     */
    float testFloat(float f);

    /**
     * 测试Float类型
     * @param s
     * @return
     * ${success} 如果s="1"成功返回
     * 1
     * ${error} 如果s="-1"异常返
     * -1
     *
     */
    String testString(String s,String sw);

    /**
     * 测试Map类型
     * @param m
     * @return
     * ${success} 入参包含test参数
     * {"test1":"test2","test2":"test3"}
     * ${error} 入参不包含test参数
     * {"error":"error1","error1":"error2"}
     */
    Map testMap(
            Map m,
            String l2
    );

    /**
     * 测试list类型
     * @param list
     * @return
     *
     * ${success} 所有都返回
     * [{"test":"test1","test2":"test3","test3":"test4"}]
     */
    List testListMap(List list);

    /**
     * 测试void
     * @param name 没有入参
     */
    void testVoid(String name);
}
