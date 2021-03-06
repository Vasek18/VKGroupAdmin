package ru.tearum.vkgroupadmin;

/**
 * Created by Вася on 31.10.2015.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

public class NotificationUtils {


    private static final String TAG = NotificationUtils.class.getSimpleName();
    private static final String LOG_TAG = "myLogs";

    private static NotificationUtils instance;

    private static Context context;
    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
    private HashMap<Integer, Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления


    //приватный контструктор для Singleton
    private NotificationUtils(Context context){
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
    }
    /**
     * Получение ссылки на синглтон
     */
    public static NotificationUtils getInstance(Context context){
        if(instance==null){
            instance = new NotificationUtils(context);
        } else{
            instance.context = context;
        }
        return instance;
    }

    public int createInfoNotification(String title, String message, Integer comment_id){
        Intent notificationIntent = new Intent(context, MainActivity.class).putExtra("comment_id", comment_id); // по клику на уведомлении откроется MainActivity
        //       NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
        Notification.Builder nb = new Notification.Builder(context) //для версии Android > 3.0
                .setSmallIcon(R.drawable.logo350x350) //иконка уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setContentText(message) // Основной текст уведомления
                .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                .setContentTitle(title) //заголовок уведомления
                .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

        Notification notification = nb.build(); //генерируем уведомление
        manager.notify(lastId, notification); // отображаем его пользователю.
        notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
        return lastId++;
    }
}
