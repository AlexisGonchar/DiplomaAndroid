package com.alejostudio.practicemobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ArrayList<Device> devices;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        token = preferences.getString("token", null);
        listView = findViewById(R.id.listViewDevices);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDevices();
            }
        });
        getDevices();
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.deleteContextMenuItem:
                clearData(info.position);
                return true;
            case R.id.aboutContextMenuItem:
                intent = new Intent(MainActivity.this, DeviceControlActivity.class);
                intent.putExtra("position", info.position);
                startActivity(intent);
                return true;
            case R.id.addConditionContextMenuItem:
                intent = new Intent(MainActivity.this, AddConditionActivity.class);
                intent.putExtra("position", info.position);
                startActivity(intent);
                return true;
            case R.id.addScheduleContextMenuItem:
                intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                intent.putExtra("position", info.position);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    void clearData(int position){
        String url = PropertiesManager.Host + "/clear-data";
        JSONObject json = new JSONObject();
        try {
            json.put("sid", token);
            json.put("did", devices.get(position).getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpSender.send(url, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Tools.toastShow(MainActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Tools.toastShow(MainActivity.this, response.body().string());
                getDevices();
            }
        });
    }

    void getDevices(){
        devices = new ArrayList<>();
        String url = PropertiesManager.Host + "/getdevices";
        JSONObject json = new JSONObject();
        try {
            json.put("sid", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpSender.send(url, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray devicesArray = jsonObject.getJSONArray("devices");
                    for(int i = 0; i < devicesArray.length(); i++){
                        JSONObject jsonOb = new JSONObject(devicesArray.get(i).toString());
                        String name = jsonOb.getString("name");
                        String id = jsonOb.getString("id");
                        Boolean status = jsonOb.getBoolean("status");
                        double lastActivity = jsonOb.getDouble("last-activity");
                        devices.add(new Device(name, id, status, lastActivity));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SetAdapter();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (JSONException e) {
                    Tools.toastShow(MainActivity.this, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.addNewDeviceMenuItem:
                intent = new Intent(MainActivity.this, AddingDeviceActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutMenuItem:
                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                preferences.edit().remove("token").apply();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void SetAdapter(){
        DevicesListAdapter adapter = new DevicesListAdapter(this, devices);
        listView.setAdapter(adapter);
    }
}
