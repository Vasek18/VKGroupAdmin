package ru.tearum.vkgroupadmin;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GCMService extends GcmListenerService {

    private static final String LOG_TAG = "myLogs";

    BD vkgaBD;

    public GCMService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");
        String group_id = data.getString("group");
        String type = data.getString("type");
        String commentID = data.getString("idcom");
        String user_id = data.getString("user_id");
        String date = data.getString("date");
//        Integer related_id = data.getInt("related_id");
        Log.d(LOG_TAG, "group_id = " + group_id);

        // Подключаемся к БД
        vkgaBD = new BD(this);
        vkgaBD.open();

        // запись в бд
        Integer related_id = 1;
        String user_name = "Олег";
        Integer newCommentID = vkgaBD.addComment(Integer.valueOf(user_id), Integer.valueOf(group_id), message, Integer.valueOf(type), related_id, Integer.valueOf(commentID), user_name, date, 1);
        Log.d(LOG_TAG, "Новый коммент = " + newCommentID);

        // Выводим уведомление
        NotificationUtils n = NotificationUtils.getInstance(this);
        n.createInfoNotification(title, message, newCommentID);
    }

}
