package ru.tearum.vkgroupadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    public java.lang.String VK_USER_ID;

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
                String scope = ""; // ???
                VKSdk.login(this, scope);

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

                // идём на главное активити
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}