package com.alejostudio.practicemobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.*;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class AddingDeviceActivity extends AppCompatActivity {
    EditText editTextDeviceName, editTextSsid, editTextWiFiPass;
    String uuid, token;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_device);
        Button connect = (Button) findViewById(R.id.buttonConnectToDevice);
        Button buttonCreateDevice = (Button) findViewById(R.id.buttonCreateDevice);
        editTextDeviceName = findViewById(R.id.editTextDeviceName);
        editTextSsid = findViewById(R.id.editTextSsid);
        editTextWiFiPass = findViewById(R.id.editTextWiFiPass);
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        token = preferences.getString("token", null);
        uuid = UUID.randomUUID().toString();
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert mWifiManager != null;
        WifiInfo info = mWifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        editTextSsid.setText(ssid.substring(1, ssid.length() - 1));
        connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String ssid = editTextSsid.getText().toString();
                String pass = editTextWiFiPass.getText().toString();
                String url = "http://192.168.4.1/set_f?ssid=" + ssid + "&password=" + pass + "&did=" + uuid;
                OkHttpSender.send(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Tools.toastShow(AddingDeviceActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Tools.toastShow(AddingDeviceActivity.this, response.body().string());
                    }
                });
            }
        });

        buttonCreateDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = PropertiesManager.Host + "/createdev";
                JSONObject json = new JSONObject();
                try {
                    json.put("sid", token);
                    json.put("did", uuid);
                    json.put("name", editTextDeviceName.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttpSender.send(url, json.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Tools.toastShow(AddingDeviceActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Tools.toastShow(AddingDeviceActivity.this, response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(AddingDeviceActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void connectToWiFi(String SSID, String pass){
        Log.d("INFO", SSID);
        WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                .setSsid(SSID)
                .setWpa2Passphrase(pass)
                .build();
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build();
        final ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback());
    }

    OkHttpClient client = new OkHttpClient();

    public void run(String url, Callback callback) {

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
