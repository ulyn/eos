/*
 * @(#) SingleTargetActionCardMessage
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
import java.util.Map;

/**
 * Created by dustin on 2017/3/19.
 */
public class SingleTargetActionCardMessage implements Message {
    private String title;

    private String bannerUrl;
    private String briefTitle;
    private String briefText;

    private String singleTitle;
    private String singleURL;

    private boolean hideAvatar;

    public boolean isHideAvatar() {
        return hideAvatar;
    }

    public void setHideAvatar(boolean hideAvatar) {
        this.hideAvatar = hideAvatar;
    }

    public String getBriefTitle() {
        return briefTitle;
    }

    public void setBriefTitle(String briefTitle) {
        this.briefTitle = briefTitle;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBriefText() {
        return briefText;
    }

    public void setBriefText(String briefText) {
        this.briefText = briefText;
    }

    public String getSingleTitle() {
        return singleTitle;
    }

    public void setSingleTitle(String singleTitle) {
        this.singleTitle = singleTitle;
    }

    public String getSingleURL() {
        return singleURL;
    }

    public void setSingleURL(String singleURL) {
        this.singleURL = singleURL;
    }


    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "actionCard");

        Map<String, Object> actionCardContent = new HashMap<String, Object>();
        actionCardContent.put("title", title);

        StringBuffer text = new StringBuffer();
        if (StringUtils.isNotBlank(bannerUrl)) {
            text.append(MarkdownMessage.getImageText(bannerUrl) + "\n");
        }
        if (StringUtils.isNotBlank(briefTitle)) {
            text.append(MarkdownMessage.getHeaderText(3, briefTitle) + "\n");
        }
        if (StringUtils.isNotBlank(briefText)) {
            text.append(briefText + "\n");
        }
        actionCardContent.put("text", text.toString());

        if (hideAvatar) {
            actionCardContent.put("hideAvatar", "1");
        }
        if (StringUtils.isBlank(singleTitle)) {
            throw new IllegalArgumentException("singleTitle should not be blank");
        }
        if (StringUtils.isBlank(singleURL)) {
            throw new IllegalArgumentException("singleURL should not be blank");
        }

        actionCardContent.put("singleTitle", singleTitle);
        actionCardContent.put("singleURL", singleURL);

        items.put("actionCard", actionCardContent);

        return JSON.toJSONString(items);
    }
}
