/*
 * @(#) FeedCardMessage
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-19 16:40:54
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.chatbot.message;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dustin on 2017/3/19.
 */
public class FeedCardMessage implements Message {

    private List<FeedCardMessageItem> feedItems;

    public List<FeedCardMessageItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(List<FeedCardMessageItem> feedItems) {
        this.feedItems = feedItems;
    }

    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "feedCard");

        Map<String, Object> feedCard = new HashMap<String, Object>();

        if (feedItems == null || feedItems.isEmpty()) {
            throw new IllegalArgumentException("feedItems should not be null or empty");
        }
        for (FeedCardMessageItem item : feedItems) {
            if (StringUtils.isBlank(item.getTitle())) {
                throw new IllegalArgumentException("title should not be blank");
            }
            if (StringUtils.isBlank(item.getMessageURL())) {
                throw new IllegalArgumentException("messageURL should not be blank");
            }
            if (StringUtils.isBlank(item.getPicURL())) {
                throw new IllegalArgumentException("picURL should not be blank");
            }
        }
        feedCard.put("links", feedItems);
        items.put("feedCard", feedCard);

        return JSON.toJSONString(items);
    }
}
