/*
 * @(#) ConfigurableGUI
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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>  参数配置面板
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class ConfigurableGUI implements SearchableConfigurable {

    private static final String ID = "Eos.Configurable";
    private static final String DISPLAY_NAME = "Eos Plugin";
    private static final String HELP_TOPIC = null;

    private final ApplicationConfig applicationConfig;
    private final ProjectConfig projectConfig;
    private JPanel contentPane;
    private JTextField eosURL;
    private JTextField eosUserName;
    private JTextField eosUserPassword;
    private JTextField appId;
    private JTextField webhook;
    private JTextField telPhones;

    public ConfigurableGUI(@NotNull Project project) {
        this.applicationConfig = ApplicationConfig.getInstance();
        this.projectConfig = ProjectConfig.getInstance(project);
        ApplicationManager.getApplication().invokeLater(() -> reloadAllConfigurations());
    }

    private void reloadAllConfigurations() {
        reset();
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return HELP_TOPIC;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return contentPane;
    }

    @Override
    public boolean isModified() {
        return !(
            eosURL.getText().equals(applicationConfig.getEosURL())
                && eosUserName.getText().equals(applicationConfig.getEosUserName())
                && eosUserPassword.getText().equals(applicationConfig.getEosUserPassword())
                && appId.getText().equals(projectConfig.getAppId())
                && webhook.getText().equals(projectConfig.getWebhook())
                && telPhones.getText().equals(projectConfig.getTelPhones())
        );
    }

    @Override
    public void apply() throws ConfigurationException {
        applicationConfig.setEosURL(eosURL.getText());
        applicationConfig.setEosUserName(eosUserName.getText());
        applicationConfig.setEosUserPassword(eosUserPassword.getText());
        projectConfig.setAppId(appId.getText());
        projectConfig.setWebhook(webhook.getText());
        projectConfig.setTelPhones(telPhones.getText());

    }

    @Override
    public void reset() {
        eosURL.setText(applicationConfig.getEosURL());
        eosUserName.setText(applicationConfig.getEosUserName());
        eosUserPassword.setText(applicationConfig.getEosUserPassword());
        appId.setText(projectConfig.getAppId());
        webhook.setText(projectConfig.getWebhook());
        telPhones.setText(projectConfig.getTelPhones());
    }

    @Override
    public void disposeUIResources() {
        contentPane = null;
    }
}

