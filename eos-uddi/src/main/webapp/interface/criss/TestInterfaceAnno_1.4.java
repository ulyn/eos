/**
 * @(#)TestInterfaceAnno
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-30 下午3:35
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */

import com.sunsharing.eos.common.annotation.EosService;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> EosService注解，id不填取默认值，同Spring。version必填，appId由上传接口时程序自动填充
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
@EosService(version="1.4",appId="criss",id="testInterfaceAnno")
public interface TestInterfaceAnno {
    /**
     * 输出
     * @param name 入参
     * @return
     *
     * [success]当入参name="criss"为成功输出
     * {"success":"成功了"}
     *
     * [error]当入参为其他时为错误输出
     * {"error":"错误了"}
     */
    String sayHello(String name);

    /**
     * 输出
     * @param name 入参
     * @return [success]当入参name="criss"为成功输出
     * {"success":"成功了2",
     * "haha":"haha2"}
     *
     * [error]当入参为其他时为错误输出
     * {"error":"错误了2"}
     */
    String sayHello2(String name);
}

