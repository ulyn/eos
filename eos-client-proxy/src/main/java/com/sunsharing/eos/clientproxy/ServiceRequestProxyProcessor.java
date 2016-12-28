/**
 * @(#)ServiceRequestProxyProcessor
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-12-28 上午11:58
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.clientproxy;

import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.common.utils.VersionUtil;

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
public class ServiceRequestProxyProcessor {

    public static String invoke(String serviceReqBase64Str){
        if("test".equals(serviceReqBase64Str)){
            // 针对测试，返回
            return "success eos" + VersionUtil.getVersion();
        }
        return DynamicRpc.invoke(serviceReqBase64Str);
    }

}

