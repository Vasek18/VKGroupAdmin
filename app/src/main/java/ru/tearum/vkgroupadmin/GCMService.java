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
        Integer group_id = data.getInt("group");
        Integer type = data.getInt("type");
        Integer commentID = data.getInt("idcom");
        Integer user_id = data.getInt("user_id");
        String date = data.getString("date");
//        Integer related_id = data.getInt("related_id");

        // Подключаемся к БД
        vkgaBD = new BD(this);
        vkgaBD.open();

        // запись в бд
        Integer related_id = 1;
        String user_name = "Олег";
        Integer newCommentID = vkgaBD.addComment(user_id, group_id, message, type, related_id, commentID, user_name, date);
        Log.d(LOG_TAG, "Новый коммент = " + newCommentID);

        // Выводим уведомление
        NotificationUtils n = NotificationUtils.getInstance(this);
        n.createInfoNotification(title, message, newCommentID);
    }

    private void sendNotification(String message) {
       /* Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/
    }
}
