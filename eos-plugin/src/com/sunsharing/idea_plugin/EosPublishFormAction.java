/*
 * @(#) EosPublishFormAction
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-15 16:03:50
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.sunsharing.idea_plugin.chatbot.EosCallData;
import com.sunsharing.idea_plugin.chatbot.call.DingTalkCall;
import com.sunsharing.idea_plugin.configurator.ApplicationConfig;
import com.sunsharing.idea_plugin.configurator.ProjectConfig;
import com.sunsharing.idea_plugin.eos.EosManage;
import com.sunsharing.idea_plugin.eos.EosResult;
import com.sunsharing.idea_plugin.utils.EventLogger;
import com.sunsharing.idea_plugin.utils.InterfaceServcie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by yope on 2017/12/15.
 */
public class EosPublishFormAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        VirtualFile dirRoot = event.getData(CommonDataKeys.VIRTUAL_FILE);
        File selectFile = VfsUtilCore.virtualToIoFile(dirRoot);

        ValidateSelectFile validateSelectFile = new ValidateSelectFile(selectFile).invoke();
        if (validateSelectFile.is()) return;

        String serviceName = validateSelectFile.getServiceName();
        String module = validateSelectFile.getModule();
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        ProjectConfig projectConfig = ProjectConfig.getInstance(event.getProject());

        EosManage eosManage = new EosManage(applicationConfig.getEosURL(), applicationConfig.getEosUserName(), applicationConfig.getEosUserPassword());
        EosResult<String> result = eosManage.login();
        if (result != null && result.toString().indexOf("status=true") != -1) {
            EventLogger.log("uuid登录成功！");
        } else {
            EventLogger.log("uuid 登录失败！！！");
            Messages.showErrorDialog("uuid 登录失败！！！", "错误提醒");
            return;
        }

        try {
            EosResult res = eosManage.postJavaFile(selectFile, serviceName, module, projectConfig);
            if (res.isSuccess()) {
                EventLogger.log("服务文件上传成功！请通知管理员审批!");
                Messages.showWarningDialog("服务文件上传成功！请通知管理员审批!", "成功提醒");
                if (projectConfig.getWebhook() == null) {
                    EventLogger.log("未配置webhook 不做钉钉消息的推送！");
                    return;
                }
                EosCallData eosCallData = new EosCallData();
                eosCallData.setEosURL(applicationConfig.getEosURL());
                eosCallData.setUserName(applicationConfig.getEosUserName());
                eosCallData.setAppId(projectConfig.getAppId());
                eosCallData.setWebhook(projectConfig.getWebhook());
                eosCallData.setTelPhones(projectConfig.getTelPhones());
                eosCallData.setServiceName(serviceName);
                eosCallData.setModule(module);
                eosCallData.setSelectFileName(selectFile.getName());

                DingTalkCall.call(eosCallData);

                return;
            } else {
                EventLogger.log("服务文件上传失败！！！");
                Messages.showErrorDialog("服务文件上传失败！", "错误提醒");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // sayHello(project, serviceName);
    }

    private String askForName(Project project) {
        return Messages.showInputDialog(project,
            "请输入服务名称", "输入服务名称",
            Messages.getQuestionIcon());
    }

    private void alertMsg(Project project, String msg) {
        Messages.showMessageDialog(project,
            msg, "提示信息",
            Messages.getInformationIcon());
    }

    private class ValidateSelectFile {
        private boolean myResult;
        private File selectFile;
        private String serviceName;
        private String module;

        public ValidateSelectFile(File selectFile) {
            this.selectFile = selectFile;
        }

        boolean is() {
            return myResult;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getModule() {
            return module;
        }

        public ValidateSelectFile invoke()  {
            EventLogger.log("验证文件的规范问题开始");
            if (selectFile != null && selectFile.getName().indexOf(".java") == -1) {
                EventLogger.log("对不起！您选中的文件不符合上传规范!(不是java类)");
                Messages.showErrorDialog("对不起！您选中的文件不符合上传规范！(不是java类)", "错误提醒");
                myResult = true;
                return this;
            }
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader reader = null;
            try {
                fis = new FileInputStream(selectFile);
                isr = new InputStreamReader(fis, "UTF-8");
                reader = new BufferedReader(isr);
            } catch (Exception e) {
                EventLogger.log("读取不到文件" + e.getMessage());
            }
            List<String> strList = new ArrayList<String>();
            String line = "";
            try {
                while ((line = reader.readLine()) != null) {
                    strList.add(line);
                }
            } catch (IOException e) {
                EventLogger.log("读取文件出错啦！" + e.getMessage());
            }finally {
                try {
                    reader.close();
                    isr.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String[] lines = strList.toArray(new String[]{});
            InterfaceServcie service = new InterfaceServcie();
            if (service.getInterfaceName(lines) == null) {
                EventLogger.log("对不起！选中的java 类不是接口类，无法上传！");
                Messages.showErrorDialog("对不起！选中的java 类不是接口类，无法上传！", "错误提醒");
                myResult = true;
                return this;
            }
            serviceName = service.getInterfaceEosShow(lines);
            if (serviceName == null) {
                EventLogger.log("服务注解未找到desc 关键字！参看: @EosService(desc=\"地址服务\",module=\"通用\")");
                Messages.showErrorDialog("服务注解未找到desc 关键字！参看: @EosService(desc=\"地址服务\",module=\"通用\")", "错误提醒");
                myResult = true;
                return this;
            }
            module = service.getInterfaceEosModule(lines);
            if (module == null) {
                EventLogger.log("服务注解未找到module 关键字！参看: @EosService(desc=\"地址服务\",module=\"通用\")");
                Messages.showErrorDialog("服务注解未找到module 关键字！参看: @EosService(desc=\"地址服务\",module=\"通用\")", "错误提醒");
                myResult = true;
                return this;
            }
            EventLogger.log("验证文件的规范问题结束");
            myResult = false;
            return this;

        }
    }
}
