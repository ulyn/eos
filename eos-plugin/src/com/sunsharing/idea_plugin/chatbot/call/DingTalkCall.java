/*
 * @(#) DingTalkCall
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-19 17:34:04
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
import com.sunsharing.idea_plugin.chatbot.EosCallData;
import com.sunsharing.idea_plugin.chatbot.SendResult;
import com.sunsharing.idea_plugin.chatbot.message.MarkdownMessage;
import com.sunsharing.idea_plugin.chatbot.message.TextMessage;
import com.sunsharing.idea_plugin.utils.EventLogger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yope on 2017/12/19.
 */
public class DingTalkCall {
    private static DingtalkChatbotClient client = null;

    static {
        init();
    }

    private static void init() {
        client = new DingtalkChatbotClient();
    }

    public static void call(final EosCallData eosCallData) throws IOException {
        MarkdownMessage markdownMessage = new MarkdownMessage();
        String content = eosCallData.getUserName() + "上传" + eosCallData.getSelectFileName() + "文件到EOS，\n\n" +
            "更新的服务所属模块："+ eosCallData.getModule() + " ，\n\n" +
            "服务中文名：" + eosCallData.getServiceName() + "!\n\n" +
            "请相关研发人员知悉！";
        String linkUrl = eosCallData.getEosURL() + "/index.html#/servicelist/" + eosCallData.getAppId() + "/0";
        markdownMessage.setTitle("这是一条EOS服务审批的消息！");
        markdownMessage.add(MarkdownMessage.getHeaderText(4, "这是一条EOS服务更新的消息，也是一条EOS服务审批的消息！"));
        markdownMessage.add("\n\n");
        markdownMessage.add("> "+content);
        markdownMessage.add("\n\n");
        markdownMessage.add(MarkdownMessage.getLinkText("点击跳转到EOS", linkUrl));
        // message.add(MarkdownMessage.getLinkText("中文跳转", "dtmd://dingtalkclient/sendMessage?content=" + URLEncoder.encode("链接消息", "UTF-8")));

        TextMessage textMessage = new TextMessage("请审核人员及时审批！请研发人员注意服务版本变更影响！");
        if (eosCallData.getTelPhones() != null) {
            ArrayList<String> atMobiles = new ArrayList<String>();
            String strArr[] = eosCallData.getTelPhones().split(",");
            for (String tel : strArr) {
                atMobiles.add(tel);
            }
            textMessage.setAtMobiles(atMobiles);
        } else {
            EventLogger.log("为配置相关人员 默认@所有人");
            textMessage.setIsAtAll(true);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EventLogger.log("markdownMessage=" + markdownMessage.toJsonString());
                    SendResult markdownMessageResult = client.send(eosCallData.getWebhook(), markdownMessage);
                    EventLogger.log("markdownMessageResult=" + markdownMessageResult.toString());
                } catch (IOException e) {
                    EventLogger.log("markdownMessageResult 接收IO异常！" + e.getMessage());
                }
                try {
                    EventLogger.log("textMessage=" + textMessage.toJsonString());
                    SendResult textMessageResult = client.send(eosCallData.getWebhook(), textMessage);
                    EventLogger.log("textMessageResult=" + textMessageResult.toString());
                } catch (IOException e) {
                    EventLogger.log("textMessageResult 接收IO异常！" + e.getMessage());
                }

            }
        }).start();
    }
}
