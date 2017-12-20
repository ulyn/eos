/*
 * @(#) TestCall
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-19 16:48:43
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.chatbot.call;

import com.sunsharing.idea_plugin.chatbot.DingtalkChatbotClient;
import com.sunsharing.idea_plugin.chatbot.SendResult;
import com.sunsharing.idea_plugin.chatbot.message.MarkdownMessage;
import com.sunsharing.idea_plugin.chatbot.message.TextMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yope on 2017/12/19.
 */
public class TestCall {
    public static final String CHATBOT_WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=e93294f582643b66f09924540fd70a07a7a92d0e14f25852f4d862ce9b14bc97";

    public static void main(String[] args) throws IOException {
        DingtalkChatbotClient client = new DingtalkChatbotClient();
       /* TextMessage message = new TextMessage("我就是我, 是不一样的烟火");
        ArrayList<String> atMobiles = new ArrayList<String>();
        atMobiles.add("18850221127");
        message.setAtMobiles(atMobiles);*/
        // message.setIsAtAll(true);

    /*    LinkMessage message = new LinkMessage();
        message.setTitle("时代的火车向前开");
        message.setText("这个即将发布的新版本，创始人陈航（花名“无招”）称它为“红树林”。\n" +
            "而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是“红树林”？\"");
        message.setMessageUrl("https://mp.weixin.qq.com/s?spm=a219a.7629140.0.0.EUDyWG&__biz=MzA4NjMwMTA2Ng==&mid=2650316842&idx=1&sn=60da3ea2b29f1dcc43a7c8e4a7c97a16&scene=2&srcid=09189AnRJEdIiWVaKltFzNTw&from=timeline&isappinstalled=0&key=&ascene=2&uin=&devicetype=android-23&version=26031933&nettype=WIFI");
        message.setPicUrl("https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png");*/

        MarkdownMessage markdownMessage = new MarkdownMessage();
        markdownMessage.setTitle("这是一条Eos服务审批的消息！");
        markdownMessage.add(MarkdownMessage.getHeaderText(1, "这是一条Eos服务更新的消息，也是一条EOS服务审批的消息！"));
        markdownMessage.add("\n\n");
        markdownMessage.add("addressServie 地址服务");
        markdownMessage.add("\n\n");
        //message.add(MarkdownMessage.getImageText("http://img01.taobaocdn.com/top/i1/LB1GCdYQXXXXXXtaFXXXXXXXXXX"));
        markdownMessage.add(MarkdownMessage.getLinkText("点击跳转到Eos", "http://192.168.0.235:8100/index.html#/getjava/11/1578"));
        // message.add(MarkdownMessage.getLinkText("中文跳转", "dtmd://dingtalkclient/sendMessage?content=" + URLEncoder.encode("链接消息", "UTF-8")));

        SendResult result1 = client.send(CHATBOT_WEBHOOK, markdownMessage);

        System.out.println(result1);

        TextMessage textMessage = new TextMessage("我就是我, 是不一样的烟火");
        ArrayList<String> atMobiles = new ArrayList<String>();
        atMobiles.add("18850221127");
        textMessage.setAtMobiles(atMobiles);
        SendResult result2 = client.send(CHATBOT_WEBHOOK, textMessage);
        System.out.println(result2);

    }
}
