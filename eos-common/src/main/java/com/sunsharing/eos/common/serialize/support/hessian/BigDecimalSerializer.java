/**
 * @(#)BigDecimalSerializer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-27 下午12:02
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.serialize.support.hessian;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

import java.io.IOException;
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
public class BigDecimalSerializer extends AbstractSerializer {
    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {

        if (obj == null)
            out.writeNull();
        else {
            Class cl = obj.getClass();

            if (out.addRef(obj))
                return;

            int ref = out.writeObjectBegin(cl.getName());

            BigDecimal bi = (BigDecimal) obj;

            if (ref < -1) {
                out.writeString("value");
                out.writeString(bi.toString());
                out.writeMapEnd();
            } else {
                if (ref == -1) {
                    out.writeInt(1);
                    out.writeString("value");
                    out.writeObjectBegin(cl.getName());
                }

                out.writeString(bi.toString());
            }
        }
    }
}

