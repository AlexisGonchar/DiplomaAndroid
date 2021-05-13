package com.alejostudio.practicemobile;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Tools {

    public static void toastShow(final AppCompatActivity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

            }
        });
    }

}
