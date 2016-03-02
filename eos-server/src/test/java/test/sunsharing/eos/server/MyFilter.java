/**
 * @(#)MyFilter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2016
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 16-3-2 下午3:56
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package test.sunsharing.eos.server;

import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.RpcException;

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
public class MyFilter extends AbstractServiceFilter {
    @Override
    protected void doFilter(ServiceRequest req, ServiceResponse res, FilterChain filterChain) throws ServiceFilterException, RpcException {
        System.out.println("MyFilter:" + req.getServiceId());
    }
}

