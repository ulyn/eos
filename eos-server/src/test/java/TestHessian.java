/**
 * @(#)TestHessian
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-17 下午2:53
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.SerializationFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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
public class TestHessian {
//    public static void main(String[] args) throws Exception {
//        RpcParams invocation = new RpcParams();
//        invocation.setArguments(new Object[]{});
//        invocation.setMethodName("sayHello");
//        invocation.setMock("");
////        invocation.setParameterTypes(new Class[]{String.class});
////        invocation.setParameterTypes(null);
//
//        System.out.println(getSerializationBytes(invocation).length);
//
//    }
//
//    protected static byte[] getSerializationBytes(Object o) throws Exception {
//        Serialization serial = SerializationFactory.createSerialization(Constants.DEFAULT_SERIALIZATION);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ObjectOutput objectOutput = serial.serialize(outputStream);
//        objectOutput.writeObject(o);
//        objectOutput.flushBuffer();
//        return outputStream.toByteArray();
//    }
//
//    protected static <T> T serializationBytesToObject(byte[] bytes, Class<T> cls) throws Exception {
//        Serialization serial = SerializationFactory.createSerialization(Constants.DEFAULT_SERIALIZATION);
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//        ObjectInput objectInput = serial.deserialize(inputStream);
//        return objectInput.readObject(cls);
//    }
}

