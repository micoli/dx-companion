package org.micoli.dxcompanion.ui;

import com.intellij.openapi.project.ProjectManager;

public class Notification {
    public static void message(String message){
            com.intellij.notification.NotificationGroupManager.getInstance()
                .getNotificationGroup("DX Companion")
                .createNotification("Opened item: " + message, com.intellij.notification.NotificationType.INFORMATION)
                .notify(ProjectManager.getInstance().getOpenProjects()[0]);

    }
}
