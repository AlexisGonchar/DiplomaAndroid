package com.alejostudio.practicemobile;

import android.graphics.Color;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AddConditionActivity extends AppCompatActivity {

    EditText editTextTemperature, editTextHumidity;
    TextView textViewTemperatureError, textViewHumidityError, textViewTemperatureLabel, textViewHumidityLabel;
    Switch switchAction, switchTemperatureAccounting, switchHumidityAccounting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_condition);
        Button btnSendConditionData = findViewById(R.id.buttonSendConditionData);
        editTextHumidity = findViewById(R.id.editTextHumidityCondition);
        editTextTemperature = findViewById(R.id.editTextTemperatureCondition);
        textViewHumidityError = findViewById(R.id.textViewHumidityInfoCondition);
        textViewTemperatureError = findViewById(R.id.textViewTemperatureInfoCondition);
        textViewTemperatureLabel = findViewById(R.id.textViewTemperatureCondition);
        textViewHumidityLabel = findViewById(R.id.textViewHumidityCondition);
        switchAction = findViewById(R.id.switchActionCondition);
        switchTemperatureAccounting = findViewById(R.id.switchTemperatureAccounting);
        switchHumidityAccounting = findViewById(R.id.switchHumidityAccounting);

        switchTemperatureAccounting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked && !switchHumidityAccounting.isChecked()){
                    switchHumidityAccounting.setChecked(true);
                }
                if(isChecked){
                    editTextTemperature.setEnabled(true);
                    textViewTemperatureError.setTextColor(Color.BLACK);
                    textViewTemperatureLabel.setTextColor(Color.BLACK);
                }else{
                    editTextTemperature.setEnabled(false);
                    textViewTemperatureError.setTextColor(Color.GRAY);
                    textViewTemperatureLabel.setTextColor(Color.GRAY);
                }
            }
        });

        switchHumidityAccounting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked && !switchTemperatureAccounting.isChecked()){
                    switchTemperatureAccounting.setChecked(true);
                }
                if(isChecked){
                    editTextHumidity.setEnabled(true);
                    textViewHumidityError.setTextColor(Color.BLACK);
                    textViewHumidityLabel.setTextColor(Color.BLACK);
                }else{
                    editTextHumidity.setEnabled(false);
                    textViewHumidityError.setTextColor(Color.GRAY);
                    textViewHumidityLabel.setTextColor(Color.GRAY);
                }
            }
        });

        btnSendConditionData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String strHumidity = editTextHumidity.getText().toString();
                    String strTemperature = editTextTemperature.getText().toString();
                    if(checkEmptyFields(strHumidity, strTemperature)){
                        if(checkTemperatureBorder(strTemperature)){
                            if(switchTemperatureAccounting.isChecked()) {
                                textViewTemperatureError.setTextColor(Color.BLACK);
                            }
                            if(checkHumidityBorder(strHumidity)){
                                if(switchHumidityAccounting.isChecked()){
                                    textViewHumidityError.setTextColor(Color.BLACK);
                                }
                                JSONObject json = new JSONObject();
                                if(switchTemperatureAccounting.isChecked()){
                                    int temperature = Integer.parseInt(strTemperature);
                                    json.put("t", temperature);
                                }else{
                                    json.put("t", JSONObject.NULL);
                                }
                                if(switchHumidityAccounting.isChecked()){
                                    int humidity = Integer.parseInt(strHumidity);
                                    json.put("h", humidity);
                                }else{
                                    json.put("h", JSONObject.NULL);
                                }
                                json.put("action", switchAction.isChecked());
                                Tools.toastShow(AddConditionActivity.this, json.toString());
                            }else{
                                textViewHumidityError.setTextColor(Color.RED);
                                Tools.toastShow(AddConditionActivity.this, "Влажность за пределами!");
                            }
                        }else{
                            textViewTemperatureError.setTextColor(Color.RED);
                            Tools.toastShow(AddConditionActivity.this, "Температура за пределами!");
                        }
                    }else{
                        Tools.toastShow(AddConditionActivity.this, "Данные не введены!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.toastShow(AddConditionActivity.this, e.getMessage());
                }
            }
        });
    }

    private boolean checkEmptyFields(String strHumidity, String strTemperature){
        boolean isOk = true;
        if(strHumidity.length() == 0 && switchHumidityAccounting.isChecked()){
            isOk = false;
        }
        if(strTemperature.length() == 0 && switchTemperatureAccounting.isChecked()){
            isOk = false;
        }
        return isOk;
    }

    private boolean checkHumidityBorder(String strHumidity){
        boolean isOk = true;
        if(switchHumidityAccounting.isChecked()){
            int humidity = Integer.parseInt(strHumidity);
            if(humidity > 100 || humidity < 0){
                isOk = false;
            }
        }
        return isOk;
    }

    private boolean checkTemperatureBorder(String strTemperature){
        boolean isOk = true;
        if(switchTemperatureAccounting.isChecked()){
            int temperature = Integer.parseInt(strTemperature);
            if(temperature < PropertiesManager.MinConditionTemperature ||
                    temperature > PropertiesManager.MaxConditionTemperature){
                isOk = false;
            }
        }
        return isOk;
    }
}
