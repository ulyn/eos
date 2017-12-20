/*
 * @(#) EventLogger
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-18 16:55:40
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;


public class EventLogger {
    private static final String GROUP_ID = "Eos Publish";//The unique group id where "Event Log" could use to group your messages together.
    private static final String TITLE = "Eos Publish Event Log";//The title on Balloon

    /**
     * Print log to "Event Log"
     */
    public static void log(String msg) {
        System.out.println(msg);
        Notification notification = new Notification(GROUP_ID, TITLE, msg, NotificationType.INFORMATION);//build a notification
        Notifications.Bus.notify(notification);//use the default bus to notify (application level)
        if(notification.getBalloon() != null) {
            notification.getBalloon().hide(true);//try to hide the balloon immediately.
        }
    }
}