/**
 * @(#)BigDecimalSerializerFactory
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-27 上午11:39
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.serialize.support.hessian;

import com.caucho.hessian.io.*;

import java.math.BigDecimal;

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
public class BigDecimalSerializerFactory extends AbstractSerializerFactory {

    private BigDecimalSerializer bigDecimalSerializer = new BigDecimalSerializer();
    private BigDecimalDeserializer bigDecimalDeserializer = new BigDecimalDeserializer();

    @Override
    public Serializer getSerializer(Class aClass) throws HessianProtocolException {
        if (BigDecimal.class.isAssignableFrom(aClass)) {
            return bigDecimalSerializer;
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class aClass) throws HessianProtocolException {
        if (BigDecimal.class.isAssignableFrom(aClass)) {
            return bigDecimalDeserializer;
        }
        return null;
    }


}

