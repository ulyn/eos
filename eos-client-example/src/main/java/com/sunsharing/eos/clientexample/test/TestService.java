package com.sunsharing.eos.clientexample.test;

import com.sunsharing.eos.common.annotation.EosService;
import com.sunsharing.eos.common.annotation.ParameterNames;

import java.util.List;
import java.util.Map;

/**
 * @(#)TestService
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午8:13
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */

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
@EosService(version = "1.4", appId = "test", id = "testService")
public interface TestService {

    /**
     * 测试Int类型
     *
     * @param i 随便
     * @return ${success} 如果i=1成功返回
     * 100
     * ${error} 如果i==-1返回-1
     * -1
     */
    @ParameterNames(value = {"i"})
    int testInt(int i);

    /**
     * 测试Double类型
     *
     * @param d 随便
     * @return ${success} 如果d=1成功返回
     * 101.0
     * ${error} 如果d=-1异常返回
     * -1.0
     */
    @ParameterNames(value = {"d"})
    double testDouble(double d);

    /**
     * 测试Float类型
     *
     * @param f 随便
     * @return ${success} 如果f=1成功返回
     * 1.0
     * ${error} 如果f=-1异常返回
     * -1.0
     */
    @ParameterNames(value = {"f"})
    float testFloat(float f);

    /**
     * 测试Float类型
     *
     * @param s
     * @return ${success} 如果s="1"成功返回
     * 1
     * ${error} 如果s="-1"异常返
     * -1
     */
    @ParameterNames(value = {"s", "sw"})
    String testString(String s, String sw);

    /**
     * 测试Map类型
     *
     * @param m
     * @return ${success} 入参包含test参数
     * {"test1":"test2","test2":"test3"}
     * ${error} 入参不包含test参数
     * {"error":"error1","error1":"error2"}
     */
    @ParameterNames(value = {"m", "l2"})
    Map testMap(
            Map m,
            String l2
    );

    /**
     * 测试list类型
     *
     * @param list
     * @return ${success} 所有都返回
     * [{"test":"test1","test2":"test3","test3":"test4"}]
     */
    @ParameterNames(value = {"list"})
    List testListMap(List list);

    /**
     * 测试void
     *
     * @param name 没有入参
     */
    @ParameterNames(value = {"name"})
    void testVoid(String name);
}
