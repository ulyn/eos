package com.sunsharing.eos.common.serialize.support.json;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.protocol.HeartPro;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


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

    public static void main(String[] args) {
        RpcInvocation rpc = new RpcInvocation();
        Map map = new HashMap();
        map.put("a", 1);
        rpc.setArguments(new Object[]{map, 1, "1", 1.1, new HeartPro()});
        String str = JSONObject.toJSONString(rpc);
        System.out.println(str);
        RpcInvocation de = JSON.parseObject(str, RpcInvocation.class);
    }
}