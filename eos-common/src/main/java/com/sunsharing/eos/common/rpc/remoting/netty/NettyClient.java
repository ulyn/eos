/**
 * @(#)NettyClient
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 上午10:19
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.rpc.RpcClient;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

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
public abstract class NettyClient implements RpcClient {
    Logger logger = Logger.getLogger(NettyClient.class);

    public static final int CONNECT_TIMEOUT = 5000;

    public static Map<String, ArrayBlockingQueue<ResponsePro>> result =
            new ConcurrentHashMap<String, ArrayBlockingQueue<ResponsePro>>();

    // 因ChannelFactory的关闭有DirectMemory泄露，采用静态化规避
    // https://issues.jboss.org/browse/NETTY-424
    private final static ChannelFactory clientSocketChannelFactory = new
            NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool());
    private ClientBootstrap clientBootstrap;

    public Channel connect(String ip,int port) throws Exception
    {
        clientBootstrap = new ClientBootstrap(clientSocketChannelFactory);
        ChannelPipeline pipeline = clientBootstrap.getPipeline();
        pipeline.addLast("decoder", new ExDecode());
        pipeline.addLast("encoder", new ExEncode());
        pipeline.addLast("handler", new ClientHandler());

        clientBootstrap.setOption("tcpNoDelay", true);
        clientBootstrap.setOption("keepAlive", true);
        clientBootstrap.setOption("connectTimeoutMillis", CONNECT_TIMEOUT);
        clientBootstrap.setOption("reuseAddress", true); //注意child前缀
        ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(ip, new Integer(port)));
        final CountDownLatch channelLatch = new CountDownLatch(1);
        final String ipFinal = ip;
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture cf) throws Exception {
                if (cf.isSuccess()) {
//                    channel = cf.getChannel();
                    channelLatch.countDown();
                } else {
//                    throw new RpcException(RpcException.CONNECT_EXCEPTION, "client failed to connect to server "
//                            + ipFinal + ", error message is:" + cf.getCause() == null ? "unknown" : cf.getCause().getMessage(), cf.getCause());
                }
            }
        });
        channelLatch.await(5,TimeUnit.SECONDS);
        if(future.isSuccess())
        {
            return future.getChannel();
        }else
        {
            throw new RpcException(RpcException.CONNECT_EXCEPTION, "client failed to connect to server "
                    + ipFinal + ", port:"+port+"error message is:");
        }

    }

    protected ResponsePro getResult(RequestPro pro, MyChannel channel,int timeout) throws Throwable
    {
        if (StringUtils.isBlank(pro.getMsgId())) {
            pro.setMsgId(StringUtils.genUUID());
        }
        result.put(pro.getMsgId(), new ArrayBlockingQueue<ResponsePro>(1));
        try {

            channel.write(pro);

            //等待返回
            ArrayBlockingQueue<ResponsePro> blockingQueue = result.get(pro.getMsgId());
            ResponsePro result = blockingQueue.poll(timeout, TimeUnit.MILLISECONDS);
            logger.debug("返回结果:" + result);
            if (result == null) {
                logger.error("等待结果超时!");
                throw new RpcException(RpcException.TIMEOUT_EXCEPTION);
            }
            return result;
        } catch (Exception e) {
            logger.error("请求出错！", e);
            throw e;
        } finally {
            result.remove(pro.getMsgId());
//            if (channel != null) {
//                channel.close();
//            }
//                clientBootstrap.releaseExternalResources();
        }
    }
}

