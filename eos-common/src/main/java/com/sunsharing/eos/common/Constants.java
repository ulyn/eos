package com.sunsharing.eos.common;


public class Constants {

    /**
     * 长连接
     */
    public static final String LONG_NETTY = "longNetty";
    /**
     * 短连接
     */
    public static final String SHORT_NETTY = "netty";

    public static final String DEFAULT_SERIALIZATION = "hessian";
    public static final String DEFAULT_PROXY = "jdk";
    public static final String DEFAULT_TRANSPORTER = LONG_NETTY;
    public static final int DEFAULT_TIMEOUT = 30000;


    //eos的部署模式
    public static final String EOS_MODE_PRO = "pro";
    //eos的开发模式
    public static final String EOS_MODE_DEV = "dev";

    //void的mock
    public static final String MOCK_VOID = "void";

    //正常
    public static final byte STATUS_SUCCESS = 1;
    //失败
    public static final byte STATUS_ERROR = 0;

}
