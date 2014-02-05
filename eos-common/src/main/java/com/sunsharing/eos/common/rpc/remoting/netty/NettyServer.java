/**
 * @(#)$CurrentFile
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2013
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author criss
 *<br> 13-8-16 上午9:20
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.remoting.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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
public class NettyServer {

    private static Logger logger = Logger.getLogger(NettyServer.class);
    public int port = 1315;
    private static final ServerBootstrap serverBootstrap
            = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()));
    private ChannelPipelineFactory channelPipelineFactory;

    public NettyServer(int port, ChannelPipelineFactory factory) {
        this.port = port;
        this.channelPipelineFactory = factory;
    }

    public boolean startup() throws Exception {
        /**
         * 采用默认ChannelPipeline管道
         * 这意味着同一个ExchangeServerHandler实例将被多个Channel通道共享
         * 这种方式对于ExchangeServerHandler中无有状态的成员变量是可以的，并且可以提高性能！
         * */
        //ChannelPipeline pipeline = serverBootstrap.getPipeline();
        /**
         * 解码器是基于文本行的协议，\r\n或者\n\r
         * */
        serverBootstrap.setPipelineFactory(channelPipelineFactory);

        serverBootstrap.setOption("child.tcpNoDelay", true); //注意child前缀
        //serverBootstrap.setOption("reuseAddress", true); //注意child前缀
        serverBootstrap.setOption("child.keepAlive", true); //注意child前缀

        /** ServerBootstrap对象的bind方法返回了一个绑定了本地地址的服务端Channel通道对象  */
        Channel channel = serverBootstrap.bind(new InetSocketAddress(port));
        logger.info("eos netty server is started on port " + port);
        return false;
    }

    public void shutdown() throws Exception {
        try {
            /**主动关闭服务器*/
            //ChannelGroupFuture future = allChannels.close();
            //future.awaitUninterruptibly();//阻塞，直到服务器关闭
            serverBootstrap.releaseExternalResources();
        } catch (Exception e) {
            logger.error("主动关闭eos netty server失败！", e);
        } finally {
            logger.info("eos netty server is shutdown on port " + port);
            System.exit(1);
        }
    }

}

