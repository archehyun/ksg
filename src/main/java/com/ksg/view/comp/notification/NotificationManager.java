package com.ksg.view.comp.notification;

import java.awt.Frame;

public class NotificationManager {

    private Frame frame;
    
    private static NotificationManager instance;

    private NotificationManager(){}
    
    public static NotificationManager getInstance()
    {
        if(instance ==null) instance = new NotificationManager();

        return instance;
    }
    public void setFrame(Frame frame)
    {
        this.frame =frame;
    }
    public Frame getFrame()
    {
        return this.frame;
    }

    public static void showNotification(String message)
    {
        showNotification(Notification.Type.SUCCESS, message);
    }

    public static void showNotification(Notification.Type type, String message)
    {
        Notification panel = new Notification(NotificationManager.getInstance().getFrame(), type, Notification.Location.TOP_CENTER, message);
        panel.showNotification();
    }
}
