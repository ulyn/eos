package com.sunsharing.eos.common.serialize.support.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;


public class JavaObjectInput extends NativeJavaObjectInput {
    public final static int MAX_BYTE_ARRAY_LENGTH = 8 * 1024 * 1024;

    public JavaObjectInput(InputStream is) throws IOException {
        super(new ObjectInputStream(is));
    }

    public JavaObjectInput(InputStream is, boolean compacted) throws IOException {
        super(compacted ? new CompactedObjectInputStream(is) : new ObjectInputStream(is));
    }

    public byte[] readBytes() throws IOException {
        int len = getObjectInputStream().readInt();
        if (len < 0)
            return null;
        if (len == 0)
            return new byte[0];
        if (len > MAX_BYTE_ARRAY_LENGTH)
            throw new IOException("Byte array length too large. " + len);

        byte[] b = new byte[len];
        getObjectInputStream().readFully(b);
        return b;
    }

    public String readUTF() throws IOException {
        int len = getObjectInputStream().readInt();
        if (len < 0)
            return null;

        return getObjectInputStream().readUTF();
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        byte b = getObjectInputStream().readByte();
        if (b == 0)
            return null;

        return getObjectInputStream().readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls) throws IOException,
            ClassNotFoundException {
        return (T) readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

}