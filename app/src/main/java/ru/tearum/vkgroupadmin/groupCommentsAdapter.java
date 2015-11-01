package ru.tearum.vkgroupadmin;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Вася on 01.11.2015.
 */
public class groupCommentsAdapter extends SimpleCursorAdapter{

    BD db;

    private int layout;

    public groupCommentsAdapter(Context context, int _layout, Cursor c, String[] from, int[] to, int flags){
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
        LinearLayout llGroupWrap = (LinearLayout) v.findViewById(R.id.commentWrap);
        TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
        TextView tvText = (TextView) v.findViewById(R.id.tvText);

        if (c != null){
            String user_name = c.getString(c.getColumnIndex("user_name"));
            String text = c.getString(c.getColumnIndex("text"));
            String date = c.getString(c.getColumnIndex("date"));
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
