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
import com.vk.sdk.api.VKError;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    public java.lang.String VK_USER_ID;
    private static final String LOG_TAG = "myLogs";

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
//                String scope = ""; // ???
//                VKSdk.login(this, scope);

//                pushTokenOnServer();
                new LongOperation().execute();

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            // Пользователь успешно авторизовался
            public void onResult(VKAccessToken res) {
                VK_USER_ID = res.userId;

//                Log.d(LOG_TAG, "Token = " + res.accessToken);

                // идём на главное активити
//                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                startActivity(intent);
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
    public void pushTokenOnServer() {
        Log.d(LOG_TAG, "pushTokenOnServer");
        Log.d(LOG_TAG, request("http://av-promo.ru/http-test/test.php"));
    }

    private String request(String sUrl) {
        String answer = "";
        try {
            URL url = new URL("http://av-promo.ru/http-test/test.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
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

    private class LongOperation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            pushTokenOnServer();
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