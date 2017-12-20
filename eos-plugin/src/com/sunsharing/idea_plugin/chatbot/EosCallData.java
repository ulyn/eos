/*
 * @(#) EosCallData
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-19 17:40:25
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.chatbot;

/**
 * Created by yope on 2017/12/19.
 */
public class EosCallData {

    private String eosURL;
    private String userName;
    private String webhook;
    private String telPhones;
    private String serviceName;
    private String appId;
    private String module;
    private String selectFileName;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSelectFileName() {
        return selectFileName;
    }

    public void setSelectFileName(String selectFileName) {
        this.selectFileName = selectFileName;
    }

    public String getEosURL() {
        return eosURL;
    }

    public void setEosURL(String eosURL) {
        this.eosURL = eosURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getTelPhones() {
        return telPhones;
    }

    public void setTelPhones(String telPhones) {
        this.telPhones = telPhones;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public String toString() {
        return "EosCallData{" +
            "eosURL='" + eosURL + '\'' +
            ", userName='" + userName + '\'' +
            ", webhook='" + webhook + '\'' +
            ", telPhones='" + telPhones + '\'' +
            ", serviceName='" + serviceName + '\'' +
            ", appId='" + appId + '\'' +
            ", module='" + module + '\'' +
            ", selectFileName='" + selectFileName + '\'' +
            '}';
    }
}
