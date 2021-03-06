package ru.tearum.vkgroupadmin;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import ru.tearum.vkgroupadmin.MainFragment.OnMainFragmentIL;


public class MainActivity extends AppCompatActivity implements OnMainFragmentIL{

    private static final String LOG_TAG = "myLogs";
    TextView tvName;
    public java.lang.String VK_USER_ID;

    Fragment mainFrag;
    android.app.FragmentTransaction fTrans;

    BD vkgaBD;

    SharedPreferences sPref;

    private static final String BACK_STACK_TAG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // на весь экран
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tvName = (TextView) findViewById(R.id.user_name);

        Integer comment_id = null;
        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras != null){
                comment_id = extras.getInt("comment_id");
            }
        }

        // подключаем бд
        vkgaBD = new BD(this);
        vkgaBD.open();

        vkgaBD.getTableInfo("comments");

        // скачиваем картинки
//        vkgaBD.downloadImages("groups", "ava");

        // подключаем фрагмент
        if (comment_id != null){
            mainFrag = new CommentDetail();
        } else{
            mainFrag = new MainFragment();
        }

        fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.frgmCont, mainFrag);
        fTrans.addToBackStack(BACK_STACK_TAG); // добавляем в стек (для кнопки назад)
        fTrans.commit();

        // зачем каждый раз имя получать?
        sPref = getPreferences(MODE_PRIVATE);
        String name = sPref.getString("name", "");
        if (name == null || name.isEmpty()){
            // запрос для получения имени юзера
            VKRequest request = VKApi.users().get();
            request.executeWithListener(new VKRequest.VKRequestListener(){
                @Override
                // успешный запрос
                public void onComplete(VKResponse response){
                    super.onComplete(response);
//                Log.d(LOG_TAG, "Что-то получилось");

//                Log.d(LOG_TAG, response.responseString);

                    org.json.JSONObject json = response.json;

                    // приходит массив в json, берём его
                    org.json.JSONArray jsonArr = null;
                    try {
                        jsonArr = json.getJSONArray("response");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // массив в json - массив, поэтому преобразуем его в json
                    JSONObject res = null;
                    try {
                        res = (JSONObject) jsonArr.get(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // берём имя из подмассива
                    String name = "";
                    try {
                        name = res.getString("first_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                Log.d(LOG_TAG, "name = " + name);
                    // ставим имя в шапку
                    tvName.setText(name);

                    // записываем имя в префы
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("name", name);
                    ed.commit();

//                Log.d(LOG_TAG, "name = " + name);
//                Log.d(LOG_TAG, json.toString());

                }

                @Override
                public void onError(VKError error){
//Do error stuff
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts){
//I don't really believe in progress
                }
            });
        } else{
            // ставим имя в шапку
            tvName.setText(name);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>(){
            @Override
            // Пользователь успешно авторизовался
            public void onResult(VKAccessToken res){
                VK_USER_ID = res.userId;
            }

            @Override
            public void onError(VKError error){
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        vkgaBD.close();
    }
}
