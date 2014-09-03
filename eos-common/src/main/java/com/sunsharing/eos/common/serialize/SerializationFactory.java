/**
 * @(#)SerializationFactory
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午11:02
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.serialize;

import com.sunsharing.eos.common.serialize.support.hessian.Hessian2Serialization;
import com.sunsharing.eos.common.serialize.support.java.JavaSerialization;
import com.sunsharing.eos.common.serialize.support.json.FastJsonSerialization;

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
public class SerializationFactory {

    public static Serialization createSerialization(String serialization) {
        if ("hessian".equalsIgnoreCase(serialization)) {
            return new Hessian2Serialization();
        } else if ("java".equalsIgnoreCase(serialization)) {
            return new JavaSerialization();
        } else if ("fastjson".equalsIgnoreCase(serialization)) {
            return new FastJsonSerialization();
        } else {
            throw new RuntimeException("指定的序列化方式找不到实现类：serialization=" + serialization);
        }
    }
}

