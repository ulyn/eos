/*
 * @(#) ProjectConfig
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-15 16:30:16
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.configurator;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

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
@State(
    name = "EosPluginApplicationConfig",
    storages = {
        @Storage("EosPluginApplicationConfig.xml")}
)
public class ProjectConfig implements PersistentStateComponent<ProjectConfig> {

    private String appId;
    private String webhook="";
    private String telPhones="";

    public String getTelPhones() {
        return telPhones;
    }

    public void setTelPhones(String telPhones) {
        this.telPhones = telPhones;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    @Nullable
    @Override
    public ProjectConfig getState() {
        return this;
    }

    @Override
    public void loadState(ProjectConfig config) {
        XmlSerializerUtil.copyBean(config, this);
    }

    @Nullable
    public static ProjectConfig getInstance(Project project) {
        ProjectConfig config = ServiceManager.getService(project, ProjectConfig.class);
        return config;
    }


    public boolean ready() {
        return !StringUtils.isBlank(appId)
            && !StringUtils.isBlank(webhook)
            && !StringUtils.isBlank(telPhones);
    }
}

