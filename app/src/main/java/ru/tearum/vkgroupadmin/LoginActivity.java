package ru.tearum.vkgroupadmin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    public java.lang.String VK_USER_ID;
    private static final String LOG_TAG = "myLogs";

    public VKAccessToken VKAccessTokenRes; // для передачи ответа от входа в вк дальше в запрос про группы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // на весь экран
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // объявляем элементы
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // привязываем слушители событий
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            // Пользователь успешно авторизовался
            public void onResult(VKAccessToken res) {
                VK_USER_ID = res.userId;
                VKAccessTokenRes = res;
//                Log.d(LOG_TAG, "accessToken = " + res.accessToken);

                // запрос на группы пользователя
                VKRequest request = VKApi.groups().get(VKParameters.from(VKApiConst.USER_ID, VKAccessTokenRes.userId, "filter", "moder"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    // успешный запрос
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        String strResponse = response.responseString;
                        JSONObject jsonResponse = response.json;

                        JSONObject jsonResponseBody = null;
                        try {
                            jsonResponseBody = jsonResponse.getJSONObject("response");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String groupsArr = null;
                        try {
                            groupsArr = jsonResponseBody.getString("items");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(LOG_TAG, "resArray = " + groupsArr);

                        // здесь мы пушим на сервер токен
                        new LongOperation().execute(VKAccessTokenRes.userId, VKAccessTokenRes.accessToken, groupsArr);

                    }

                    @Override
                    public void onError(VKError error) {
                        //Do error stuff
                    }

                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                        //I don't really believe in progress
                    }
                });
            }

            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // отправка токена на сервак
    public void pushTokenOnServer(String id, String token, String groups) throws UnsupportedEncodingException {
//        Log.d(LOG_TAG, "pushTokenOnServer");
        String query = "idvk=" + id + "&token=" + token + "&groupuser=" + groups;
//        String query = URLEncoder.encode("idvk=" + id + "&token=" + token + "&groupuser=" + groups, "UTF-8");
        Log.d(LOG_TAG, query);
        request("http://vkadveyj.bget.ru/reg.php?" + query);

        // идём на главное активити
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    private String request(String sUrl) {
        String answer = "";
        try {
            URL url = new URL(sUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("POST");
//            Log.d(LOG_TAG, "Connect");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            Log.d(LOG_TAG, readStream(in));
            answer = readStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            urlConnection.disconnect();
        }

        return answer;
    }

    private class LongOperation extends AsyncTask<String, Void, Void> {
        @Override
        // асинхронный код
        protected Void doInBackground(String... params) {
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
        protected void onPostExecute(Void result) {
            Log.d(LOG_TAG, "post_async");
        }

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG, "pre_async");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d(LOG_TAG, "on_progress_async");
        }
    }

    // стрим в строку
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}