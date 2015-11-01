package ru.tearum.vkgroupadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Вася on 31.10.2015.
 */
public class BD{
    private final Context vkgaCtx;
    private static final String LOG_TAG = "myLogs";

    private DBHelper vkgaBDHelper;
    private SQLiteDatabase vkgaBD;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_VK_ID = "vk_id";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String DATABASE_NAME = "vkgaDB";
    private static final int DATABASE_VERSION = 1;

    public BD(Context ctx){
        vkgaCtx = ctx;
    }

    // открыть подключение
    public void open(){
        vkgaBDHelper = new DBHelper(vkgaCtx, DATABASE_NAME, null, DATABASE_VERSION);
        vkgaBD = vkgaBDHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close(){
        if (vkgaBDHelper != null) vkgaBDHelper.close();
    }

    // добавление группы (только айдишник)
    public Integer addGroup(Integer vkID, String ava, String name){
//        Log.d(LOG_TAG, "addGroup");
        if (vkID == null){
            Log.d(LOG_TAG, "Не пришёл vk_id");
            return null;
        }
        // существует ли уже такая группа?
        String selection = COLUMN_VK_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(vkID)};
        Cursor c = vkgaBD.query("groups", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()){
            Log.d(LOG_TAG, "Уже есть группа с vk_id");
            return null;
        }

        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(COLUMN_VK_ID, vkID);
        cv.put("name", name);
        cv.put("ava", ava);

        return (int) vkgaBD.insert("groups", null, cv);
    }

    public void getTableInfo(String table){
        if (table == null){
            Log.d(LOG_TAG, "Не пришла таблица");
            return;
        }
        Cursor c = vkgaBD.query(table, null, null, null, null, null, null);
        if (c.moveToFirst()){
            int vkIDIndex = c.getColumnIndex(COLUMN_VK_ID);
            do{
                String row = "";
                for (int i = 0; i < c.getColumnCount(); i++){
                    row += c.getColumnName(i) + " + " + c.getString(i) + " ";
                }
                Log.d(LOG_TAG, row);
            } while (c.moveToNext());
        }
    }

    public Cursor getGroupsData(){
        String table = "groups as G";
        String columns[] = {"G." + COLUMN_ID + " as " + COLUMN_ID + "", "G.active as active", "G.name as name", "G.ava as ava", "G." + COLUMN_VK_ID + " as " + COLUMN_VK_ID + ""};
        String selection = "";
        String[] selectionArgs = {""};
        return vkgaBD.query(table, columns, null, null, null, null, null);
    }

    // поменять в дб картинки с урла на ресурс
    public void downloadImages(String table, String column){
//        Log.d(LOG_TAG, "downloadImages");
        if (table == null || table.isEmpty()){
            Log.d(LOG_TAG, "Не пришёл table");
            return;
        }
        if (column == null || column.isEmpty()){
            Log.d(LOG_TAG, "Не пришёл column");
            return;
        }
        // существует группы без скаченных картинок
        String selection = column + " LIKE ?";
        String[] selectionArgs = new String[]{"http%"};
//        Cursor c = vkgaBD.query(table, null, null, null, null, null, null);
        Cursor c = vkgaBD.query(table, null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()){
            Log.d(LOG_TAG, "Есть такие");
            return;
        }

       /* ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(COLUMN_VK_ID, vkID);
        cv.put("name", name);
        cv.put("ava", ava);

        return (int) vkgaBD.insert("groups", null, cv);*/
        return;
    }

    public Cursor getCommentDetail(Integer id){
        String table = "comments as C";
        String columns[] = {"C.user_name as user_name", "C.date as date", "C.text as text", "C.type as type"};
        String selection = "_id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return vkgaBD.query(table, columns, selection, selectionArgs, null, null, null);

    }

    public Cursor getGroupDetail(Integer id){
        String table = "groups as G";
        String columns[] = {"G.name as name"};
        String selection = "_id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return vkgaBD.query(table, columns, selection, selectionArgs, null, null, null);

    }

    // добавление коммента
    public Integer addComment(Integer user_id, Integer group_id, String text, Integer type, Integer related_id, Integer vkID, String user_name, String date){
        Log.d(LOG_TAG, "addComment " + user_id + " " + group_id + " " + type + " " + related_id + " " + vkID + " " + user_name);
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(COLUMN_VK_ID, vkID);
        cv.put("group_id", group_id);
        cv.put("type", type);
        cv.put("user_id", user_id);
        cv.put("text", text);
        cv.put("user_name", user_name);
        cv.put("related_id", related_id);
        cv.put("date", date);

        return (int) vkgaBD.insert("comments", null, cv);
    }

    // комментарии группы
    // todo по айдишнику группы фильтровать
    public Cursor getGroupComments(Integer group_id){
        String table = "comments as C";
        String columns[] = {"C." + COLUMN_ID + " as " + COLUMN_ID + "", "C.user_name as user_name", "C.text as text", "C.date as date"};
        String selection = "C.group_id > ?";
        String[] selectionArgs = {String.valueOf(group_id)};
        return vkgaBD.query(table, columns, selection, selectionArgs, null, null, null);
    }

    // класс для работы с БД
    class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version){
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db){
            ContentValues cv = new ContentValues(); // объект для данных

            // создаем таблицу групп
            db.execSQL("create table groups ("
                    + COLUMN_ID + " integer primary key autoincrement," // с нижним подчёркиванием, чтобы можно было получать id в адаптере
                    + "active integer,"
                    + COLUMN_VK_ID + " integer,"
                    + "name text,"
                    + "ava text"
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
                    + "in_favorite integer,"
                    + "date text"
                    + ");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
    }

}
