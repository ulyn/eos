/**
 * @(#)Invocation
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-22 下午9:34
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc;

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
public interface Invocation {

    //迁移到协议头，此处不再浪费数据空间
//    String getId();

    /**
     * 获取方法名
     *
     * @return
     */
    String getMethodName();

    /**
     * 获取方法入参类型数组，使用simpleName
     *
     * @return
     */
    String[] getParameterTypes();

    /**
     * 获取参数值
     *
     * @return
     */
    Object[] getArguments();

    /**
     * 获取模拟的指定类型
     *
     * @return
     */
    String getMock();
}