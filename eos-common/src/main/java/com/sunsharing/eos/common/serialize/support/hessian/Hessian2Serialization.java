package com.sunsharing.eos.common.serialize.support.hessian;

import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Hessian2Serialization implements Serialization {

    public static final byte ID = 2;

    public byte getContentTypeId() {
        return ID;
    }

    public String getContentType() {
        return "x-application/hessian2";
    }

    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new Hessian2ObjectOutput(out);
    }

    public ObjectInput deserialize(InputStream is) throws IOException {
        return new Hessian2ObjectInput(is);
    }

}