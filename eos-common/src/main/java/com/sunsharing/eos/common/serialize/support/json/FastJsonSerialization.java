package com.sunsharing.eos.common.serialize.support.json;


import com.alibaba.fastjson.parser.ParserConfig;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FastJsonSerialization implements Serialization {
    static {
        ParserConfig.getGlobalInstance().putDeserializer(Object[].class,
                new ObjectArrayDeserializer());
    }

    public byte getContentTypeId() {
        return 6;
    }

    public String getContentType() {
        return "text/json";
    }

    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new FastJsonObjectOutput(output);
    }

    public ObjectInput deserialize(InputStream input) throws IOException {
        return new FastJsonObjectInput(input);
    }
//
//    public static void main(String[] args) {
//        RpcParams rpc = new RpcParams();
//        Map map = new HashMap();
//        map.put("a", 1);
//        rpc.setArguments(new Object[]{map, 1, "1", 1.1, new HeartPro()});
//        String str = JSONObject.toJSONString(rpc);
//        System.out.println(str);
//        RpcParams de = JSON.parseObject(str, RpcParams.class);
//    }
}