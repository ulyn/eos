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
package com.sunsharing.eos.server.test;

import com.sunsharing.eos.common.annotation.EosService;

import java.util.List;
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
@EosService(version = "1.4")
public interface TestService {
    /**
     * 输出
     *
     * @param name
     * @return ${success}当入参name="criss"为成功输出
     *         成功了2
     *         ${error}当入参为其他时为错误输出
     *         错误了2
     */
    String sayHello(String name);

    /**
     * 取得num条List
     *
     * @param num
     * @return ${success}当入参name="criss"为成功输出
     *         [{"success":"成功了2",
     *         "haha":"haha2"}]
     *         ${error}当入参为其他时为错误输出
     *         [{"error":"错误了2"}]
     */
    List getList(int num);

    /**
     * 测试没有返回值的情况
     */
    void testVoid();

    /**
     * 获取map
     *
     * @return ${张三}当名字为张三的时候
     *         {"name":"张三","age":40,"sex":"男"}
     *         ${李四}当名字为李四的时候
     *         {"name":"李四","age":20,"sex":"男"}
     */
    Map getMap();

    /**
     * 测试入参为map的情况
     *
     * @param paramMap
     * @return ${success}当业务执行成功时候
     *         {"name":"success","test1":40,"test2":"a"}
     *         ${error}当业务执行失败时候
     *         {"name":"error","test1":20,"test2":"b"}
     */
    Map testMapParam(Map paramMap);

    /**
     * 测试入参为List的情况
     *
     * @param paramList
     * @return ${success}当业务执行成功时候
     *         [{"name":"success1","test1":40,"test2":"a"},{"name":"success2","test1":40,"test2":"a"}]
     *         ${error}当业务执行失败时候
     *         [{"name":"error1","test1":20,"test2":"b"},{"name":"error2","test1":20,"test2":"b"}]
     */
    List testListParam(List paramList);
}