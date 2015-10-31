package ru.tearum.vkgroupadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Вася on 31.10.2015.
 */
public class BD {
    private final Context vkgaCtx;
    private static final String LOG_TAG = "myLogs";

    private DBHelper vkgaBDHelper;
    private SQLiteDatabase vkgaBD;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_VK_ID = "vk_id";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String DATABASE_NAME = "vkgaDB";
    private static final int DATABASE_VERSION = 1;

    public BD(Context ctx) {
        vkgaCtx = ctx;
    }

    // открыть подключение
    public void open() {
        vkgaBDHelper = new DBHelper(vkgaCtx, DATABASE_NAME, null, DATABASE_VERSION);
        vkgaBD = vkgaBDHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (vkgaBDHelper != null) vkgaBDHelper.close();
    }

    public Integer addGroup(Integer vkID) {
        if (vkID == null) {
            Log.d(LOG_TAG, "Не пришёл vk_id");
            return null;
        }

        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(COLUMN_VK_ID, vkID);

        return (int) vkgaBD.insert("groups", null, cv);
    }

    public void getGroups() {
        Cursor c = vkgaBD.query("groups", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int vkIDIndex = c.getColumnIndex(COLUMN_VK_ID);
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    Log.d(LOG_TAG, c.getColumnName(i) + " + " + c.getString(i));
                }
            } while (c.moveToNext());
        }
    }

    // класс для работы с БД
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            ContentValues cv = new ContentValues(); // объект для данных

            // создаем таблицу групп
            db.execSQL("create table groups ("
                    + COLUMN_ID + " integer primary key autoincrement," // с нижним подчёркиванием, чтобы можно было получать id в адаптере
                    + "active integer,"
                    + COLUMN_VK_ID + " integer,"
                    + "name text,"
                    + "photo text"
                    + ");");

            // создаем таблицу комментов
            db.execSQL("create table comments ("
                    + COLUMN_ID + " integer primary key autoincrement," // с нижним подчёркиванием, чтобы можно было получать id в адаптере
                    + "active integer,"
                    + COLUMN_VK_ID + " integer,"
                    + "group_id integer,"
                    + "type integer,"
                    + "text text,"
                    + "user_id integer,"
                    + "user_name text,"
                    + "related_id integer,"
                    + "in_favorite integer"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}