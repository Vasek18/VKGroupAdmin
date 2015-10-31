package ru.tearum.vkgroupadmin;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    android.app.FragmentTransaction fTrans;

    private OnMainFragmentIL mListener;

    groupsOnMainAdapter gomAdapter;

    BD vkgaBD;

    ListView groupsOnMainContainer;

    private static final int URL_LOADER = 0;

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();

        return f;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    // точка входа при подключении в активити
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        vkgaBD = new BD(getActivity());
        vkgaBD.open();

        // вывод групп на главной
        // формируем столбцы сопоставления
        String[] from = new String[] {"ava", "vk_id"};
        int[] to = new int[] {R.id.ivAva, R.id.tvName};

        // создаем адаптер и настраиваем список
        gomAdapter = new groupsOnMainAdapter(getActivity(), R.layout.group_on_main_item, null, from, to, 0);
        groupsOnMainContainer = (ListView) v.findViewById(R.id.groupsOnMainContainer);
        groupsOnMainContainer.setAdapter(gomAdapter);

        // создаем лоадер для чтения данных
        getLoaderManager().initLoader(URL_LOADER, null, this);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMainFragmentIL) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        return new gomCursorLoader(getActivity(), vkgaBD);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        gomAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }

    public interface OnMainFragmentIL {

    }

    public interface mListener{
        public void mnfEvent(String s);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        vkgaBD.close();
    }


    static class gomCursorLoader extends CursorLoader{

        BD db;

        public gomCursorLoader(Context context, BD db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getGroupsData();

            return cursor;
        }

    }
}
