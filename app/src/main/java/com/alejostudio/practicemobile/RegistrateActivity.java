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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrateActivity extends AppCompatActivity {

    EditText InName, InEmail, InPwd, InRepeatPwd;
    Button BtnSendToReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);
        InName = (EditText)findViewById(R.id.editTextRegName);
        InEmail = (EditText)findViewById(R.id.editTextRegEmail);
        InPwd = (EditText)findViewById(R.id.editTextRegPassword);
        InRepeatPwd = (EditText)findViewById(R.id.editTextRegRepeatPassword);
        BtnSendToReg = (Button)findViewById(R.id.buttonReg);
        BtnSendToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InPwd.getText().toString().equals(InRepeatPwd.getText().toString()))
                    registrate();
                else
                    Toast.makeText(RegistrateActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void registrate(){
        String url = PropertiesManager.Host + "/reg";
        JSONObject json = new JSONObject();
        try {
            json.put("name", InName.getText());
            json.put("email", InEmail.getText());
            json.put("password", InPwd.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpSender.send(url, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Tools.toastShow(RegistrateActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrateActivity.this, "Registrated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrateActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
