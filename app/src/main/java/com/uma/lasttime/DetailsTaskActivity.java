package com.uma.lasttime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_task);
    }

    public void renewTask(View v) {
        Toast toast;
        if (true) {
            toast = Toast.makeText(getApplicationContext(), R.string.msg_renew, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(getApplicationContext(), R.string.msg_error_on_renew, Toast.LENGTH_LONG);
        }
        toast.show();
    }
}