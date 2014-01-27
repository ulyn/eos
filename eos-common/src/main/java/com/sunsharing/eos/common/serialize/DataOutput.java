package com.sunsharing.eos.common.serialize;

import java.io.IOException;


public interface DataOutput {

    /**
     * Write boolean.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeBool(boolean v) throws IOException;

    /**
     * Write byte.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeByte(byte v) throws IOException;

    /**
     * Write short.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeShort(short v) throws IOException;

    /**
     * Write integer.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeInt(int v) throws IOException;

    /**
     * Write long.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeLong(long v) throws IOException;

    /**
     * Write float.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeFloat(float v) throws IOException;

    /**
     * Write double.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeDouble(double v) throws IOException;

    /**
     * Write string.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeUTF(String v) throws IOException;

    /**
     * Write byte array.
     *
     * @param v value.
     * @throws java.io.IOException
     */
    void writeBytes(byte[] v) throws IOException;

    /**
     * Write byte array.
     *
     * @param v   value.
     * @param off offset.
     * @param len length.
     * @throws java.io.IOException
     */
    void writeBytes(byte[] v, int off, int len) throws IOException;

    /**
     * Flush buffer.
     *
     * @throws java.io.IOException
     */
    void flushBuffer() throws IOException;
}