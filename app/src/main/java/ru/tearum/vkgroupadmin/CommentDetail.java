package ru.tearum.vkgroupadmin;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CommentDetail extends Fragment{

    private static final String LOG_TAG = "myLogs";

    SimpleCursorAdapter scAdapter;
    BD vkgaBD;

    public Integer comment_id;

    TextView tvName;
    TextView tvDate;
    TextView tvCommentPlace;
    TextView tvComment;
    Button btnDel;
    Button btnImportant;
    EditText mtvAnswer;
    Button btnSend;

    String newCommentText;

    public CommentDetail(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.comment_detail, container, false);

        // инициализируем элементы
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvCommentPlace = (TextView) v.findViewById(R.id.tvCommentPlace);
        tvComment = (TextView) v.findViewById(R.id.tvComment);
        btnDel = (Button) v.findViewById(R.id.btnDel);
        btnImportant = (Button) v.findViewById(R.id.btnImportant);
        mtvAnswer = (EditText) v.findViewById(R.id.mtvAnswer);
        btnSend = (Button) v.findViewById(R.id.btnSend);

        // Подключаемся к БД
        vkgaBD = new BD(getActivity());
        vkgaBD.open();

        // вывод основной инфы про коммент
//        vkgaBD.getTableInfo("comments");
        comment_id = 3;
        Cursor c = vkgaBD.getCommentDetail(comment_id); // todo переделать на асинхронность
        if (c != null) {
            if (c.moveToFirst()) {
                tvName.setText(c.getString(c.getColumnIndex("user_name")));
                tvDate.setText(c.getString(c.getColumnIndex("date")));
                tvComment.setText(c.getString(c.getColumnIndex("text")));
                tvCommentPlace.setText(c.getString(c.getColumnIndex("commentPlace")));
            }
            else{
                Log.d(LOG_TAG, "Нет такого коммента");
            }
        }


        // Добавление коммента
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newCommentText = mtvAnswer.getText().toString();

                // проверка на пустоту
                if ("".equals(newCommentText)) {
                    Toast.makeText(getActivity(), R.string.error_empty_comment, Toast.LENGTH_LONG).show();
                    return;
                }

                Integer user_id = 1; // todo текущий пользователь
                Integer group_id = 1;
                String text = "Ololo";
                Integer type = 1;
                Integer related_id = 1;
                Integer vkID = 1;
                String user_name = "Олег";
                // todo обновлять список комментариев
                Integer newCommentID = vkgaBD.addComment(user_id, group_id, text, type, related_id, vkID, user_name);
                Log.d(LOG_TAG, "Новый коммент = " + newCommentID);
            }
        });

        return v;
    }

    public static CommentDetail newInstance(Integer id) {
        CommentDetail f = new CommentDetail();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("comment_id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comment_id = getArguments() != null ? getArguments().getInt("comment_id") : 0;
    }

}
