package com.sunsharing.eos.common.serialize;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface Serialization {

    /**
     * get content type id
     *
     * @return content type id
     */
    byte getContentTypeId();

    /**
     * get content type
     *
     * @return content type
     */
    String getContentType();

    /**
     * create serializer
     *
     * @param output
     * @return serializer
     * @throws java.io.IOException
     */
    ObjectOutput serialize(OutputStream output) throws IOException;

    /**
     * create deserializer
     *
     * @param input
     * @return deserializer
     * @throws java.io.IOException
     */
    ObjectInput deserialize(InputStream input) throws IOException;

}