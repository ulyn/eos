/*
 * @(#) ZentaoResultAdapter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-18 09:23:06
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.eos;

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
public interface EosResultAdapter {

    public final EosResultAdapter DEFAULT = new EosResultAdapter() {
        @Override
        public EosResult from(String eosRes, Class cls) {
            if (eosRes.trim().startsWith("<html>")) {
                return new EosResult(false, "调用服务返回:" + eosRes, null);
            }
            return EosResult.from(eosRes, cls);
        }
    };

    <T> EosResult<T> from(String eosRes, Class<T> cls);

}

