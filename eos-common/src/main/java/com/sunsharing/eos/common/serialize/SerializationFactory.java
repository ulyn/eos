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

import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.common.serialize.support.hessian.Hessian2Serialization;
import com.sunsharing.eos.common.serialize.support.java.JavaSerialization;
import com.sunsharing.eos.common.serialize.support.json.FastJsonSerialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public static byte[] serializeToBytes(Object o, String serialization) throws IOException {
        Serialization serial = SerializationFactory.createSerialization(serialization);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serial.serialize(outputStream);
        objectOutput.writeObject(o);
        objectOutput.flushBuffer();
        return outputStream.toByteArray();
    }

    public static String serializeToBase64Str(Object o, String serialization) throws IOException {
        byte[] bytes = serializeToBytes(o, serialization);
        return Base64.encode(bytes);
    }

    public static <T> T deserializeBytes(byte[] bytes, Class<T> cls, String serialization) throws IOException, ClassNotFoundException {
        Serialization serial = SerializationFactory.createSerialization(serialization);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serial.deserialize(inputStream);
        return objectInput.readObject(cls);
    }

    public static <T> T deserializeBase64Str(String base64Str, Class<T> cls, String serialization) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.decode(base64Str);
        return deserializeBytes(bytes, cls, serialization);
    }
}

