package ru.tearum.vkgroupadmin;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by Вася on 31.10.2015.
 */
public class groupsOnMainAdapter extends SimpleCursorAdapter{

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_VK_ID = "vk_id";

    private static final String LOG_TAG = "myLogs";

    BD db;

    private int layout;

    public groupsOnMainAdapter(Context context, int _layout, Cursor c, String[] from, int[] to, int flags){
        super(context, _layout, c, from, to, flags);

        layout = _layout;

        // todo убрать отсюда новое подключение к БД и передавать объект BD, потому что хз где закрывать подключения
        db = new BD(context);
        db.open();

    }

    //связывает данные с view на которые указывает курсор
    @Override
    public void bindView(View v, final Context context, Cursor c){
        super.bindView(v, context, c);

        // инициализируем элементы
        LinearLayout llGroupWrap = (LinearLayout) v.findViewById(R.id.llGroupWrap);
        ImageView ivAva = (ImageView) v.findViewById(R.id.ivAva);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvNewsCount = (TextView) v.findViewById(R.id.tvNewsCount);

        if (c != null){
            String ava = c.getString(c.getColumnIndex("ava"));
            String name = c.getString(c.getColumnIndex("name"));
            String active = c.getString(c.getColumnIndex("active"));
            String vk_id = c.getString(c.getColumnIndex("vk_id"));
        }
    }

    // сoздаёт новую view для хранения данных на которую указывает курсор
    @Override
    public View newView(Context _context, Cursor c, ViewGroup parent){
        super.newView(_context, c, parent);

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(layout, parent, false);

        return v;
    }
}
