package com.sunsharing.eos.common.serialize.support.java;

import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CompactedJavaSerialization implements Serialization {

    public byte getContentTypeId() {
        return 4;
    }

    public String getContentType() {
        return "x-application/compactedjava";
    }

    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new JavaObjectOutput(out, true);
    }

    public ObjectInput deserialize(InputStream is) throws IOException {
        return new JavaObjectInput(is, true);
    }

}