package com.alejostudio.practicemobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    EditText inEmail, inPwd;
    Button buttonSendToAuth, buttonSwitchToReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = preferences.getString("token", null);
        if(token != null)
            StartMainActivity();
        inEmail = findViewById(R.id.editTextAuthEmail);
        inPwd = findViewById(R.id.editTextAuthPassword);
        buttonSendToAuth = findViewById(R.id.buttonAuth);
        buttonSwitchToReg = findViewById(R.id.buttonSwitchToReg);

        buttonSendToAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });

        buttonSwitchToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrateActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void StartMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void auth(){
        String url = PropertiesManager.Host + "/login";
        JSONObject json = new JSONObject();
        try {
            json.put("email", inEmail.getText());
            json.put("password", inPwd.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpSender.send(url, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Tools.toastShow(LoginActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    SharedPreferences preferences = LoginActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                    preferences.edit().putString("token", jsonObject.getString("sid")).apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Logged", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    Tools.toastShow(LoginActivity.this, e.getMessage());
                }
            }
        });
    }
}
