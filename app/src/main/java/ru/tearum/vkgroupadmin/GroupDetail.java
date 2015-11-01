package ru.tearum.vkgroupadmin;


import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class GroupDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = "myLogs";

    SimpleCursorAdapter gcAdapter;
    BD vkgaBD;

    public static Integer group_id;

    Integer vkid;
    String ownerName;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    TextView tvName;

    ListView commentsContainer;

    private static final int URL_LOADER = 1;

    android.app.FragmentTransaction fTrans;

    private static final String BACK_STACK_TAG = null;

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

        // вывод комментариев группы
        // формируем столбцы сопоставления
        String[] from = new String[] {"user_name", "text", "date"};
        int[] to = new int[] {R.id.tvUserName, R.id.tvText, R.id.tvDate};

        // создаем адаптер и настраиваем список
        gcAdapter = new groupCommentsAdapter(getActivity(), R.layout.group_comments_item, null, from, to, 0);
        commentsContainer = (ListView) v.findViewById(R.id.commentsList);
        commentsContainer.setAdapter(gcAdapter);

        // создаем лоадер для чтения данных
        getLoaderManager().initLoader(URL_LOADER, null, this);

        // обработчик нажатия на элемент списка
        commentsContainer.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id){

                // переход на детальную страницу чекина
                fTrans = getFragmentManager().beginTransaction();
                CommentDetail commentDetailFragment = CommentDetail.newInstance((int) id);
                fTrans.replace(R.id.frgmCont, commentDetailFragment);
                fTrans.addToBackStack(BACK_STACK_TAG); // добавляем в стек (для кнопки назад)
                fTrans.commit();

            }
        });

        // отметить комментарии как увиденные
        vkgaBD.iVeSeenComments(group_id);

        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        vkgaBD.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new gcCursorLoader(getActivity(), vkgaBD);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        gcAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }

    static class gcCursorLoader extends CursorLoader{

        BD db;

        public gcCursorLoader(Context context, BD db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getGroupComments(group_id);

            return cursor;
        }

    }

}
