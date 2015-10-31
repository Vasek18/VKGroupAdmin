package ru.tearum.vkgroupadmin;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class CommentDetail extends Fragment{

    private static final String LOG_TAG = "myLogs";

    SimpleCursorAdapter scAdapter;
    BD db;

    public static long comment_id;

    TextView tvName;
    TextView tvDate;
    TextView tvCommentPlace;
    TextView tvComment;
    Button btnDel;
    Button btnImportant;
    EditText mtvAnswer;
    Button btnSend;

    public CommentDetail(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.comment_detail, container, false);
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
