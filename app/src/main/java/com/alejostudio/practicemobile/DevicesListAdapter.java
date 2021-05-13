package com.alejostudio.practicemobile;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.alejostudio.practicemobile.Device;
import com.alejostudio.practicemobile.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class DevicesListAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Device> objects;
    MainActivity activity;

    DevicesListAdapter(Context context, ArrayList<Device> devices) {
        ctx = context;
        this.activity = (MainActivity)ctx;
        objects = devices;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.device_item, parent, false);
        }
        final Device d = (Device) getItem(position);
        ToggleButton tb = view.findViewById(R.id.toggleButtonItemSwitchStatusDevice);
        tb.setChecked(d.getStatus());
        TextView tv = view.findViewById(R.id.textViewItemNameDevice);
        TextView tvOnline = view.findViewById(R.id.textViewItemOnline);
        tvOnline.setText((System.currentTimeMillis() / 1000) - d.getLastActivity() < 5 ? "В сети" : "Не в сети");
        tv.setText(d.getName());
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStatus(activity.token, d.getId(), position);
            }
        });
        view.setLongClickable(true);
        /*sw.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearData(activity.token, d.getId());
                return false;
            }
        });*/
        return view;
    }



    void switchStatus(String token, String did, final int position){
        String url = PropertiesManager.Host + "/switch";
        JSONObject json = new JSONObject();
        try {
            json.put("sid", token);
            json.put("did", did);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpSender.send(url, json.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Tools.toastShow(activity, e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Tools.toastShow(activity, response.body().string());
            }
        });
    }
}