/*
 * @(#) ApplicationConfig
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
        name="EosPluginProjectConfig",
        storages = {
                @Storage("EosPluginProjectConfig.xml")}
)
public class ApplicationConfig implements PersistentStateComponent<ApplicationConfig> {

    private String eosURL = "http://192.168.0.235:8100";
    private String eosUserName;
    private String eosUserPassword;

    public String getEosURL() {
        return eosURL;
    }

    public void setEosURL(String eosURL) {
        this.eosURL = eosURL;
    }

    public String getEosUserName() {
        return eosUserName;
    }

    public void setEosUserName(String eosUserName) {
        this.eosUserName = eosUserName;
    }

    public String getEosUserPassword() {
        return eosUserPassword;
    }

    public void setEosUserPassword(String eosUserPassword) {
        this.eosUserPassword = eosUserPassword;
    }

    @Nullable
    @Override
    public ApplicationConfig getState() {
        return this;
    }

    @Override
    public void loadState(ApplicationConfig config) {
        XmlSerializerUtil.copyBean(config, this);
    }

    @Nullable
    public static ApplicationConfig getInstance() {
        ApplicationConfig config = ServiceManager.getService(ApplicationConfig.class);
        return config;
    }

    public boolean ready(){
        return !StringUtils.isBlank(eosURL)
                &&  !StringUtils.isBlank(eosUserName)
                &&  !StringUtils.isBlank(eosUserName);
    }
}

