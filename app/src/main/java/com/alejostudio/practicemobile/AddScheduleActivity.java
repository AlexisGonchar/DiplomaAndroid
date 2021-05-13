package com.alejostudio.practicemobile;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    Calendar time = Calendar.getInstance();
    TextView currentTime;
    ArrayList<CheckBox> daysOfWeek;
    Switch switchAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        currentTime = findViewById(R.id.textViewTimeSchedule);
        Button btnSetTime = findViewById(R.id.buttonSetTimeSchedule);
        Button btnSendSchedule = findViewById(R.id.buttonSendScheduleData);
        switchAction = findViewById(R.id.switchActionSchedule);

        daysOfWeek = new ArrayList<>();
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxSunday));
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxMonday));
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxTuesday));
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxWednesday));
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxThursday));
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxFriday));
        daysOfWeek.add((CheckBox) findViewById(R.id.checkBoxSaturday));

        setInitialDateTime();

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddScheduleActivity.this, t,
                        time.get(Calendar.HOUR_OF_DAY),
                        time.get(Calendar.MINUTE), true)
                        .show();
            }
        });
        
        btnSendSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> checkedDaysOfWeek = getCheckedDaysOfWeek();
                if(checkedDaysOfWeek.size() != 0){
                    JSONObject json = new JSONObject();
                    try {
                        JSONArray arr = new JSONArray(checkedDaysOfWeek);
                        json.put("h", time.get(Calendar.HOUR_OF_DAY));
                        json.put("m", time.get(Calendar.MINUTE));
                        json.put("days", arr);
                        json.put("action", switchAction.isChecked());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Tools.toastShow(AddScheduleActivity.this, e.getMessage());
                    }
                    Tools.toastShow(AddScheduleActivity.this, json.toString());
                }
                else{
                    Tools.toastShow(AddScheduleActivity.this, "Дни недели не установлены!");
                }
            }
        });
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка даты и времени
    private void setInitialDateTime() {
        int hours = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        currentTime.setText(String.format("Время срабатывания: %02d:%02d", hours, minute));
    }
    
    private ArrayList<Integer> getCheckedDaysOfWeek(){
        ArrayList<Integer> checkedDaysOfWeek = new ArrayList<>();

        for (int i = 0; i < daysOfWeek.size(); i++) {
            if(daysOfWeek.get(i).isChecked()){
                checkedDaysOfWeek.add(i);
            }
        }
        return checkedDaysOfWeek;
    }
}
