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
package com.sunsharing.eos.client.test;

import com.sunsharing.eos.common.annotation.EosService;

import java.util.List;

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
@EosService(version = "1.1", appId = "ihome")
public interface TestService {
    /**
     * 输出
     *
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

}
