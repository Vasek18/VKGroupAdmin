package ru.tearum.vkgroupadmin;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Вася on 31.10.2015.
 */
public class Tools {

    public static final String SENDER_ID = "1084694217875";

    private static final String LOG_TAG = "myLogs";

    public Tools() {

    }

    // запрос на внешний сервак по урлу (так что поддерживает гет)
    public String request(String sUrl) {
        String answer = "";
        if (sUrl == null || sUrl.isEmpty()){
            Log.d(LOG_TAG, "Не пришёл урл");
            return null;
        }

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

    // стрим в строку
    public String readStream(InputStream is) {
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
