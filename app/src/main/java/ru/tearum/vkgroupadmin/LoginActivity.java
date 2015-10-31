package ru.tearum.vkgroupadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnLogin;
    public java.lang.String VK_USER_ID;
    private static final String LOG_TAG = "myLogs";

    public VKAccessToken VKAccessTokenRes; // для передачи ответа от входа в вк дальше в запрос про группы

    SharedPreferences sPref;

    Tools Tools;

    BD vkgaBD;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // на весь экран
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // если мы уже логинелись - сразу уходиим на другое активити
/*        if (isIDAndTokenInPrefs()){
            // идём на главное активити
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }*/

        // объявляем элементы
        btnLogin = (ImageButton) findViewById(R.id.btnLogin);

        // привязываем слушители событий
        btnLogin.setOnClickListener(this);

        Tools = new Tools();

        // подключаем бд
        vkgaBD = new BD(this);
        vkgaBD.open();
    }

    @Override
    public void onClick(View v){
//        Log.d(LOG_TAG, "Нажата кнопка");

        switch (v.getId()) {
            case R.id.btnLogin:

                // логин
                String scope = "offline, groups"; // пермишионы
                VKSdk.login(this, scope);

                break;
            default:
                break;
        }
    }

    @Override
    // здесь мы отлавливаем колбек логина вк
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>(){
            @Override
            // Пользователь успешно авторизовался
            public void onResult(VKAccessToken res){
                VK_USER_ID = res.userId;
                VKAccessTokenRes = res;
//                Log.d(LOG_TAG, "accessToken = " + res.accessToken);

                // запрос на группы пользователя
                VKRequest request = VKApi.groups().get(VKParameters.from(VKApiConst.USER_ID, VKAccessTokenRes.userId, "filter", "moder", "fields", "name,photo_100", "extended", 1));
//                VKRequest request = VKApi.groups().get(VKParameters.from(VKApiConst.USER_ID, VKAccessTokenRes.userId, "filter", "moder"));
                request.executeWithListener(new VKRequest.VKRequestListener(){
                    @Override
                    // успешный запрос
                    public void onComplete(VKResponse response){
                        super.onComplete(response);

                        // получение групп из респонса
                        String strResponse = response.responseString;
//                        Log.d(LOG_TAG, "strResponse = " + strResponse);

                        JSONObject jsonResponse = response.json;

                        JSONObject jsonResponseBody = null;
                        try {
                            jsonResponseBody = jsonResponse.getJSONObject("response");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // id групп в виде строки
                        String groupsArrStr = null;
                        // группы в виде массива
                        JSONArray groupsArr = null;
                        groupsArr = jsonResponseBody.optJSONArray("items");
                        // перебор групп
                        for (int i = 0; i < groupsArr.length(); i++){
                            String groupStr = null;
                            try {
                                groupStr = groupsArr.getString(i);
//                                Log.d(LOG_TAG, "group " + i + " = " + groupStr);
                                JSONObject group = new JSONObject(groupStr.toString());
                                Integer id = group.getInt("id");
                                String ava = group.getString("photo_100");
                                String name = group.getString("name");

                                // для сервака
                                groupsArrStr += id;

                                // записываем группу в бд
                                vkgaBD.addGroup(id, ava, name);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//                            Log.d(LOG_TAG, "group " + i + " = " + group.toString());
                        }

                        // группы в виде строки
                        Log.d(LOG_TAG, "resArray = " + groupsArrStr);


                        // здесь мы пушим на сервер токен
                        new LongOperation().execute(VKAccessTokenRes.userId, VKAccessTokenRes.accessToken, groupsArrStr);

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
            }

            @Override
            public void onError(VKError error){
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // отправка токена на сервак
    public void pushTokenOnServer(String id, String token, String groups) throws UnsupportedEncodingException{
//        Log.d(LOG_TAG, "pushTokenOnServer");

        // получение токена для gcm (просто чтобы в одном запросе)
        String gcmToken = getGcmToken();

        String query = "idvk=" + id + "&token=" + token + "&groupuser=" + groups + "&atoken=" + gcmToken;
        Log.d(LOG_TAG, query);
        Tools.request("http://vkadveyj.bget.ru/reg.php?" + query);

        // сохранение токена и id в Preferences
        putIDAndTokenInPrefs(id, token);

        // идём на главное активити
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    private class LongOperation extends AsyncTask<String, Void, Void>{
        @Override
        // асинхронный код
        protected Void doInBackground(String... params){
//            Log.d(LOG_TAG, "One " + params[0]);
//            Log.d(LOG_TAG, "Two " + params[1]);
            String id = params[0];
            String token = params[1];
            String groups = params[2];

            try {
                pushTokenOnServer(id, token, groups);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            Log.d(LOG_TAG, "async");
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.d(LOG_TAG, "post_async");
        }

        @Override
        protected void onPreExecute(){
            Log.d(LOG_TAG, "pre_async");
        }

        @Override
        protected void onProgressUpdate(Void... values){
            Log.d(LOG_TAG, "on_progress_async");
        }
    }

    public void putIDAndTokenInPrefs(String id, String token){
        Log.d(LOG_TAG, "Кладём в преференсес");
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("id", id);
        ed.putString("token", token);
        ed.commit();
    }

    public Boolean isIDAndTokenInPrefs(){
        Log.d(LOG_TAG, "Ищем в преференсес");
        sPref = getPreferences(MODE_PRIVATE);
        String id = sPref.getString("id", "");
        String token = sPref.getString("token", "");
        if ((id != null && !id.isEmpty()) && (token != null && !token.isEmpty())){
            return true;
        }
        return false;
    }


    public String getGcmToken(){
        // получаем токен
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try {
            token = instanceID.getToken(Tools.SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return token;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        vkgaBD.close();
    }
}