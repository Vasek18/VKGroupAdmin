package ru.tearum.vkgroupadmin;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class GroupDetail extends Fragment{

    private static final String LOG_TAG = "myLogs";

    SimpleCursorAdapter scAdapter;
    BD vkgaBD;

    public Integer group_id;

    Integer vkid;
    String ownerName;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    TextView tvName;


    public static GroupDetail newInstance(Integer id){
        GroupDetail f = new GroupDetail();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("group_id", id);
        f.setArguments(args);

        return f;
    }

    public GroupDetail(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        group_id = getArguments() != null ? getArguments().getInt("group_id") : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_group_detail, container, false);

        // инициализируем элементы
        tvName = (TextView) v.findViewById(R.id.tvName);

        // Подключаемся к БД
        vkgaBD = new BD(getActivity());
        vkgaBD.open();

        Cursor c = vkgaBD.getGroupDetail(group_id);
        if (c != null) {
            if (c.moveToFirst()) {
                tvName.setText(c.getString(c.getColumnIndex("name")));
            }
            else{
                Log.d(LOG_TAG, "Нет такой группы");
            }
        }

        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        vkgaBD.close();
    }

}
