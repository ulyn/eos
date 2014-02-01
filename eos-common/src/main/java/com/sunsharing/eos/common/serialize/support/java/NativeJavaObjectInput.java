package com.sunsharing.eos.common.serialize.support.java;


import com.sunsharing.eos.common.serialize.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

public class NativeJavaObjectInput implements ObjectInput {

    private final ObjectInputStream inputStream;

    public NativeJavaObjectInput(InputStream is) throws IOException {
        this(new ObjectInputStream(is));
    }

    protected NativeJavaObjectInput(ObjectInputStream is) {
        inputStream = is;
    }

    protected ObjectInputStream getObjectInputStream() {
        return inputStream;
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return (T) readObject();
    }

    public boolean readBool() throws IOException {
        return inputStream.readBoolean();
    }

    public byte readByte() throws IOException {
        return inputStream.readByte();
    }

    public short readShort() throws IOException {
        return inputStream.readShort();
    }

    public int readInt() throws IOException {
        return inputStream.readInt();
    }

    public long readLong() throws IOException {
        return inputStream.readLong();
    }

    public float readFloat() throws IOException {
        return inputStream.readFloat();
    }

    public double readDouble() throws IOException {
        return inputStream.readDouble();
    }

    public String readUTF() throws IOException {
        return inputStream.readUTF();
    }

    public byte[] readBytes() throws IOException {
        int len = inputStream.readInt();
        if (len < 0) {
            return null;
        } else if (len == 0) {
            return new byte[]{};
        } else {
            byte[] result = new byte[len];
            inputStream.readFully(result);
            return result;
        }
    }
}
